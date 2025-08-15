-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  NULLIF(1, 1) 
    AS ROUTINE_CATALOG,
  STRIP(r.ROUTINESCHEMA) 
    AS ROUTINE_SCHEMA,
  STRIP(r.ROUTINENAME) 
    AS ROUTINE_NAME,
  STRIP(r.SPECIFICNAME) 
    AS SPECIFIC_NAME,
  NULLIF(1, 1) 
    AS REFERENCED_OBJECT_CATALOG,
  STRIP(t.TABSCHEMA) 
    AS REFERENCED_OBJECT_SCHEMA,
  STRIP(t.TABNAME) 
    AS REFERENCED_OBJECT_NAME,
  NULLIF(1, 1) 
    AS REFERENCED_OBJECT_SPECIFIC_NAME,
  CASE BTYPE
    WHEN 'T' THEN 'Table'
    WHEN 'V' THEN 'View'
    WHEN 'P' THEN 'Stored Procedure'
    WHEN 'F' THEN 'Function'
    WHEN 'M' THEN 'Module'
    WHEN 'R' THEN 'Sequence'
    WHEN 'G' THEN 'Global Variable'
    WHEN 'X' THEN 'Index'
    WHEN 'Y' THEN 'Trigger'
    WHEN 'Q' THEN 'Package'
    WHEN 'L' THEN 'Alias'
    WHEN 'S' THEN 'Synonym'
    WHEN 'C' THEN 'Type'
    WHEN 'D' THEN 'Data Type'
    WHEN 'B' THEN 'Bufferpool'
    ELSE 'Unknown'
  END
    AS REFERENCED_OBJECT_TYPE
FROM
  SYSCAT.ROUTINEDEP d
  INNER JOIN SYSCAT.ROUTINES r
    ON d.ROUTINESCHEMA = r.ROUTINESCHEMA
   AND d.ROUTINENAME = r.ROUTINENAME
  INNER JOIN SYSCAT.TABLES t
    ON d.BSCHEMA = t.TABSCHEMA 
   AND d.BNAME = t.TABNAME
WHERE
  d.BTYPE IN ('T', 'V', 'F', 'P')
  AND r.ROUTINETYPE IN ('F', 'P')
  AND r.ROUTINESCHEMA != 'SYSPROC'
ORDER BY
  r.ROUTINESCHEMA,
  r.ROUTINENAME,
  t.TABSCHEMA,
  t.TABNAME
WITH UR
