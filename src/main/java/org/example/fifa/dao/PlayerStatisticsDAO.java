package org.example.fifa.dao;

import lombok.RequiredArgsConstructor;
import org.example.fifa.model.PlayerStatistics;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlayerStatisticsDAO {
    private final DataSource dataSource;

    public Optional<PlayerStatistics> findByPlayerIdAndSeasonId(String playerId, String seasonId) throws SQLException {
        String query = """
            SELECT scored_goals, playing_time FROM player_statistics
            WHERE player_id = ? AND season_id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playerId);
            stmt.setString(2, seasonId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new PlayerStatistics(
                        playerId,
                        seasonId,
                        rs.getInt("scored_goals"),
                        rs.getInt("playing_time")
                ));
            } else {
                return Optional.empty();
            }
        }
    }

    public void save(PlayerStatistics stats) throws SQLException {
        String insert = """
            INSERT INTO player_statistics (player_id, season_id, scored_goals, playing_time)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (player_id, season_id) DO UPDATE SET
              scored_goals = excluded.scored_goals,
              playing_time = excluded.playing_time
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, stats.getPlayerId());
            stmt.setString(2, stats.getSeasonId());
            stmt.setInt(3, stats.getScoredGoals());
            stmt.setInt(4, stats.getPlayingTime());
            stmt.executeUpdate();
        }
    }
}