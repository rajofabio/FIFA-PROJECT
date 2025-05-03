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
public class ClubStatisticsDao {

    private final DataSource dataSource;

    public List<ClubStatistics> findStatisticsOrderedByName(String seasonYear) {
        String query = """
            SELECT c.id, c.name, c.acronym, c.year_creation, c.stadium,
                   c.coach_name, c.coach_nationality,
                   s.ranking_points, s.scored_goals, s.conceded_goals,
                   (s.scored_goals - s.conceded_goals) AS difference_goals,
                   s.clean_sheet_number
            FROM clubs c
            JOIN club_statistics s ON c.id = s.id_club
            WHERE s.season_year = ?
            ORDER BY c.name ASC
        """;

        return executeQuery(seasonYear, query);
    }

    public List<ClubStatistics> findStatisticsOrderedByRanking(String seasonYear) {
        String query = """
            SELECT c.id, c.name, c.acronym, c.year_creation, c.stadium,
                   c.coach_name, c.coach_nationality,
                   s.ranking_points, s.scored_goals, s.conceded_goals,
                   (s.scored_goals - s.conceded_goals) AS difference_goals,
                   s.clean_sheet_number
            FROM clubs c
            JOIN club_statistics s ON c.id = s.id_club
            WHERE s.season_year = ?
            ORDER BY s.ranking_points DESC,
                     difference_goals DESC,
                     s.clean_sheet_number DESC
        """;

        return executeQuery(seasonYear, query);
    }

    private List<ClubStatistics> executeQuery(String seasonYear, String sql) {
        List<ClubStatistics> results = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, seasonYear);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    results.add(ClubStatistics.builder()
                            .id(rs.getString("id"))
                            .name(rs.getString("name"))
                            .acronym(rs.getString("acronym"))
                            .yearCreation(rs.getInt("year_creation"))
                            .stadium(rs.getString("stadium"))
                            .coach(org.example.fifa.model.Coach.builder()
                                    .name(rs.getString("coach_name"))
                                    .nationality(rs.getString("coach_nationality"))
                                    .build())
                            .rankingPoints(rs.getInt("ranking_points"))
                            .scoredGoals(rs.getInt("scored_goals"))
                            .concededGoals(rs.getInt("conceded_goals"))
                            .differenceGoals(rs.getInt("difference_goals"))
                            .cleanSheetNumber(rs.getInt("clean_sheet_number"))
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching statistics for season: " + seasonYear, e);
        }
        return results;
    }
}
