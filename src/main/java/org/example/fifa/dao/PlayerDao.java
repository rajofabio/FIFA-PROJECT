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
                Player player = new Player();
                player.setId(rs.getString("id"));
                player.setName(rs.getString("name"));
                player.setNumber(rs.getInt("number"));
                player.setPosition(Player.Position.valueOf(rs.getString("position")));
                player.setNationality(rs.getString("nationality"));
                player.setAge(rs.getInt("age"));
                player.setClubId(rs.getString("club_id"));
                players.add(player);
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
                Player player = new Player();
                player.setId(rs.getString("id"));
                player.setName(rs.getString("name"));
                player.setNumber(rs.getInt("number"));
                player.setPosition(Player.Position.valueOf(rs.getString("position")));
                player.setNationality(rs.getString("nationality"));
                player.setAge(rs.getInt("age"));
                player.setClubId(rs.getString("club_id"));
                players.add(player);
            }
        }
        return players;
    }
}