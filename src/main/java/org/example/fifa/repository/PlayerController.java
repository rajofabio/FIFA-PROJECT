package org.example.fifa.repository;

import lombok.RequiredArgsConstructor;
import org.example.fifa.Rest.PlayerRest;
import org.example.fifa.model.Player;
import org.example.fifa.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping
    public List<PlayerRest> getAllPlayers() throws SQLException {
        return playerService.getAllPlayers();
    }

    @PutMapping
    public void saveAllPlayers(@RequestBody List<PlayerRest> playerRest) throws SQLException {
        playerService.saveAllPlayers(playerRest);
    }

}
