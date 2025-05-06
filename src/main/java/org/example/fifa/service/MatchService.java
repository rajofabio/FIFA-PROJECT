package org.example.fifa.service;

import org.example.fifa.Mapper.MatchRestMapper;
import org.example.fifa.Rest.MatchRest;
import org.example.fifa.dao.*;
import org.example.fifa.model.*;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MatchService {

    private final SeasonDao seasonDao;
    private final ClubDao clubDao;
    private final MatchDao matchDao;
    private final MatchRestMapper matchRestMapper;
    private final ClubStatisticsDao clubStatisticsDao;
    private final PlayerDao playerDao;
    private final PlayerStatisticsDAO playerStatisticsDAO;


    public MatchService(SeasonDao seasonDao, ClubDao clubDao,
                        MatchDao matchDao, MatchRestMapper matchRestMapper, ClubStatisticsDao clubStatisticsDao, PlayerDao playerDao, PlayerStatisticsDAO playerStatisticsDAO) {
        this.seasonDao = seasonDao;
        this.clubDao = clubDao;
        this.matchDao = matchDao;
        this.matchRestMapper = matchRestMapper;
        this.clubStatisticsDao = clubStatisticsDao;
        this.playerDao = playerDao;
        this.playerStatisticsDAO = playerStatisticsDAO;
    }

    public List<MatchRest> generateSeasonMatches(String seasonYear) throws SQLException {
        int year;
        try {
            year = Integer.parseInt(seasonYear);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid season year format");
        }

        Season season = seasonDao.findByYear(year);
        if (season == null) {
            throw new NoSuchElementException("Season not found");
        }

        if (season.getStatus() != Season.Status.STARTED) {
            throw new IllegalArgumentException("Season must be in STARTED status");
        }

        if (matchDao.existsBySeasonId(season.getId())) {
            throw new IllegalArgumentException("Matches already generated for this season");
        }


        List<Club> clubs = clubDao.findByChampionshipId(season.getChampionshipId());
        if (clubs.size() < 2) {
            throw new IllegalArgumentException("At least 2 clubs required");
        }


        List<Match> matches = new ArrayList<>();
        LocalDateTime matchDate = LocalDateTime.now().plusDays(7); // Start in 1 week

        for (int i = 0; i < clubs.size(); i++) {
            for (int j = i + 1; j < clubs.size(); j++) {
                Club home = clubs.get(i);
                Club away = clubs.get(j);


                Match match = new Match();
                match.setId(UUID.randomUUID().toString());
                match.setHomeClubId(home.getId());
                match.setAwayClubId(away.getId());
                match.setStadium(home.getStadium());
                match.setMatchDatetime(matchDate);
                match.setStatus(Match.Status.NOT_STARTED);
                match.setHomeScore(0);
                match.setAwayScore(0);
                match.setSeasonId(season.getId());
                match.setClubPlayingHome(home);
                match.setClubPlayingAway(away);

                matches.add(match);
                matchDate = matchDate.plusDays(7);
            }
        }


        matchDao.saveAll(matches);


        return matchRestMapper.toRestList(matches);
    }

    public List<MatchRest> getMatchesBySeason(
            String seasonYear,
            String matchStatus,
            String clubPlayingName,
            LocalDateTime matchAfter,
            LocalDateTime matchBeforeOrEquals) throws NoSuchElementException, SQLException {

        int year;
        try {
            year = Integer.parseInt(seasonYear);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }


        List<Match> matches = matchDao.findBySeasonWithFilters(
                year,
                matchStatus,
                clubPlayingName,
                matchAfter,
                matchBeforeOrEquals
        );

        if (matches.isEmpty()) {
            throw new NoSuchElementException();
        }

        return matchRestMapper.toRestList(matches);
    }

    public MatchRest updateMatchStatus(String matchId, Match.Status newStatus) throws SQLException {
        Match match = matchDao.findByIdWithClubs(matchId);
        if (match == null) {
            throw new NoSuchElementException();
        }

        if (!isValidStatusTransition(match.getStatus(), newStatus)) {
            throw new IllegalArgumentException();
        }

        match.setStatus(newStatus);

        if (newStatus == Match.Status.FINISHED) {
            updateClubStatistics(match);
        }

        matchDao.update(match);

        return matchRestMapper.toRest(match);
    }

    private boolean isValidStatusTransition(Match.Status currentStatus, Match.Status newStatus) {
        return (currentStatus == Match.Status.NOT_STARTED && newStatus == Match.Status.STARTED) ||
                (currentStatus == Match.Status.STARTED && newStatus == Match.Status.FINISHED) ||
                currentStatus == newStatus;
    }

    private void updateClubStatistics(Match match) throws SQLException {
        Season season = seasonDao.findById(match.getSeasonId());
        if (season == null) {
            throw new IllegalStateException();
        }
        String seasonYear = String.valueOf(season.getYear());


        ClubStatistics homeStats = clubStatisticsDao.findByClubAndSeason(match.getHomeClubId(), seasonYear);
        ClubStatistics awayStats = clubStatisticsDao.findByClubAndSeason(match.getAwayClubId(), seasonYear);


        homeStats.setScoredGoals(homeStats.getScoredGoals() + match.getHomeScore());
        homeStats.setConcededGoals(homeStats.getConcededGoals() + match.getAwayScore());
        homeStats.setDifferenceGoals(homeStats.getScoredGoals() - homeStats.getConcededGoals());

        awayStats.setScoredGoals(awayStats.getScoredGoals() + match.getAwayScore());
        awayStats.setConcededGoals(awayStats.getConcededGoals() + match.getHomeScore());
        awayStats.setDifferenceGoals(awayStats.getScoredGoals() - awayStats.getConcededGoals());

        if (match.getAwayScore() == 0) {
            homeStats.setCleanSheetNumber(homeStats.getCleanSheetNumber() + 1);
        }
        if (match.getHomeScore() == 0) {
            awayStats.setCleanSheetNumber(awayStats.getCleanSheetNumber() + 1);
        }


        if (match.getHomeScore() > match.getAwayScore()) {
            homeStats.setRankingPoints(homeStats.getRankingPoints() + 3);
        } else if (match.getHomeScore() < match.getAwayScore()) {
            awayStats.setRankingPoints(awayStats.getRankingPoints() + 3);
        } else {
            homeStats.setRankingPoints(homeStats.getRankingPoints() + 1);
            awayStats.setRankingPoints(awayStats.getRankingPoints() + 1);
        }


        clubStatisticsDao.save(homeStats, seasonYear);
        clubStatisticsDao.save(awayStats, seasonYear);
    }

    public MatchRest addGoalsToMatch(String matchId, List<GoalRequest> goalRequests) throws SQLException {
        Match match = matchDao.findByIdWithClubs(matchId);
        if (match == null) {
            throw new NoSuchElementException();
        }


        if (match.getStatus() != Match.Status.STARTED) {
            throw new IllegalArgumentException();
        }


        if (match.getHomeGoals() == null) {
            match.setHomeGoals(new ArrayList<>());
        }
        if (match.getAwayGoals() == null) {
            match.setAwayGoals(new ArrayList<>());
        }

        for (GoalRequest goalRequest : goalRequests) {
            Player player = findPlayer(goalRequest.getScorerIdentifier(), goalRequest.getClubId());
            if (player == null) {
                throw new NoSuchElementException();
            }

            Goal goal = Goal.builder()
                    .clubId(goalRequest.getClubId())
                    .scorer(player)
                    .minuteOfGoal(goalRequest.getMinuteOfGoal())
                    .ownGoal(goalRequest.isOwnGoal())
                    .build();
            goal.setMatchId(matchId);

            boolean isOwnGoal = goalRequest.isOwnGoal();
            String scoringClubId = isOwnGoal ?
                    (goalRequest.getClubId().equals(match.getHomeClubId()) ? match.getAwayClubId() : match.getHomeClubId())
                    : goalRequest.getClubId();


            if (scoringClubId.equals(match.getHomeClubId())) {
                match.getHomeGoals().add(goal);
                match.setHomeScore(match.getHomeScore() + 1);
            } else {
                match.getAwayGoals().add(goal);
                match.setAwayScore(match.getAwayScore() + 1);
            }

            if (!isOwnGoal) {
                updatePlayerStatistics(player.getId(), match.getSeasonId());
            }
        }


        matchDao.updateMatchWithGoals(match);

        return matchRestMapper.toRest(match);
    }
    private Player findPlayer(String identifier, String clubId) throws SQLException {
        Player player = playerDao.findById(identifier);
        if (player != null && player.getClubId().equals(clubId)) {
            return player;
        }
        try {
            int playerNumber = Integer.parseInt(identifier);
            return playerDao.findByNumberAndClub(playerNumber, clubId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private void updatePlayerStatistics(String playerId, String seasonId) throws SQLException {
        PlayerStatistics stats = playerStatisticsDAO
                .findByPlayerIdAndSeasonId(playerId, seasonId)
                .orElse(new PlayerStatistics(playerId, seasonId, 0, 0));

        stats.setScoredGoals(stats.getScoredGoals() + 1);
        playerStatisticsDAO.save(stats);
    }

}
