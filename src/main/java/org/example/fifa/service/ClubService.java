package org.example.fifa.service;


import lombok.RequiredArgsConstructor;
import org.example.fifa.dao.ClubDao;
import org.example.fifa.model.Club;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubDao clubDao;

    public Club findById(String id) throws SQLException {
        return clubDao.findById(id);
    }
}
