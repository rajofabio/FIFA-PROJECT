package org.example.fifa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Club {
    private String id = UUID.randomUUID().toString();
    private String name;
    private String acronym;
    private int yearCreation;
    private String stadium;
    private Coach coach;
    private String championshipId;
}