-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- PostgreSQL syntax
SET CLIENT_MIN_MESSAGES TO WARNING;
DROP SCHEMA IF EXISTS BOOKS CASCADE;
CREATE SCHEMA BOOKS;
SET SEARCH_PATH TO BOOKS, PUBLIC;
