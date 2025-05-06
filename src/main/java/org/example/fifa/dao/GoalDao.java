package org.example.fifa.dao;

import lombok.RequiredArgsConstructor;
import org.example.fifa.model.Goal;
import org.example.fifa.model.Player;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GoalDao {

    private final DataSource dataSource;

    public List<Goal> findByMatchIdAndClubId(String matchId, String clubId) {
        String sql = """
        SELECT g.match_id, g.club_id, g.minute_of_goal, g.own_goal,
               p.id as player_id, p.name, p.number
        FROM goal g
        JOIN players p ON g.player_id = p.id
        WHERE g.match_id = ? AND g.club_id = ?
        """;

        List<Goal> goals = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, matchId);
            stmt.setString(2, clubId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Player player = Player.builder()
                        .id(rs.getString("player_id"))
                        .name(rs.getString("name"))
                        .number(rs.getInt("number"))
                        .build();

                Goal goal = Goal.builder()
                        .matchId(rs.getString("match_id"))
                        .clubId(rs.getString("club_id"))
                        .scorer(player)
                        .minuteOfGoal(rs.getInt("minute_of_goal"))
                        .ownGoal(rs.getBoolean("own_goal"))
                        .build();

                goals.add(goal);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch goals by match and club", e);
        }

        return goals;
    }
}
