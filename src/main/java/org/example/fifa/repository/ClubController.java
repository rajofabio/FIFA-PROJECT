package org.example.fifa.repository;

import lombok.RequiredArgsConstructor;

import org.example.fifa.Mapper.ClubRestMapper;
import org.example.fifa.Rest.ClubRest;
import org.example.fifa.dao.ClubDao;
import org.example.fifa.model.Club;
import org.example.fifa.service.ClubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final ClubRestMapper mapper;
    private final ClubRestMapper clubRestMapper;
    private final ClubDao clubDao;

    @GetMapping
    public List<ClubRest> getAll() throws SQLException {
        return clubService.findAll()
                .stream()
                .map(mapper::toRest)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubRest> getById(@PathVariable String id) throws SQLException {
        var club = clubService.findById(id);
        if (club == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toRest(club));
    }

    @PostMapping
    public ResponseEntity<ClubRest> create(@RequestBody ClubRest clubRest) throws SQLException {
        clubService.save(mapper.toDomain(clubRest));
        return ResponseEntity.ok().build();
    }
    @PutMapping
    public ResponseEntity<List<ClubRest>> upsertClubs(@RequestBody List<ClubRest> clubRests) throws SQLException {
        List<ClubRest> result = new ArrayList<>();

        for (ClubRest clubRest : clubRests) {
            Club club = clubRestMapper.toDomain(clubRest);
            Club upsertedClub = clubService.upsert(club);
            result.add(clubRestMapper.toRest(upsertedClub));
        }

        return ResponseEntity.ok(result);
    }
}
