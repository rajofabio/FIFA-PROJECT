package org.example.fifa.dao;

import lombok.RequiredArgsConstructor;
import org.example.fifa.model.Club;
import org.example.fifa.model.Match;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MatchDao {
    private final DataSource dataSource;



    public void saveAll(List<Match> matches) throws SQLException {
        String sql = "INSERT INTO matches (id, home_club_id, away_club_id, stadium, " +
                "match_datetime, status, home_score, away_score, season_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (Match match : matches) {
                stmt.setString(1, match.getId());
                stmt.setString(2, match.getHomeClubId());
                stmt.setString(3, match.getAwayClubId());
                stmt.setString(4, match.getStadium());
                stmt.setObject(5, match.getMatchDatetime());
                stmt.setString(6, match.getStatus().name());
                stmt.setInt(7, match.getHomeScore());
                stmt.setInt(8, match.getAwayScore());
                stmt.setString(9, match.getSeasonId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        }
    }

    // Méthode pour mettre à jour un match
    public void update(Match match) throws SQLException {
        String sql = "UPDATE matches SET home_club_id = ?, away_club_id = ?, stadium = ?, match_datetime = ?, " +
                "status = ?, home_score = ?, away_score = ?, season_id = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, match.getHomeClubId());
            stmt.setString(2, match.getAwayClubId());
            stmt.setString(3, match.getStadium());
            stmt.setObject(4, match.getMatchDatetime());
            stmt.setString(5, match.getStatus().name());
            stmt.setInt(6, match.getHomeScore());
            stmt.setInt(7, match.getAwayScore());
            stmt.setString(8, match.getSeasonId());
            stmt.setString(9, match.getId());
            stmt.executeUpdate();
        }
    }


    // Trouver les clubs par championshipId
    public List<Club> findByChampionshipIdMatches(String championshipId) throws SQLException {
        String sql = "SELECT * FROM clubs WHERE championship_id = ?";
        List<Club> clubs = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, championshipId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Club club = new Club();
                club.setId(rs.getString("id"));
                club.setName(rs.getString("name"));
                club.setAcronym(rs.getString("acronym"));
                club.setStadium(rs.getString("stadium"));
                club.setChampionshipId(rs.getString("championship_id"));
                clubs.add(club);
            }
        }
        return clubs;
    }
    // Méthode pour vérifier si des matchs existent pour une saison donnée
    public boolean existsBySeasonId(String seasonId) {
        String sql = "SELECT COUNT(*) FROM matches WHERE season_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seasonId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if match exists for season", e);
        }
    }
    public List<Match> findBySeasonIdWithClubs(String seasonId) throws SQLException {
        String sql = """
            SELECT m.*, 
                   h.name as home_name, h.acronym as home_acronym, h.stadium as home_stadium,
                   a.name as away_name, a.acronym as away_acronym
            FROM matches m
            JOIN clubs h ON m.home_club_id = h.id
            JOIN clubs a ON m.away_club_id = a.id
            WHERE m.season_id = ?
            ORDER BY m.match_datetime
            """;

        List<Match> matches = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, seasonId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Match match = new Match();
                match.setId(rs.getString("id"));
                match.setHomeClubId(rs.getString("home_club_id"));
                match.setAwayClubId(rs.getString("away_club_id"));
                match.setStadium(rs.getString("stadium"));
                match.setMatchDatetime(rs.getTimestamp("match_datetime").toLocalDateTime());
                match.setStatus(Match.Status.valueOf(rs.getString("status")));
                match.setHomeScore(rs.getInt("home_score"));
                match.setAwayScore(rs.getInt("away_score"));
                match.setSeasonId(rs.getString("season_id"));

                // Home club
                Club homeClub = new Club();
                homeClub.setId(rs.getString("home_club_id"));
                homeClub.setName(rs.getString("home_name"));
                homeClub.setAcronym(rs.getString("home_acronym"));
                homeClub.setStadium(rs.getString("home_stadium"));
                match.setClubPlayingHome(homeClub);

                // Away club
                Club awayClub = new Club();
                awayClub.setId(rs.getString("away_club_id"));
                awayClub.setName(rs.getString("away_name"));
                awayClub.setAcronym(rs.getString("away_acronym"));
                match.setClubPlayingAway(awayClub);

                matches.add(match);
            }
        }
        return matches;
    }
    public List<Match> findBySeasonWithFilters(
            int seasonYear,
            String matchStatus,
            String clubPlayingName,
            LocalDateTime matchAfter,
            LocalDateTime matchBeforeOrEquals) throws SQLException {

        StringBuilder sql = new StringBuilder("""
        SELECT m.*, 
               h.name as home_name, h.acronym as home_acronym,
               a.name as away_name, a.acronym as away_acronym
        FROM matches m
        JOIN seasons s ON m.season_id = s.id
        JOIN clubs h ON m.home_club_id = h.id
        JOIN clubs a ON m.away_club_id = a.id
        WHERE s.year = ?
        """);

        List<Object> params = new ArrayList<>();
        params.add(seasonYear);

        // Filtre par statut
        if (matchStatus != null && !matchStatus.isEmpty()) {
            sql.append(" AND m.status = ?");
            params.add(matchStatus);
        }

        // Filtre par nom de club
        if (clubPlayingName != null && !clubPlayingName.isEmpty()) {
            sql.append(" AND (h.name LIKE ? OR a.name LIKE ?)");
            params.add("%" + clubPlayingName + "%");
            params.add("%" + clubPlayingName + "%");
        }

        // Filtre par date
        if (matchAfter != null) {
            sql.append(" AND m.match_datetime >= ?");
            params.add(matchAfter);
        }
        if (matchBeforeOrEquals != null) {
            sql.append(" AND m.match_datetime <= ?");
            params.add(matchBeforeOrEquals);
        }

        sql.append(" ORDER BY m.match_datetime");

        List<Match> matches = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Set des paramètres
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Match match = new Match();
                // Mapping des données comme dans les méthodes existantes
                matches.add(match);
            }
        }
        return matches;
    }
}