-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Grants - Create additional user
-- SQL Server syntax
CREATE LOGIN OTHERUSER WITH PASSWORD = '0th3rU$3r4D@t@b@s3';
CREATE USER OTHERUSER FOR LOGIN OTHERUSER;
