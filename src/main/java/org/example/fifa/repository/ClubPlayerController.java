package org.example.fifa.repository;

import lombok.RequiredArgsConstructor;

import org.example.fifa.Mapper.PlayerRestMapper;
import org.example.fifa.Rest.PlayerRest;
import org.example.fifa.model.Player;
import org.example.fifa.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/clubs/{clubId}/players")
@RequiredArgsConstructor
public class ClubPlayerController {

    private final PlayerService playerService;
    private final PlayerRestMapper playerRestMapper;

    @GetMapping
    public List<PlayerRest> getPlayersByClub(@PathVariable String clubId) {
        try {
            List<Player> players = playerService.getPlayersByClubId(clubId);
            return playerRestMapper.toRestList(players);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching players", e);
        }
    }

    @PutMapping
    public List<PlayerRest> replacePlayersOfClub(@PathVariable String clubId, @RequestBody List<Player> players) {
        try {
            List<Player> updatedPlayers = playerService.replacePlayersOfClub(clubId, players);
            return playerRestMapper.toRestList(updatedPlayers);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException("Error replacing players of club", e);
        }
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<PlayerRest> addPlayersToClub(@PathVariable String clubId, @RequestBody List<Player> players) {
        try {
            List<Player> added = playerService.addPlayersToClub(clubId, players);
            return playerRestMapper.toRestList(added);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException("Error adding players to club", e);
        }
    }

}
