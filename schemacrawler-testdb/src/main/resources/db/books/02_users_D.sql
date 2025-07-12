-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Create Oracle user with just select catalog role
CREATE USER CATUSER IDENTIFIED BY CATUSER;
GRANT CONNECT TO CATUSER;
GRANT SELECT_CATALOG_ROLE TO CATUSER;


-- Create Oracle user with just SELECT grant
CREATE USER SELUSER IDENTIFIED BY SELUSER;
GRANT CONNECT TO SELUSER;
GRANT CREATE SESSION TO SELUSER;
-- Select grant is made in the grants script


-- Create Oracle user with no access
CREATE USER NOTUSER IDENTIFIED BY NOTUSER;
GRANT CONNECT TO NOTUSER;
