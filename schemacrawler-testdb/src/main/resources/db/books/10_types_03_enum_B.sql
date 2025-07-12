-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

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
