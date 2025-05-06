package org.example.fifa.repository;

import org.example.fifa.Rest.MatchRest;
import org.example.fifa.model.GoalRequest;
import org.example.fifa.model.Match;
import org.example.fifa.service.MatchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @PutMapping("/matches/{id}/status")
    public ResponseEntity<?> updateMatchStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> requestBody) {

        try {
            String statusStr = requestBody.get("status");
            if (statusStr == null) {
                return ResponseEntity.badRequest().body("Status is required");
            }

            Match.Status newStatus;
            try {
                newStatus = Match.Status.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status value");
            }

            MatchRest updatedMatch = matchService.updateMatchStatus(id, newStatus);
            return ResponseEntity.ok(updatedMatch);

        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating match status");
        }


    }

    @RestController
    @RequestMapping("/matches/{matchId}/goals")
    public class MatchGoalsController {
        private final MatchService matchService;

        public MatchGoalsController(MatchService matchService) {
            this.matchService = matchService;
        }

        @PostMapping
        public ResponseEntity<?> addGoalsToMatch(
                @PathVariable String matchId,
                @RequestBody List<GoalRequest> goalRequests) {
            try {
                MatchRest matchRest = matchService.addGoalsToMatch(matchId, goalRequests);
                return ResponseEntity.ok(matchRest);
            } catch (NoSuchElementException e) {
                return ResponseEntity.notFound().build();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (SQLException e) {
                return ResponseEntity.internalServerError().body("Database error");
            }
        }
    }
}
