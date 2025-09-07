-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
    R.ROUTINE_CATALOG,
    R.ROUTINE_SCHEMA,
    R.ROUTINE_NAME,
    R.SPECIFIC_NAME,
    R.ROUTINE_BODY,
    R.ROUTINE_DEFINITION
FROM
    INFORMATION_SCHEMA.ROUTINES R
