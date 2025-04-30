package org.example.fifa.repository;

import org.example.fifa.Rest.PlayerRest;
import org.example.fifa.model.Player;
import org.example.fifa.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerRest> getAllPlayers() throws SQLException {
        return playerService.getAllPlayers();
    }
}
