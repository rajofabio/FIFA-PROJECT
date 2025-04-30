package org.example.fifa.service;

import org.example.fifa.dao.ClubDao;
import org.example.fifa.dao.MatchDao;
import org.example.fifa.dao.StatisticsDao;
import org.example.fifa.model.Club;
import org.example.fifa.model.Match;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service

public class MatchService {
    private final MatchDao matchDao;
    private final ClubDao clubDao;
    private final StatisticsDao statisticsDao;

    public MatchService(MatchDao matchDao, ClubDao clubDao, StatisticsDao statisticsDao) {
        this.matchDao = matchDao;
        this.clubDao = clubDao;
        this.statisticsDao = statisticsDao;
    }

    public void generateSeasonMatches(String seasonId) throws SQLException {
        List<Club> clubs = clubDao.findAll();

        if (clubs.isEmpty()) {
            throw new SQLException("No clubs found for generating matches.");
        }

        for (int i = 0; i < clubs.size(); i++) {
            for (int j = i + 1; j < clubs.size(); j++) {
                createMatch(seasonId, clubs.get(i), clubs.get(j));
                createMatch(seasonId, clubs.get(j), clubs.get(i));
            }
        }
    }

    private void createMatch(String seasonId, Club homeClub, Club awayClub) throws SQLException {
        Match match = new Match();
        match.setHomeClubId(homeClub.getId());
        match.setAwayClubId(awayClub.getId());
        match.setStadium(homeClub.getStadium());
        match.setSeasonId(seasonId);
        matchDao.save(match);
    }

    public void recordMatchResult(String matchId, int homeScore, int awayScore) throws SQLException {
        Optional<Match> matchOptional = Optional.ofNullable(matchDao.findById(matchId));

        if (matchOptional.isEmpty()) {
            throw new IllegalArgumentException("Match not found");
        }

        Match match = matchOptional.get();
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match.setStatus(Match.Status.FINISHED);
        matchDao.update(match);

        int homePoints = calculatePoints(homeScore, awayScore);
        int awayPoints = calculatePoints(awayScore, homeScore);


        updateStatistics(match, homePoints, awayPoints, homeScore, awayScore);
    }

    private int calculatePoints(int teamScore, int opponentScore) {
        if (teamScore > opponentScore) {
            return 3;
        } else if (teamScore == opponentScore) {
            return 1;
        } else {
            return 0;
        }
    }

    private void updateStatistics(Match match, int homePoints, int awayPoints, int homeScore, int awayScore) throws SQLException {

        statisticsDao.updateClubStats(
                match.getHomeClubId(),
                match.getSeasonId(),
                homePoints,
                homeScore,
                awayScore,
                awayScore == 0 ? 1 : 0
        );

        statisticsDao.updateClubStats(
                match.getAwayClubId(),
                match.getSeasonId(),
                awayPoints,
                awayScore,
                homeScore,
                homeScore == 0 ? 1 : 0
        );
    }
}

