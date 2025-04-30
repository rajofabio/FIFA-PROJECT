package org.example.fifa.service;

import org.example.fifa.dao.SeasonDao;
import org.example.fifa.model.Season;

import java.sql.SQLException;
import java.util.List;

public class SeasonService {
    private final SeasonDao seasonDao;

    public SeasonService(SeasonDao seasonDao) {
        this.seasonDao = seasonDao;
    }

    public Season createSeason(int year, String alias) throws SQLException {
        Season season = new Season();
        season.setYear(year);
        season.setAlias(alias);
        return seasonDao.save(season);
    }

    public void startSeason(String seasonId) throws SQLException {
        Season season = seasonDao.findById(seasonId);
        season.setStatus(Season.Status.STARTED);
        seasonDao.update(season);
    }

    public List<Season> getAllSeasons() throws SQLException {
        return seasonDao.findAll();
    }
}
