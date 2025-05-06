package org.example.fifa.Mapper;

import org.example.fifa.Rest.ClubRest;
import org.example.fifa.Rest.CoachRest;
import org.example.fifa.model.Club;
import org.example.fifa.model.Coach;

import org.springframework.stereotype.Component;

@Component
public class ClubRestMapper {

    public ClubRest toRest(Club club) {
        ClubRest rest = new ClubRest();
        rest.setId(club.getId());
        rest.setName(club.getName());
        rest.setAcronym(club.getAcronym());
        rest.setYearCreation(club.getYearCreation());
        rest.setStadium(club.getStadium());

        CoachRest coachRest = new CoachRest();
        coachRest.setName(club.getCoach().getName());
        coachRest.setNationality(club.getCoach().getNationality());

        rest.setCoach(coachRest);
        return rest;
    }

    public Club toDomain(ClubRest rest) {
        Coach coach = new Coach(rest.getCoach().getName(), rest.getCoach().getNationality());
        return new Club(
                rest.getId(),
                rest.getName(),
                rest.getAcronym(),
                rest.getYearCreation(),
                rest.getStadium(),
                coach,
                null
        );
    }

}