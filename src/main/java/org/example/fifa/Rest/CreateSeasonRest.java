package org.example.fifa.Rest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSeasonRest {
    private int year;
    private String alias;
}