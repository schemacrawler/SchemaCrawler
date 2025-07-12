-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Enumerated Types
-- MySQL format

CREATE TABLE person
(
    name VARCHAR(40),
    tshirt ENUM('small', 'medium', 'large'),
    mood enum('sad', 'ok', 'happy')
)
;
