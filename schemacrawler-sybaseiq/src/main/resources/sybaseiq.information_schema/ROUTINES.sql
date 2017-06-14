select DB_NAME() AS ROUTINE_CATALOG, trim(usr.user_name) AS ROUTINE_SCHEMA, trim(prc.proc_name) AS ROUTINE_NAME, 'SQL' AS ROUTINE_BODY, trim(prc.source) AS ROUTINE_DEFINITION
from sys.SYSPROCEDURE prc
inner join sys.SYSUSER usr on usr.user_id = prc.creator
