-- Enumerated Types
-- MySQL format

CREATE TABLE person
(
    name VARCHAR(40),
    tshirt ENUM('small', 'medium', 'large'),
    mood enum('sad', 'ok', 'happy')
)
;
