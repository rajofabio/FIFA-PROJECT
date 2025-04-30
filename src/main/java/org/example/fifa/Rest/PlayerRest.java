package org.example.fifa.Rest;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerRest {
    private String id;
    private String name;
    private int number;
    private String position;
    private String nationality;
    private int age;
    private ClubRest club;
}

