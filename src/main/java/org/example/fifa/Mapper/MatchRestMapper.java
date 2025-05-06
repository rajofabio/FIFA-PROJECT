package org.example.fifa.Mapper;

import org.example.fifa.Rest.*;
import org.example.fifa.model.Club;
import org.example.fifa.model.Match;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatchRestMapper {
    public List<MatchRest> toRestList(List<Match> matches) {
        return matches.stream()
                .map(this::toRest)
                .collect(Collectors.toList());
    }

    public MatchRest toRest(Match match) {
        MatchRest matchRest = new MatchRest();
        matchRest.setId(match.getId());
        matchRest.setStadium(match.getStadium());
        matchRest.setMatchDatetime(match.getMatchDatetime());
        matchRest.setActualStatus(match.getStatus().name());

        ClubWithScorersRest homeClub = new ClubWithScorersRest();
        homeClub.setId(match.getClubPlayingHome().getId());
        homeClub.setName(match.getClubPlayingHome().getName());
        homeClub.setAcronym(match.getClubPlayingHome().getAcronym());
        homeClub.setScore(0);
        homeClub.setScorers(createEmptyScorers());
        matchRest.setClubPlayingHome(homeClub);


        ClubWithScorersRest awayClub = new ClubWithScorersRest();
        awayClub.setId(match.getClubPlayingAway().getId());
        awayClub.setName(match.getClubPlayingAway().getName());
        awayClub.setAcronym(match.getClubPlayingAway().getAcronym());
        awayClub.setScore(0);
        awayClub.setScorers(createEmptyScorers());
        matchRest.setClubPlayingAway(awayClub);

        return matchRest;
    }

    private List<GoalScorerRest> createEmptyScorers() {
        GoalScorerRest scorer = new GoalScorerRest();
        scorer.setPlayer(new PlayerScorerRest("string", "string", 0));
        scorer.setMinuteOfGoal(0);
        scorer.setOwnGoal(true);
        return Collections.singletonList(scorer);
    }
    public Match toDomain(MatchRest matchRest) {
        Match match = new Match();
        match.setId(matchRest.getId());
        match.setStadium(matchRest.getStadium());
        match.setMatchDatetime(matchRest.getMatchDatetime());
        match.setStatus(Match.Status.valueOf(matchRest.getActualStatus()));


        match.setClubPlayingHome(new Club(matchRest.getClubPlayingHome().getId(), matchRest.getClubPlayingHome().getName(), matchRest.getClubPlayingHome().getAcronym()));
        match.setClubPlayingAway(new Club(matchRest.getClubPlayingAway().getId(), matchRest.getClubPlayingAway().getName(), matchRest.getClubPlayingAway().getAcronym()));

        return match;
    }

}