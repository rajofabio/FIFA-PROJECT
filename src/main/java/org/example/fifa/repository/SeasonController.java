package org.example.fifa.repository;

import lombok.RequiredArgsConstructor;

import org.example.fifa.Mapper.SeasonRestMapper;
import org.example.fifa.rest.SeasonRest;
import org.example.fifa.service.SeasonService;
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

    @GetMapping
    public List<SeasonRest> getAll() throws SQLException {
        return service.findAll().stream()
                .map(mapper::toRest)
                .collect(Collectors.toList());
    }
}
