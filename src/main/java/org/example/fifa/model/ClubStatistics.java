package org.example.fifa.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubStatistics {
    private String clubId;
    private String seasonId;
    private int rankingPoints;
    private int scoredGoals;
    private int concededGoals;
    private int cleanSheets;
}