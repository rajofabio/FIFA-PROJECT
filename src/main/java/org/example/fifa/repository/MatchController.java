package org.example.fifa.repository;

import org.example.fifa.Rest.MatchRest;
import org.example.fifa.service.MatchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/matchMaker/{seasonYear}")
    public ResponseEntity<?> generateSeasonMatches(@PathVariable String seasonYear) {
        try {
            List<MatchRest> matches = matchService.generateSeasonMatches(seasonYear);
            return ResponseEntity.ok(matches);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error generating matches");
        }
    }
    @GetMapping("/matches/{seasonYear}")
    public ResponseEntity<List<MatchRest>> getMatchesBySeason(
            @PathVariable String seasonYear,
            @RequestParam(required = false) String matchStatus,
            @RequestParam(required = false) String clubPlayingName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime matchAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime matchBeforeOrEquals) {

        try {
            List<MatchRest> matches = matchService.getMatchesBySeason(
                    seasonYear,
                    matchStatus,
                    clubPlayingName,
                    matchAfter,
                    matchBeforeOrEquals
            );
            return ResponseEntity.ok(matches);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
