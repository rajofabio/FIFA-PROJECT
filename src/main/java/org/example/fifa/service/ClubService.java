package org.example.fifa.service;

import lombok.RequiredArgsConstructor;
import org.example.fifa.dao.ClubDao;
import org.example.fifa.model.Club;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubDao clubDao;

    public List<Club> findAll() throws SQLException {
        return clubDao.findAll();
    }

    public Club findById(String id) throws SQLException {
        return clubDao.findById(id);
    }

    public void save(Club club) throws SQLException {
        clubDao.save(club);
    }
    public Club upsert(Club club) throws SQLException {
        return (Club) clubDao.upsert(club);
    }

}
