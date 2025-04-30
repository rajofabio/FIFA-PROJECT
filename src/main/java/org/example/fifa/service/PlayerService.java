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
    public class PlayerService {

        private final PlayerDao playerDao;
        private final PlayerRestMapper playerRestMapper;

        public PlayerService(PlayerDao playerDao, PlayerRestMapper playerRestMapper) {
            this.playerDao = playerDao;
            this.playerRestMapper = playerRestMapper;

        }

        public List<PlayerRest> getAllPlayers() throws SQLException {
            return playerDao.findAll().stream()
                    .map(player -> playerRestMapper.toRest(player, player.getClub()))
                    .collect(Collectors.toList());
        }
    }
