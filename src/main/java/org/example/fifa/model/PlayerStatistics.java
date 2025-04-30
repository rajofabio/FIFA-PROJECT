package org.example.fifa.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatistics {
    private String playerId;
    private String seasonId;
    private int scoredGoals;
    private int playingTime;
}