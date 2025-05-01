package org.example.fifa.Mapper;

import org.example.fifa.Rest.DurationRest;
import org.example.fifa.Rest.PlayerStatisticsRest;
import org.example.fifa.model.PlayerStatistics;
import org.springframework.stereotype.Component;

@Component
public class PlayerStatisticsRestMapper {
    public PlayerStatisticsRest toRest(PlayerStatistics model) {
        PlayerStatisticsRest rest = new PlayerStatisticsRest();
        rest.setScoredGoals(model.getScoredGoals());
        rest.setPlayingTime(new DurationRest());
        rest.getPlayingTime().setValue(model.getPlayingTime());
        rest.getPlayingTime().setDurationUnit("SECOND");
        return rest;
    }
}
