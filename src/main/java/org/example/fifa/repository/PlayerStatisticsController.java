package org.example.fifa.repository;

import lombok.RequiredArgsConstructor;

import org.example.fifa.Mapper.PlayerStatisticsRestMapper;
import org.example.fifa.Rest.PlayerStatisticsRest;
import org.example.fifa.service.PlayerStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/players/{playerId}/statistics")
public class PlayerStatisticsController {

    private final PlayerStatisticsService service;
    private final PlayerStatisticsRestMapper mapper;

    @GetMapping("/{seasonYear}")
    public ResponseEntity<PlayerStatisticsRest> getStats(@PathVariable String playerId, @PathVariable String seasonYear) throws Exception {
        return service.getStatistics(playerId, seasonYear)
                .map(mapper::toRest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
