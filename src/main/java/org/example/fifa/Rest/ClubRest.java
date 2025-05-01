package org.example.fifa.Rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClubRest {
    private String id;
    private String name;
    private String acronym;
    private int yearCreation;
    private String stadium;
    private CoachRest coach;
}
