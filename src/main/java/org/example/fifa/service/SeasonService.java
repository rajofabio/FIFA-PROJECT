package org.example.fifa.service;

import lombok.RequiredArgsConstructor;

import org.example.fifa.dao.SeasonDao;
import org.example.fifa.model.Season;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SeasonService {

    private final SeasonDao seasonDao;


    public List<Season> findAll() throws SQLException {
        return seasonDao.findAll();
    }
    public Season create(Season season) throws SQLException {
        return seasonDao.save(season);
    }
    public void updateStatusByYear(int year, Season.Status newStatus) throws SQLException {
        List<Season> all = seasonDao.findAll();
        for (Season season : all) {
            if (season.getYear() == year) {
                season.setStatus(newStatus);
                seasonDao.update(season);
                return;
            }
        }
        throw new IllegalArgumentException("Season with year " + year + " not found");
    }

}
