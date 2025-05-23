package org.example.fifa.dao;

import org.example.fifa.model.Club;
import lombok.RequiredArgsConstructor;
import org.example.fifa.model.Coach;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ClubDao {
    private final DataSource dataSource;

    public void save(Club club) throws SQLException {
        String sql = "INSERT INTO clubs (id, name, acronym, year_creation, stadium, coach_name, coach_nationality, championship_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, club.getId());
            stmt.setString(2, club.getName());
            stmt.setString(3, club.getAcronym());
            stmt.setInt(4, club.getYearCreation());
            stmt.setString(5, club.getStadium());
            stmt.setString(6, club.getCoach().getName());
            stmt.setString(7, club.getCoach().getNationality());
            stmt.setString(8, club.getChampionshipId());
            stmt.executeUpdate();
        }
    }

    public List<Club> findAll() throws SQLException {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT * FROM clubs";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Club club = new Club();
                club.setId(rs.getString("id"));
                club.setName(rs.getString("name"));
                club.setAcronym(rs.getString("acronym"));
                club.setYearCreation(rs.getInt("year_creation"));
                club.setStadium(rs.getString("stadium"));
                club.setCoach(new Coach(rs.getString("coach_name"), rs.getString("coach_nationality")));
                clubs.add(club);
            }
        }
        return clubs;
    }
    public Club findById(String id) throws SQLException {
        String sql = "SELECT * FROM clubs WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Club club = new Club();
                    club.setId(rs.getString("id"));
                    club.setName(rs.getString("name"));
                    club.setAcronym(rs.getString("acronym"));
                    club.setYearCreation(rs.getInt("year_creation"));
                    club.setStadium(rs.getString("stadium"));
                    club.setCoach(new Coach(
                            rs.getString("coach_name"),
                            rs.getString("coach_nationality")
                    ));
                    return club;
                } else {
                    return null;
                }
            }
        }
    }
    public Club update(Club club) throws SQLException {
        String sql = """
        UPDATE clubs
        SET name = ?, acronym = ?, year_creation = ?, stadium = ?, coach_name = ?, coach_nationality = ?
        WHERE id = ?
        """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getAcronym());
            stmt.setInt(3, club.getYearCreation());
            stmt.setString(4, club.getStadium());
            stmt.setString(5, club.getCoach().getName());
            stmt.setString(6, club.getCoach().getNationality());
            stmt.setString(7, club.getId());
            stmt.executeUpdate();
        }
        return club;
    }
    public List<Club> findByChampionshipId(String championshipId) throws SQLException {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT * FROM clubs WHERE championship_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, championshipId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Club club = new Club();
                    club.setId(rs.getString("id"));
                    club.setName(rs.getString("name"));
                    club.setAcronym(rs.getString("acronym"));
                    club.setYearCreation(rs.getInt("year_creation"));
                    club.setStadium(rs.getString("stadium"));
                    club.setCoach(new Coach(
                            rs.getString("coach_name"),
                            rs.getString("coach_nationality")
                    ));
                    clubs.add(club);
                }
            }
        }
        return clubs;
    }
    public boolean existsById(String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clubs WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public Object insert(Club club) throws SQLException {
        String sql = """
        INSERT INTO clubs (id, name, acronym, year_creation, stadium, coach_name, coach_nationality)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, club.getId());
            stmt.setString(2, club.getName());
            stmt.setString(3, club.getAcronym());
            stmt.setInt(4, club.getYearCreation());
            stmt.setString(5, club.getStadium());
            stmt.setString(6, club.getCoach().getName());
            stmt.setString(7, club.getCoach().getNationality());
            stmt.executeUpdate();
        }
        return null;
    }
    public Object upsert(Club club) throws SQLException {
        if (existsById(club.getId())) {
            return update(club);
        } else {
            return insert(club);
        }
    }

}