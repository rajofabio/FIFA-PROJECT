package org.example.fifa.dao;

import lombok.RequiredArgsConstructor;
import org.example.fifa.model.Player;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerDao {
    private final DataSource dataSource;

    public void save(Player player) throws SQLException {
        String sql = "INSERT INTO players (id, name, number, position, nationality, age, club_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getId());
            stmt.setString(2, player.getName());
            stmt.setInt(3, player.getNumber());
            stmt.setString(4, player.getPosition().name());
            stmt.setString(5, player.getNationality());
            stmt.setInt(6, player.getAge());
            stmt.setString(7, player.getClubId());
            stmt.executeUpdate();
        }
    }

    public List<Player> findByClubId(String clubId) throws SQLException {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE club_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clubId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
        }
        return players;
    }

    public List<Player> findAll() throws SQLException {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
        }
        return players;
    }

    public Player findById(String id) throws SQLException {
        String sql = "SELECT * FROM players WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayer(rs);
                }
            }
        }
        return null;
    }

    public void update(Player player) throws SQLException {
        String sql = "UPDATE players SET name = ?, number = ?, position = ?, nationality = ?, age = ?, club_id = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getName());
            stmt.setInt(2, player.getNumber());
            stmt.setString(3, player.getPosition().name());
            stmt.setString(4, player.getNationality());
            stmt.setInt(5, player.getAge());
            stmt.setString(6, player.getClubId());
            stmt.setString(7, player.getId());
            stmt.executeUpdate();
        }
    }

    public void updateClubOnly(Player player) throws SQLException {
        String sql = "UPDATE players SET club_id = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getClubId());
            stmt.setString(2, player.getId());
            stmt.executeUpdate();
        }
    }

    public void detachPlayersFromClub(String clubId) throws SQLException {
        String sql = "UPDATE players SET club_id = NULL WHERE club_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clubId);
            stmt.executeUpdate();
        }
    }

    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setId(rs.getString("id"));
        player.setName(rs.getString("name"));
        player.setNumber(rs.getInt("number"));
        player.setPosition(Player.Position.valueOf(rs.getString("position")));
        player.setNationality(rs.getString("nationality"));
        player.setAge(rs.getInt("age"));
        player.setClubId(rs.getString("club_id"));
        return player;
    }
}
