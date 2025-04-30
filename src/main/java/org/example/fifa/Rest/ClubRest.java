package org.example.fifa.Rest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubRest {
    private String id;
    private String name;
    private String acronym;
    private int yearCreation;
    private String stadium;
    private CoachRest coach;
}
