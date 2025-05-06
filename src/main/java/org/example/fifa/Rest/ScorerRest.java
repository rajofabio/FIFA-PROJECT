package org.example.fifa.Rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScorerRest {
    private PlayerScorerRest playerScorerRest;
    private int minuteOfGoal;
    private boolean ownGoal;
}
