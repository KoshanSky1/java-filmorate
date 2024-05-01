DROP TABLE IF EXISTS F03_FRIENDS;
DROP TABLE IF EXISTS F02_FILM_GENRE;
DROP TABLE IF EXISTS L01_LIKES_FILM;
DROP TABLE IF EXISTS R02_REVIEWS_LIKES;
DROP TABLE IF EXISTS R01_REVIEWS;
DROP TABLE IF EXISTS F01_FILM;
DROP TABLE IF EXISTS G01_GENRE;
DROP TABLE IF EXISTS M01_MPA;
DROP TABLE IF EXISTS S01_STATUS_FRIENDS;
DROP TABLE IF EXISTS U01_USER;



CREATE TABLE IF NOT EXISTS U01_USER (
                                          U01_ID INTEGER    AUTO_INCREMENT NOT NULL PRIMARY KEY,
                                          U01_NAME VARCHAR(50) NOT NULL,
                                          U01_EMAIL VARCHAR(50) NOT NULL,
                                          U01_LOGIN VARCHAR(50) NOT NULL,
                                          U01_BIRTHDAY date NOT NULL
);

CREATE TABLE IF NOT EXISTS S01_STATUS_FRIENDS (
                                                    S01_ID INTEGER    AUTO_INCREMENT NOT NULL PRIMARY KEY,
                                                    S01_NAME VARCHAR(50)   NOT NULL
);

CREATE TABLE IF NOT EXISTS M01_MPA (
                                         M01_ID INTEGER    AUTO_INCREMENT NOT NULL PRIMARY KEY,
                                         M01_NAME VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS G01_GENRE (
                                           G01_ID INTEGER    AUTO_INCREMENT NOT NULL PRIMARY KEY,
                                           G01_NAME VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS F01_FILM (
                                          F01_ID INTEGER    AUTO_INCREMENT NOT NULL PRIMARY KEY,
                                          F01_NAME VARCHAR(50) NOT NULL,
                                          F01_DESCRIPTION VARCHAR(50) NOT NULL,
                                          F01_RELEASE_DATE date NOT NULL,
                                          F01_DURATION INTEGER NOT NULL,
                                          M01_ID INTEGER NOT NULL,
                                          FOREIGN KEY (M01_ID) REFERENCES M01_MPA(M01_ID)
);

CREATE TABLE IF NOT EXISTS L01_LIKES_FILM (
                                                L01_ID INTEGER   AUTO_INCREMENT NOT NULL PRIMARY KEY,
                                                F01_ID INTEGER NOT NULL,
                                                U01_ID INTEGER NOT NULL,
                                                FOREIGN KEY (F01_ID) REFERENCES F01_FILM(F01_ID),
                                                FOREIGN KEY (U01_ID) REFERENCES U01_USER(U01_ID)
);

CREATE TABLE IF NOT EXISTS F02_FILM_GENRE (
                                                F02_ID INTEGER   AUTO_INCREMENT NOT NULL PRIMARY KEY,
                                                G01_ID INTEGER NOT NULL,
                                                F01_ID INTEGER NOT NULL,
                                                FOREIGN KEY (F01_ID) REFERENCES F01_FILM(F01_ID),
                                                FOREIGN KEY (G01_ID) REFERENCES G01_GENRE(G01_ID)
);

CREATE TABLE IF NOT EXISTS F03_FRIENDS (
                                             F03_ID INTEGER   AUTO_INCREMENT NOT NULL PRIMARY KEY,
                                             U01_ID INTEGER NOT NULL,
                                             U01_ID_FRIEND INTEGER NOT NULL,
                                             S01_ID INTEGER NOT NULL,
                                             FOREIGN KEY (S01_ID) REFERENCES S01_STATUS_FRIENDS(S01_ID),
                                             FOREIGN KEY (U01_ID) REFERENCES U01_USER(U01_ID),
                                             FOREIGN KEY (U01_ID_FRIEND) REFERENCES U01_USER(U01_ID)
);
CREATE TABLE IF NOT EXISTS R01_REVIEWS (
                                             R01_ID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                             R01_CONTENT VARCHAR(100) NOT NULL,
                                             R01_IS_POSITIVE BOOLEAN NOT NULL,
                                             R01_USEFUL INTEGER NOT NULL,
                                             U01_ID INTEGER NOT NULL,
                                             F01_ID INTEGER NOT NULL,
                                             FOREIGN KEY (U01_ID) REFERENCES U01_USER(U01_ID),
                                             FOREIGN KEY (F01_ID) REFERENCES F01_FILM(F01_ID)
);
CREATE TABLE IF NOT EXISTS R02_REVIEWS_LIKES (
                                             R01_ID INTEGER NOT NULL,
                                             R02_IS_POSITIVE BOOLEAN NOT NULL,
                                             U01_ID INTEGER NOT NULL,
                                             FOREIGN KEY (R01_ID) REFERENCES R01_REVIEWS(R01_ID) ON DELETE CASCADE,
                                             FOREIGN KEY (U01_ID) REFERENCES U01_USER(U01_ID) ON DELETE CASCADE
);
