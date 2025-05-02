package org.example.fifa.repository;

import lombok.RequiredArgsConstructor;
import org.example.fifa.Rest.PlayerRest;
import org.example.fifa.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @PutMapping
    public List<PlayerRest> saveAllPlayers(@RequestBody List<PlayerRest> playerRest) throws SQLException {
        playerService.saveAllPlayers(playerRest);
        return playerRest;
    }

    @GetMapping
    public List<PlayerRest> getPlayersFiltered(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer ageMinimum,
            @RequestParam(required = false) Integer ageMaximum,
            @RequestParam(required = false) String clubName
    ) throws SQLException {
        return playerService.searchPlayers(name, ageMinimum, ageMaximum, clubName);
    }

}
