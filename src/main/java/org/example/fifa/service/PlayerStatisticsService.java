package org.example.fifa.service;


import lombok.RequiredArgsConstructor;

import org.example.fifa.dao.PlayerStatisticsDAO;
import org.example.fifa.model.PlayerStatistics;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerStatisticsService {

    private final PlayerStatisticsDAO statsCrud;

    public Optional<PlayerStatistics> getStatistics(String playerId, String seasonYear) throws SQLException {
        return statsCrud.findByPlayerIdAndSeasonYear(playerId, seasonYear);
    }
}
