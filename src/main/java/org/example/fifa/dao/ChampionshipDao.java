package org.example.fifa.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class ChampionshipDao {
    private final DataSource dataSource;

    // Implémentez les méthodes spécifiques aux championnats
}