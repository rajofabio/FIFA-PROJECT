package org.example.fifa.Rest;


import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClubWithScorersRest {
    String id;
    String name;
    String acronym;
    int score;
    List<GoalScorerRest> scorers;
}