package org.example.fifa.Rest;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoachRest {
    private String name;
    private String nationality;
}
