package org.example.fifa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {
    private String matchId;
    private String clubId;
    private Player scorer;
    private int minuteOfGoal;
    private boolean ownGoal;

}
