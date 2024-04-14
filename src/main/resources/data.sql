MERGE INTO M01_MPA (M01_ID, M01_NAME)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO G01_GENRE (G01_ID, G01_NAME)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

MERGE INTO S01_STATUS_FRIENDS (S01_ID, S01_NAME)
    VALUES (1, 'В ожидании'),
           (2, 'Подтверждено');