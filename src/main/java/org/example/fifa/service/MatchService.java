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

    public MatchService(SeasonDao seasonDao, ClubDao clubDao,
                        MatchDao matchDao, MatchRestMapper matchRestMapper) {
        this.seasonDao = seasonDao;
        this.clubDao = clubDao;
        this.matchDao = matchDao;
        this.matchRestMapper = matchRestMapper;
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
}
