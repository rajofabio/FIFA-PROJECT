package org.example.fifa.dao;

import lombok.RequiredArgsConstructor;
import org.example.fifa.model.ClubStatistics;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatisticsDao {
    private final DataSource dataSource;

    public void updateClubStatistics(ClubStatistics stats) throws SQLException {
        String sql = """
            INSERT INTO club_statistics 
            (club_id, season_id, ranking_points, scored_goals, conceded_goals, clean_sheets) 
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (club_id, season_id) 
            DO UPDATE SET 
                ranking_points = club_statistics.ranking_points + ?,
                scored_goals = club_statistics.scored_goals + ?,
                conceded_goals = club_statistics.conceded_goals + ?,
                clean_sheets = club_statistics.clean_sheets + ?
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, stats.getClubId());
            stmt.setString(2, stats.getSeasonId());
            stmt.setInt(3, stats.getRankingPoints());
            stmt.setInt(4, stats.getScoredGoals());
            stmt.setInt(5, stats.getConcededGoals());
            stmt.setInt(6, stats.getCleanSheets());
            stmt.setInt(7, stats.getRankingPoints());
            stmt.setInt(8, stats.getScoredGoals());
            stmt.setInt(9, stats.getConcededGoals());
            stmt.setInt(10, stats.getCleanSheets());
            stmt.executeUpdate();
        }
    }

    public List<ClubStatistics> getClubStatistics(String seasonId, boolean orderByRanking) throws SQLException {
        List<ClubStatistics> stats = new ArrayList<>();
        String sql = "SELECT * FROM club_statistics WHERE season_id = ? " +
                (orderByRanking ? "ORDER BY ranking_points DESC, (scored_goals - conceded_goals) DESC, clean_sheets DESC"
                        : "ORDER BY club_id");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seasonId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ClubStatistics stat = new ClubStatistics();
                stat.setClubId(rs.getString("club_id"));
                stat.setSeasonId(rs.getString("season_id"));
                stat.setRankingPoints(rs.getInt("ranking_points"));
                stat.setScoredGoals(rs.getInt("scored_goals"));
                stat.setConcededGoals(rs.getInt("conceded_goals"));
                stat.setCleanSheets(rs.getInt("clean_sheets"));
                stats.add(stat);
            }
        }
        return stats;
    }
}