package org.example.fifa.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Season {
    private String id = UUID.randomUUID().toString();
    private int year;
    private String alias;
    private Status status = Status.NOT_STARTED;
    private String championshipId;


    public enum Status {
        NOT_STARTED, STARTED, FINISHED ,ONGOING
    }
}
