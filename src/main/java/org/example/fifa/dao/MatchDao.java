package org.example.fifa.dao;


import lombok.RequiredArgsConstructor;
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

    public void save(Match match) throws SQLException {
        String sql = "INSERT INTO matches (id, home_club_id, away_club_id, stadium, match_datetime, status, home_score, away_score, season_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, match.getId());
            stmt.setString(2, match.getHomeClubId());
            stmt.setString(3, match.getAwayClubId());
            stmt.setString(4, match.getStadium());
            stmt.setObject(5, match.getMatchDatetime());
            stmt.setString(6, match.getStatus().name());
            stmt.setInt(7, match.getHomeScore());
            stmt.setInt(8, match.getAwayScore());
            stmt.setString(9, match.getSeasonId());
            stmt.executeUpdate();
        }
    }

    public List<Match> findBySeasonId(String seasonId) throws SQLException {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE season_id = ?";
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
                match.setMatchDatetime(rs.getObject("match_datetime", LocalDateTime.class));
                match.setStatus(Match.Status.valueOf(rs.getString("status")));
                match.setHomeScore(rs.getInt("home_score"));
                match.setAwayScore(rs.getInt("away_score"));
                match.setSeasonId(rs.getString("season_id"));
                matches.add(match);
            }
        }
        return matches;
    }
    public Match findById(String matchId) throws SQLException {
        String sql = "SELECT * FROM matches WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, matchId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Match match = new Match();
                // ... initialisation comme dans findBySeasonId
                return match;
            }
        }
        return null;
    }

    public void updateMatchStatus(String matchId, Match.Status status) throws SQLException {
        String sql = "UPDATE matches SET status = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, matchId);
            stmt.executeUpdate();
        }
    }


    public void update(Match match) throws SQLException {
        String sql = "UPDATE matches SET home_club_id = ?, away_club_id = ?, stadium = ?, match_datetime = ?, " +
                "status = ?, home_score = ?, away_score = ?, season_id = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, match.getHomeClubId());
            stmt.setString(2, match.getAwayClubId());
            stmt.setString(3, match.getStadium());
            stmt.setObject(4, match.getMatchDatetime()); // Utilisation de LocalDateTime
            stmt.setString(5, match.getStatus().name());
            stmt.setInt(6, match.getHomeScore());
            stmt.setInt(7, match.getAwayScore());
            stmt.setString(8, match.getSeasonId());
            stmt.setString(9, match.getId()); // L'ID est utilisé dans la clause WHERE

            stmt.executeUpdate(); // Exécution de la mise à jour
        }
    }

}