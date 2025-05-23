package org.example.fifa.dao;

import lombok.RequiredArgsConstructor;
import org.example.fifa.model.Season;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeasonDao {
    private final DataSource dataSource;

    public Season save(Season season) throws SQLException {
        String sql = "INSERT INTO seasons (id, year, alias, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, season.getId());
            stmt.setInt(2, season.getYear());
            stmt.setString(3, season.getAlias());
            stmt.setString(4, season.getStatus().name());
            stmt.executeUpdate();
        }
        return season;
    }

    public List<Season> findAll() throws SQLException {
        List<Season> seasons = new ArrayList<>();
        String sql = "SELECT * FROM seasons";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Season season = new Season();
                season.setId(rs.getString("id"));
                season.setYear(rs.getInt("year"));
                season.setAlias(rs.getString("alias"));
                season.setStatus(Season.Status.valueOf(rs.getString("status")));
                seasons.add(season);
            }
        }
        return seasons;
    }

    public Season findById(String id) throws SQLException {
        String sql = "SELECT * FROM seasons WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Season season = new Season();
                season.setId(rs.getString("id"));
                season.setYear(rs.getInt("year"));
                season.setAlias(rs.getString("alias"));
                season.setStatus(Season.Status.valueOf(rs.getString("status")));
                return season;
            }
        }
        return null;
    }


    public void update(Season season) throws SQLException {
        String sql = "UPDATE seasons SET year = ?, alias = ?, status = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, season.getYear());
            stmt.setString(2, season.getAlias());
            stmt.setString(3, season.getStatus().name());
            stmt.setString(4, season.getId());
            stmt.executeUpdate();
        }
    }

    public Season findByYear(int year) throws SQLException {
        String sql = "SELECT * FROM seasons WHERE year = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Season season = new Season();
                season.setId(rs.getString("id"));
                season.setYear(rs.getInt("year"));
                season.setStatus(Season.Status.valueOf(rs.getString("status")));
                season.setChampionshipId(rs.getString("championship_id"));
                return season;
            }
            return null;
        }


    }

}
