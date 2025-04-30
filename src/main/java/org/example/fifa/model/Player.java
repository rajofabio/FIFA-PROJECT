package org.example.fifa.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private String id = UUID.randomUUID().toString();
    private String name;
    private int number;
    private Position position;
    private String nationality;
    private int age;
    private String clubId;

    public enum Position {
        STRIKER, MIDFIELDER, DEFENSE, GOAL_KEEPER
    }
}