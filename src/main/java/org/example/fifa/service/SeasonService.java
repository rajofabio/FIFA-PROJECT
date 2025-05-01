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
}
