package org.example.fifa.Rest;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalScorerRest {
    PlayerScorerRest player;
    int minuteOfGoal;
    boolean ownGoal;
}