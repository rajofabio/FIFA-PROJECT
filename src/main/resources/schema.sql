-- Clubs table
CREATE TABLE IF NOT EXISTS clubs (
                                     id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    acronym VARCHAR(3) NOT NULL,
    year_creation INT NOT NULL,
    stadium VARCHAR(100) NOT NULL,
    coach_name VARCHAR(100) NOT NULL,
    coach_nationality VARCHAR(50) NOT NULL,
    championship_id VARCHAR(36) NOT NULL
    );

-- Players table
CREATE TABLE IF NOT EXISTS players (
                                       id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    number INT NOT NULL,
    position VARCHAR(20) NOT NULL,
    nationality VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    club_id VARCHAR(36),
    CONSTRAINT fk_club FOREIGN KEY (club_id) REFERENCES clubs(id)
    );

-- Seasons table
CREATE TABLE IF NOT EXISTS seasons (
                                       id VARCHAR(36) PRIMARY KEY,
    year INT NOT NULL,
    alias VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL
    );

-- Matches table
CREATE TABLE IF NOT EXISTS matches (
                                       id VARCHAR(36) PRIMARY KEY,
    home_club_id VARCHAR(36) NOT NULL,
    away_club_id VARCHAR(36) NOT NULL,
    stadium VARCHAR(100) NOT NULL,
    match_datetime TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    home_score INT DEFAULT 0,
    away_score INT DEFAULT 0,
    season_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_home_club FOREIGN KEY (home_club_id) REFERENCES clubs(id),
    CONSTRAINT fk_away_club FOREIGN KEY (away_club_id) REFERENCES clubs(id),
    CONSTRAINT fk_season FOREIGN KEY (season_id) REFERENCES seasons(id)
    );


