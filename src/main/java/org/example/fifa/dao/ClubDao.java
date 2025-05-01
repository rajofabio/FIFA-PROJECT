package org.example.fifa.dao;

import org.example.fifa.model.Club;
import lombok.RequiredArgsConstructor;
import org.example.fifa.model.Coach;
import org.example.fifa.model.Player;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public void update(Club club) throws SQLException {
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
    }

}