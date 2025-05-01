package org.example.fifa.Mapper;

import lombok.RequiredArgsConstructor;
import org.example.fifa.Rest.ClubRest;
import org.example.fifa.Rest.CoachRest;
import org.example.fifa.Rest.PlayerRest;
import org.example.fifa.model.Club;
import org.example.fifa.model.Coach;
import org.example.fifa.model.Player;
import org.example.fifa.service.ClubService;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class PlayerRestMapper {

    private final ClubService clubService;



    private ClubRest toRest(Club club) {
        return ClubRest.builder()
                .id(club.getId())
                .name(club.getName())
                .acronym(club.getAcronym())
                .yearCreation(club.getYearCreation())
                .stadium(club.getStadium())
                .coach(toRest(club.getCoach()))
                .build();
    }

    private CoachRest toRest(Coach coach) {
        return CoachRest.builder()
                .name(coach.getName())
                .nationality(coach.getNationality())
                .build();
    }public PlayerRest toRest(Player player, Club club) {
        return PlayerRest.builder()
                .id(player.getId())
                .name(player.getName())
                .number(player.getNumber())
                .position(player.getPosition().name())
                .nationality(player.getNationality())
                .age(player.getAge())
                .club(club != null ? toRest(club) : null)
                .build();
    }

}
