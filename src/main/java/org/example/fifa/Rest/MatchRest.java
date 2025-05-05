package org.example.fifa.Rest;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class MatchRest {
    String id;
    ClubWithScorersRest clubPlayingHome;
    ClubWithScorersRest clubPlayingAway;
    String stadium;
    LocalDateTime matchDatetime;
    String actualStatus;
}