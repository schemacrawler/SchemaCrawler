-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
    V.TABLE_CATALOG,
    V.TABLE_SCHEMA,
    V.TABLE_NAME,
    V.CHECK_OPTION,
    V.IS_UPDATABLE,
    OBJECT_DEFINITION(OBJECT_ID(V.TABLE_SCHEMA + '.' + V.TABLE_NAME)) 
      AS VIEW_DEFINITION
FROM
    INFORMATION_SCHEMA.VIEWS V
