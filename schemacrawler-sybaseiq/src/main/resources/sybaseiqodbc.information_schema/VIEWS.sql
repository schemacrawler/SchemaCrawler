-- catalogs not supported in IQ ODBC Driver
select NULL AS TABLE_CATALOG, trim(vcreator) AS TABLE_SCHEMA, trim(viewname) AS TABLE_NAME, trim(viewtext) AS VIEW_DEFINITION
from sys.SYSVIEWS
