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


    public void updateClubStats(String homeClubId, String seasonId, int homePoints, int homeScore, int awayScore, int awayPoints) throws SQLException {
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

            // Mise à jour pour le club à domicile
            stmt.setString(1, homeClubId);
            stmt.setString(2, seasonId);
            stmt.setInt(3, homePoints);  // Points gagnés par le club à domicile
            stmt.setInt(4, homeScore);   // Nombre de buts marqués par le club à domicile
            stmt.setInt(5, awayScore);   // Nombre de buts encaissés par le club à domicile
            stmt.setInt(6, homeScore == 0 ? 1 : 0); // Clean sheet (si aucune goal n'a été encaissé)
            stmt.setInt(7, homePoints);  // Ajout des points obtenus par le club à domicile
            stmt.setInt(8, homeScore);   // Ajout des buts marqués par le club à domicile
            stmt.setInt(9, awayScore);   // Ajout des buts encaissés par le club à domicile
            stmt.setInt(10, homeScore == 0 ? 1 : 0); // Ajout du clean sheet
            stmt.executeUpdate();

            // Mise à jour pour le club à l'extérieur
            stmt.setString(1, homeClubId);
            stmt.setString(2, seasonId);
            stmt.setInt(3, homePoints);  // Points gagnés par le club à domicile
            stmt.setInt(4, homeScore);   // Nombre de buts marqués par le club à domicile
            stmt.setInt(5, awayScore);   // Nombre de buts encaissés par le club à domicile
            stmt.setInt(6, homeScore == 0 ? 1 : 0); // Clean sheet (si aucune goal n'a été encaissé)
            stmt.setInt(7, homePoints);  // Ajout des points obtenus par le club à domicile
            stmt.setInt(8, homeScore);   // Ajout des buts marqués par le club à domicile
            stmt.setInt(9, awayScore);   // Ajout des buts encaissés par le club à domicile
            stmt.setInt(10, homeScore == 0 ? 1 : 0); // Ajout du clean sheet
            stmt.executeUpdate();
        }
    }

}