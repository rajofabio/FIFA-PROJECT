package org.example.fifa.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Player {
    private String id = UUID.randomUUID().toString();
    private String name;
    private int number;
    private Position position;
    private String nationality;
    private int age;
    private String clubId;
    private Club club;




    public enum Position {
        STRIKER, MIDFIELDER, DEFENDER, GOALKEEPER
    }
}