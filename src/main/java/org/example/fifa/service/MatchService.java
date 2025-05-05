package org.example.fifa.service;

import org.example.fifa.Mapper.MatchRestMapper;
import org.example.fifa.Rest.MatchRest;
import org.example.fifa.Rest.PlayerStatisticsRest;
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
                        MatchDao matchDao, MatchRestMapper matchRestMapper , ClubStatisticsDao clubStatisticsDao , PlayerDao playerDao ,  PlayerStatisticsDAO playerStatisticsDAO)  {
        this.seasonDao = seasonDao;
        this.clubDao = clubDao;
        this.matchDao = matchDao;
        this.matchRestMapper = matchRestMapper;
        this.clubStatisticsDao = clubStatisticsDao;
        this.playerDao = playerDao;
        this.playerStatisticsDAO = playerStatisticsDAO;
    }

    public List<MatchRest> generateSeasonMatches(String seasonYear) throws SQLException {
        // Validation
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

        // Get clubs
        List<Club> clubs = clubDao.findByChampionshipId(season.getChampionshipId());
        if (clubs.size() < 2) {
            throw new IllegalArgumentException("At least 2 clubs required");
        }

        // Generate matches (only home matches as requested)
        List<Match> matches = new ArrayList<>();
        LocalDateTime matchDate = LocalDateTime.now().plusDays(7); // Start in 1 week

        for (int i = 0; i < clubs.size(); i++) {
            for (int j = i + 1; j < clubs.size(); j++) {
                Club home = clubs.get(i);
                Club away = clubs.get(j);

                // Create only home match
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
                matchDate = matchDate.plusDays(7); // Space matches by 1 week
            }
        }

        // Save to database
        matchDao.saveAll(matches);

        // Return formatted response
        return matchRestMapper.toRestList(matches);
    }

    public List<MatchRest> getMatchesBySeason(
            String seasonYear,
            String matchStatus,
            String clubPlayingName,
            LocalDateTime matchAfter,
            LocalDateTime matchBeforeOrEquals) throws NoSuchElementException, SQLException {

        // Validation de l'année
        int year;
        try {
            year = Integer.parseInt(seasonYear);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid season year format");
        }

        // Récupération des matchs avec filtres
        List<Match> matches = matchDao.findBySeasonWithFilters(
                year,
                matchStatus,
                clubPlayingName,
                matchAfter,
                matchBeforeOrEquals
        );

        if (matches.isEmpty()) {
            throw new NoSuchElementException("No matches found for season " + seasonYear);
        }

        return matchRestMapper.toRestList(matches);
    }
    // Dans MatchService.java
    public MatchRest updateMatchStatus(String matchId, Match.Status newStatus) throws SQLException {
        // Récupérer le match
        Match match = matchDao.findByIdWithClubs(matchId);
        if (match == null) {
            throw new NoSuchElementException("Match not found");
        }

        // Vérifier la transition de statut valide
        if (!isValidStatusTransition(match.getStatus(), newStatus)) {
            throw new IllegalArgumentException("Invalid status transition");
        }

        // Mettre à jour le statut
        match.setStatus(newStatus);

        // Si le nouveau statut est FINISHED, mettre à jour les statistiques des clubs
        if (newStatus == Match.Status.FINISHED) {
            updateClubStatistics(match);
        }

        // Sauvegarder les modifications
        matchDao.update(match);

        return matchRestMapper.toRest(match);
    }

    private boolean isValidStatusTransition(Match.Status currentStatus, Match.Status newStatus) {
        // Transition valide seulement dans l'ordre: NOT_STARTED -> STARTED -> FINISHED
        return (currentStatus == Match.Status.NOT_STARTED && newStatus == Match.Status.STARTED) ||
                (currentStatus == Match.Status.STARTED && newStatus == Match.Status.FINISHED) ||
                currentStatus == newStatus; // Permet de réassigner le même statut
    }

    private void updateClubStatistics(Match match) throws SQLException {
        // Récupérer la saison pour avoir l'année
        Season season = seasonDao.findById(match.getSeasonId());
        if (season == null) {
            throw new IllegalStateException("Season not found for match");
        }
        String seasonYear = String.valueOf(season.getYear());

        // Récupérer les statistiques des clubs pour la saison
        ClubStatistics homeStats = clubStatisticsDao.findByClubAndSeason(match.getHomeClubId(), seasonYear);
        ClubStatistics awayStats = clubStatisticsDao.findByClubAndSeason(match.getAwayClubId(), seasonYear);

        // Mettre à jour les buts marqués et encaissés
        homeStats.setScoredGoals(homeStats.getScoredGoals() + match.getHomeScore());
        homeStats.setConcededGoals(homeStats.getConcededGoals() + match.getAwayScore());
        homeStats.setDifferenceGoals(homeStats.getScoredGoals() - homeStats.getConcededGoals());

        awayStats.setScoredGoals(awayStats.getScoredGoals() + match.getAwayScore());
        awayStats.setConcededGoals(awayStats.getConcededGoals() + match.getHomeScore());
        awayStats.setDifferenceGoals(awayStats.getScoredGoals() - awayStats.getConcededGoals());

        // Mettre à jour les clean sheets
        if (match.getAwayScore() == 0) {
            homeStats.setCleanSheetNumber(homeStats.getCleanSheetNumber() + 1);
        }
        if (match.getHomeScore() == 0) {
            awayStats.setCleanSheetNumber(awayStats.getCleanSheetNumber() + 1);
        }

        // Déterminer le résultat et mettre à jour les points
        if (match.getHomeScore() > match.getAwayScore()) {
            // Victoire à domicile
            homeStats.setRankingPoints(homeStats.getRankingPoints() + 3);
        } else if (match.getHomeScore() < match.getAwayScore()) {
            // Victoire à l'extérieur
            awayStats.setRankingPoints(awayStats.getRankingPoints() + 3);
        } else {
            // Match nul
            homeStats.setRankingPoints(homeStats.getRankingPoints() + 1);
            awayStats.setRankingPoints(awayStats.getRankingPoints() + 1);
        }

        // Sauvegarder les statistiques mises à jour
        clubStatisticsDao.save(homeStats, seasonYear);
        clubStatisticsDao.save(awayStats, seasonYear);
    }
    // Dans MatchService.java
    public MatchRest addGoalsToMatch(String matchId, List<GoalRequest> goalRequests) throws SQLException {
        // Récupérer le match
        Match match = matchDao.findByIdWithClubs(matchId);
        if (match == null) {
            throw new NoSuchElementException("Match not found");
        }

        // Vérifier que le match est STARTED
        if (match.getStatus() != Match.Status.STARTED) {
            throw new IllegalArgumentException("Goals can only be added to matches with STARTED status");
        }

        // Traiter chaque but
        for (GoalRequest goalRequest : goalRequests) {
            // Trouver le joueur (par ID ou numéro)
            Player player = findPlayer(goalRequest.getScorerIdentifier(), goalRequest.getClubId());
            if (player == null) {
                throw new NoSuchElementException("Player not found: " + goalRequest.getScorerIdentifier());
            }

            // Créer le but
            Goal goal = Goal.builder()
                    .matchId(matchId)
                    .clubId(goalRequest.getClubId())
                    .scorer(player)
                    .minuteOfGoal(goalRequest.getMinuteOfGoal())
                    .ownGoal(false) // Par défaut, pas un but contre son camp
                    .build();

            // Ajouter le but au match
            if (goalRequest.getClubId().equals(match.getHomeClubId())) {
                match.getHomeGoals().add(goal);
                match.setHomeScore(match.getHomeScore() + 1);
            } else if (goalRequest.getClubId().equals(match.getAwayClubId())) {
                match.getAwayGoals().add(goal);
                match.setAwayScore(match.getAwayScore() + 1);
            } else {
                throw new IllegalArgumentException("Club ID doesn't match either home or away team");
            }

            // Mettre à jour les statistiques du joueur
            updatePlayerStatistics(player.getId(), match.getSeasonId());
        }

        // Sauvegarder les modifications
        matchDao.updateMatchWithGoals(match);

        return matchRestMapper.toRest(match);
    }

    private Player findPlayer(String identifier, String clubId) throws SQLException {
        try {
            // 1. Essayer de trouver par ID (texte)
            Player player = playerDao.findById(identifier);
            if (player != null && player.getClubId().equals(clubId)) {
                return player;
            }

            // 2. Si échec, essayer comme numéro (uniquement si c'est un nombre)
            try {
                int playerNumber = Integer.parseInt(identifier);
                return playerDao.findByNumberAndClub(playerNumber, clubId);
            } catch (NumberFormatException e) {
                return null; // Ni ID valide ni numéro valide
            }
        } catch (SQLException e) {
            throw new SQLException("Database error while finding player", e);
        }
    }
    private void updatePlayerStatistics(String playerId, String seasonId) throws SQLException {
        PlayerStatistics stats = playerStatisticsDAO.findByPlayerIdAndSeasonId(playerId, seasonId)
                .orElse(new PlayerStatistics(playerId, seasonId, 0, 0));

        stats.setScoredGoals(stats.getScoredGoals() + 1);
        playerStatisticsDAO.save(stats);
    }
}
