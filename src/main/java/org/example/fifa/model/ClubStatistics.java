package org.example.fifa.model;

import lombok.Builder;
import lombok.Data;
import org.example.fifa.model.Coach;

@Data
@Builder
public class ClubStatistics {
    private String id;
    private String name;
    private String acronym;
    private int yearCreation;
    private String stadium;
    private Coach coach;
    private int rankingPoints;
    private int scoredGoals;
    private int concededGoals;
    private int differenceGoals;
    private int cleanSheetNumber;
}
