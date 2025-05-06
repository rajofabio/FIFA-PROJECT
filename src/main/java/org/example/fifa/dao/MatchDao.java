package org.example.fifa.dao;

import lombok.RequiredArgsConstructor;
import org.example.fifa.model.Club;
import org.example.fifa.model.Coach;
import org.example.fifa.model.Goal;
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
    private final GoalDao goalDao;


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

    public List<Match> findBySeasonWithFilters(
            int seasonYear,
            String matchStatus,
            String clubPlayingName,
            LocalDateTime matchAfter,
            LocalDateTime matchBeforeOrEquals) throws SQLException {

        StringBuilder sql = new StringBuilder("""
    SELECT m.id, m.home_club_id, m.away_club_id, m.stadium, 
           m.match_datetime, m.status, m.home_score, m.away_score, m.season_id,
           h.id as home_id, h.name as home_name, h.acronym as home_acronym, h.stadium as home_stadium,
           a.id as away_id, a.name as away_name, a.acronym as away_acronym
    FROM matches m
    LEFT JOIN seasons s ON m.season_id = s.id
    LEFT JOIN clubs h ON m.home_club_id = h.id
    LEFT JOIN clubs a ON m.away_club_id = a.id
    WHERE s.year = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(seasonYear);


        if (matchStatus != null && !matchStatus.isEmpty()) {
            sql.append(" AND m.status = ?");
            params.add(matchStatus);
        }


        if (clubPlayingName != null && !clubPlayingName.isEmpty()) {
            sql.append(" AND (h.name LIKE ? OR a.name LIKE ?)");
            params.add("%" + clubPlayingName + "%");
            params.add("%" + clubPlayingName + "%");
        }


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


            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Match match = new Match();
                match.setId(rs.getString("id"));
                match.setHomeClubId(rs.getString("home_club_id"));
                match.setAwayClubId(rs.getString("away_club_id"));
                match.setStadium(rs.getString("stadium"));
                match.setMatchDatetime(rs.getTimestamp("match_datetime").toLocalDateTime());


                String status = rs.getString("status");
                match.setStatus(status != null ? Match.Status.valueOf(status) : Match.Status.NOT_STARTED);

                match.setHomeScore(rs.getInt("home_score"));
                match.setAwayScore(rs.getInt("away_score"));
                match.setSeasonId(rs.getString("season_id"));


                Club homeClub = new Club();
                if (rs.getString("home_id") != null) {
                    homeClub.setId(rs.getString("home_id"));
                    homeClub.setName(rs.getString("home_name"));
                    homeClub.setAcronym(rs.getString("home_acronym"));
                    homeClub.setStadium(rs.getString("home_stadium"));
                }
                match.setClubPlayingHome(homeClub);


                Club awayClub = new Club();
                if (rs.getString("away_id") != null) {
                    awayClub.setId(rs.getString("away_id"));
                    awayClub.setName(rs.getString("away_name"));
                    awayClub.setAcronym(rs.getString("away_acronym"));
                }
                match.setClubPlayingAway(awayClub);

                matches.add(match);
            }
        }
        return matches;
    }
    public Match findByIdWithClubs(String matchId) throws SQLException {
        String sql = """
    SELECT m.*, 
           h.id as home_id, h.name as home_name, h.acronym as home_acronym, h.stadium as home_stadium,
           h.coach_name as home_coach_name, h.coach_nationality as home_coach_nationality,
           a.id as away_id, a.name as away_name, a.acronym as away_acronym,
           a.coach_name as away_coach_name, a.coach_nationality as away_coach_nationality
    FROM matches m
    JOIN clubs h ON m.home_club_id = h.id
    JOIN clubs a ON m.away_club_id = a.id
    WHERE m.id = ?
    """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, matchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Match match = new Match();
                match.setId(rs.getString("id"));
                match.setHomeClubId(rs.getString("home_club_id"));
                match.setAwayClubId(rs.getString("away_club_id"));
                match.setStadium(rs.getString("stadium"));
                match.setMatchDatetime(rs.getTimestamp("match_datetime").toLocalDateTime());

                String statusStr = rs.getString("status");
                Match.Status status;
                try {
                    status = Match.Status.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    status = Match.Status.NOT_STARTED;
                }
                match.setStatus(status);

                match.setHomeScore(rs.getInt("home_score"));
                match.setAwayScore(rs.getInt("away_score"));
                match.setSeasonId(rs.getString("season_id"));


                List<Goal> homeGoals = goalDao.findByMatchIdAndClubId(matchId, match.getHomeClubId());
                List<Goal> awayGoals = goalDao.findByMatchIdAndClubId(matchId, match.getAwayClubId());
                match.setHomeGoals(homeGoals);
                match.setAwayGoals(awayGoals);


                Club homeClub = new Club();
                homeClub.setId(rs.getString("home_id"));
                homeClub.setName(rs.getString("home_name"));
                homeClub.setAcronym(rs.getString("home_acronym"));
                homeClub.setStadium(rs.getString("home_stadium"));
                homeClub.setCoach(new Coach(
                        rs.getString("home_coach_name"),
                        rs.getString("home_coach_nationality")
                ));
                match.setClubPlayingHome(homeClub);

                Club awayClub = new Club();
                awayClub.setId(rs.getString("away_id"));
                awayClub.setName(rs.getString("away_name"));
                awayClub.setAcronym(rs.getString("away_acronym"));
                awayClub.setCoach(new Coach(
                        rs.getString("away_coach_name"),
                        rs.getString("away_coach_nationality")
                ));
                match.setClubPlayingAway(awayClub);

                return match;
            }
            return null;
        }
    }

    public void updateMatchWithGoals(Match match) throws SQLException {
        String updateMatchSql = """
        UPDATE matches 
        SET home_score = ?, away_score = ?
        WHERE id = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateMatchSql)) {
            stmt.setInt(1, match.getHomeScore());
            stmt.setInt(2, match.getAwayScore());
            stmt.setString(3, match.getId());
            stmt.executeUpdate();
        }


        String insertGoalSql = """
        INSERT INTO goal (match_id, club_id, player_id, minute_of_goal, own_goal)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertGoalSql)) {


            for (Goal goal : match.getHomeGoals()) {
                stmt.setString(1, match.getId());
                stmt.setString(2, match.getHomeClubId());
                stmt.setString(3, goal.getScorer().getId());
                stmt.setInt(4, goal.getMinuteOfGoal());
                stmt.setBoolean(5, goal.isOwnGoal());
                stmt.addBatch();
            }


            for (Goal goal : match.getAwayGoals()) {
                stmt.setString(1, match.getId());
                stmt.setString(2, match.getAwayClubId());
                stmt.setString(3, goal.getScorer().getId());
                stmt.setInt(4, goal.getMinuteOfGoal());
                stmt.setBoolean(5, goal.isOwnGoal());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }
}
