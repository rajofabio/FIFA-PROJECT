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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerDao playerDao;
    private final ClubDao clubDao;
    private final PlayerRestMapper  playerRestMapper;

    public void saveAllPlayers(List<PlayerRest> playerRests) throws SQLException {
        for (PlayerRest rest : playerRests) {
            Player player = playerRestMapper.toDomain(rest);
            playerDao.save(player);
        }
    }

    public List<PlayerRest> searchPlayers(String name, Integer ageMinimum, Integer ageMaximum, String clubName) throws SQLException {
        List<Player> allPlayers = playerDao.findAll();

        return allPlayers.stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> ageMinimum == null || p.getAge() >= ageMinimum)
                .filter(p -> ageMaximum == null || p.getAge() <= ageMaximum)
                .filter(p -> {
                    if (clubName == null) return true;
                    try {
                        Club club = p.getClubId() != null ? clubDao.findById(p.getClubId()) : null;
                        return club != null && club.getName().toLowerCase().contains(clubName.toLowerCase());
                    } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la récupération du club", e);
                    }
                })
                .map(p -> {
                    try {
                        Club club = p.getClubId() != null ? clubDao.findById(p.getClubId()) : null;
                        return playerRestMapper.toRest(p, club);
                    } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la transformation du joueur", e);
                    }
                })
                .collect(Collectors.toList());
    }
    public List<Player> getPlayersByClubId(String clubId) throws SQLException {
        return playerDao.findByClubId(clubId);
    }public List<Player> replacePlayersOfClub(String clubId, List<Player> players) throws SQLException {
        if (clubId == null || players == null) {
            throw new IllegalArgumentException("Club ID or player list is null");
        }

        playerDao.detachPlayersFromClub(clubId);

        for (Player player : players) {
            player.setClubId(clubId);
            if (playerDao.findById(player.getId()) != null) {
                playerDao.updateClubOnly(player);
            } else {
                playerDao.save(player);
            }
        }
        return playerDao.findByClubId(clubId);
    }

    public List<Player> addPlayersToClub(String clubId, List<Player> players) throws SQLException {
        List<Player> result = new ArrayList<>();

        for (Player input : players) {
            Player existing = playerDao.findById(input.getId());

            if (existing == null) {
                input.setClubId(clubId);
                playerDao.save(input);
                result.add(input);
            } else {
                if (existing.getClubId() != null) {
                    throw new IllegalArgumentException("Player " + existing.getId() + " is already in a club");
                } else {
                    existing.setClubId(clubId);
                    playerDao.updateClubOnly(existing);
                    result.add(existing);
                }
            }
        }

        return result;
    }


    public void updatePlayer(Player player) throws SQLException {
        playerDao.update(player);
    }
}
