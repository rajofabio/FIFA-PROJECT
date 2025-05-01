package org.example.fifa.Mapper;

import lombok.RequiredArgsConstructor;
import org.example.fifa.model.Season;
import org.example.fifa.rest.SeasonRest;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
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
}
