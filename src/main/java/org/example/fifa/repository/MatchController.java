package org.example.fifa.repository;

import org.example.fifa.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/generate/{seasonId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void generateSeasonMatches(@PathVariable String seasonId) throws SQLException {
        matchService.generateSeasonMatches(seasonId);
    }

    @PutMapping("/result/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public void recordMatchResult(@PathVariable String matchId, @RequestParam int homeScore, @RequestParam int awayScore) throws SQLException {
        matchService.recordMatchResult(matchId, homeScore, awayScore);
    }
}
