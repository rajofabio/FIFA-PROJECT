package org.example.fifa.repository;

import lombok.RequiredArgsConstructor;
import org.example.fifa.model.ClubStatistics;
import org.example.fifa.service.ClubStatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ClubStatisticsController {

    private final ClubStatisticsService service;

    @GetMapping("/clubs/statistics/{seasonYear}")
    public List<ClubStatistics> getStatistics(
            @PathVariable String seasonYear,
            @RequestParam(defaultValue = "false") boolean hasToBeClassified
    ) {
        return service.getStatistics(seasonYear, hasToBeClassified);
    }
}
