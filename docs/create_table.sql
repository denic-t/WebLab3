-- SQL скрипт для создания таблицы результатов в Oracle
-- Выполните этот скрипт в вашей Oracle БД перед запуском приложения

-- Создание sequence для генерации ID
CREATE SEQUENCE CHECK_RESULTS_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Создание таблицы для хранения результатов проверки
CREATE TABLE CHECK_RESULTS (
    ID NUMBER PRIMARY KEY,
    X NUMBER(10, 4) NOT NULL,
    Y NUMBER(10, 4) NOT NULL,
    R NUMBER(10, 4) NOT NULL,
    HIT CHAR(1) NOT NULL CHECK (HIT IN ('Y', 'N')),
    EXECUTION_TIME NUMBER NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Индекс для сортировки по времени
CREATE INDEX IDX_RESULTS_CREATED ON CHECK_RESULTS(CREATED_AT DESC);

-- Проверка создания
SELECT * FROM CHECK_RESULTS;
