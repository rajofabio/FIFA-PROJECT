package org.example.fifa.service;

import lombok.RequiredArgsConstructor;
import org.example.fifa.dao.ClubStatisticsDao;
import org.example.fifa.model.ClubStatistics;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubStatisticsService {

    private final ClubStatisticsDao clubStatisticsDao;

    public List<ClubStatistics> getStatistics(String seasonYear, boolean hasToBeClassified) {
        return hasToBeClassified
                ? clubStatisticsDao.findStatisticsOrderedByRanking(seasonYear)
                : clubStatisticsDao.findStatisticsOrderedByName(seasonYear);
    }
}
