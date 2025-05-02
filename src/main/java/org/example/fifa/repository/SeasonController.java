package org.example.fifa.repository;

import lombok.RequiredArgsConstructor;

import org.example.fifa.Mapper.SeasonRestMapper;
import org.example.fifa.Rest.CreateSeasonRest;
import org.example.fifa.Rest.UpdateSeasonStatusRest;
import org.example.fifa.model.Season;
import org.example.fifa.rest.SeasonRest;
import org.example.fifa.service.SeasonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seasons")
public class SeasonController {

    private final SeasonService service;
    private final SeasonRestMapper mapper;
    private final SeasonService seasonService;
    private final SeasonRestMapper seasonRestMapper;

    @GetMapping
    public List<SeasonRest> getAll() throws SQLException {
        return service.findAll().stream()
                .map(mapper::toRest)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<List<SeasonRest>> createSeasons(@RequestBody List<CreateSeasonRest> requestList) throws SQLException {
        List<SeasonRest> created = requestList.stream()
                .map(seasonRestMapper::toDomain)
                .map(season -> {
                    try {
                        return seasonService.create(season);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(seasonRestMapper::toRest)
                .collect(Collectors.toList());

        return ResponseEntity.ok(created);
    }
    @PutMapping("/{seasonYear}/status")
    public ResponseEntity<SeasonRest> updateSeasonStatus(@PathVariable int seasonYear,
                                                         @RequestBody UpdateSeasonStatusRest request) throws SQLException {
        Season.Status newStatus = Season.Status.valueOf(request.getStatus());
        Season updated = seasonService.updateStatusByYear(seasonYear, newStatus);
        SeasonRest response = seasonRestMapper.toRest(updated);
        return ResponseEntity.ok(response);
    }
}
