package org.example.fifa.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private String id = UUID.randomUUID().toString();
    private String homeClubId;
    private String awayClubId;
    private String stadium;
    private LocalDateTime matchDatetime;
    private Status status = Status.NOT_STARTED;
    private int homeScore;
    private int awayScore;
    private String seasonId;

    public enum Status {
        NOT_STARTED, STARTED, FINISHED
    }
}