SELECT
  NULLIF(1, 1)
    AS VIEW_CATALOG,
	STRIP(SYSCAT.TABDEP.TABSCHEMA)
	  AS VIEW_SCHEMA,
	STRIP(SYSCAT.TABDEP.TABNAME)
   	AS VIEW_NAME,
  NULLIF(1, 1)
    AS TABLE_CATALOG,
	STRIP(SYSCAT.TABDEP.BSCHEMA)
	  AS TABLE_SCHEMA,
	STRIP(SYSCAT.TABDEP.BNAME)
	  AS TABLE_NAME
FROM
	SYSCAT.TABDEP
WHERE
	DTYPE = 'V'
	AND TABSCHEMA NOT LIKE 'SYS%'
ORDER BY
	VIEW_SCHEMA,
	VIEW_NAME,
	TABLE_NAME
WITH UR
