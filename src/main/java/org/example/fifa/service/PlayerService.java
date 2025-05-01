package org.example.fifa.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.fifa.Mapper.PlayerRestMapper;
import org.example.fifa.Rest.PlayerRest;
import org.example.fifa.dao.ClubDao;
import org.example.fifa.dao.PlayerDao;
import org.example.fifa.model.Player;
import org.springframework.stereotype.Service;
import org.example.fifa.model.Club;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerDao playerDao;
    private final ClubDao clubDao;
    private final PlayerRestMapper  playerRestMapper;

    public List<PlayerRest> getAllPlayers() throws SQLException {
        List<Player> players = playerDao.findAll();

        return players.stream()
                .map(player -> {
                    try {
                        Club club = player.getClubId() != null ? clubDao.findById(player.getClubId()) : null;
                        return playerRestMapper.toRest(player, club);
                    } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors du chargement du club pour le joueur : " + player.getId(), e);
                    }
                })
                .collect(Collectors.toList());
    }
    public void saveAllPlayers(List<PlayerRest> playerRests) throws SQLException {
        for (PlayerRest rest : playerRests) {
            Player player = playerRestMapper.toDomain(rest);
            playerDao.save(player);
        }
    }

}
