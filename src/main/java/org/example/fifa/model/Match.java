package org.example.fifa.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Match {
    private String id;
    private String stadium;
    private LocalDateTime matchDatetime;

    private String homeClubId;
    private String awayClubId;
    private String seasonId;

    private Integer homeScore = 0;
    private Integer awayScore = 0;

    private Status status = Status.NOT_STARTED;


    private Club clubPlayingHome;
    private Club clubPlayingAway;
    private List<Goal> homeGoals;
    private List<Goal> awayGoals;

    public enum Status {
        NOT_STARTED, STARTED, FINISHED
    }
}
