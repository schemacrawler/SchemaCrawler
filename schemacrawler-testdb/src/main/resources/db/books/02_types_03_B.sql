-- Enumerated Types
-- PostgreSQL format

CREATE TYPE tshirt_type AS ENUM ('small', 'medium', 'large');
CREATE TYPE mood_type AS enum ('sad', 'ok', 'happy');

CREATE TABLE person
(
    name VARCHAR(40),
    tshirt tshirt_type,
    mood mood_type
)
;
