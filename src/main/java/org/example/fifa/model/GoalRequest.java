package org.example.fifa.model;// GoalRequest.java (nouvelle classe)


import lombok.Data;

@Data
public class GoalRequest {
    private String clubId;
    private String scorerIdentifier;
    private int minuteOfGoal;
    private boolean ownGoal;
}