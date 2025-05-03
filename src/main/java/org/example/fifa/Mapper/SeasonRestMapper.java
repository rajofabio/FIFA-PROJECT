package org.example.fifa.Mapper;

import org.example.fifa.Rest.CreateSeasonRest;
import org.example.fifa.Rest.SeasonRest;
import org.example.fifa.model.Season;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SeasonRestMapper {

    public SeasonRest toRest(Season model) {
        SeasonRest rest = new SeasonRest();
        rest.setId(model.getId());
        rest.setYear(model.getYear());
        rest.setAlias(model.getAlias());
        rest.setStatus(model.getStatus().name());
        return rest;
    }

    public Season toDomain(CreateSeasonRest rest) {
        Season season = new Season();
        season.setId(UUID.randomUUID().toString());
        season.setYear(rest.getYear());
        season.setAlias(rest.getAlias());
        season.setStatus(Season.Status.NOT_STARTED);
        return season;
    }
}
