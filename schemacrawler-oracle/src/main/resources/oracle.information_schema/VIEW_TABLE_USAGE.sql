SELECT /*+ PARALLEL(AUTO) */
  NULL AS VIEW_CATALOG,
  VIEWS.OWNER AS VIEW_SCHEMA,
  VIEWS.NAME AS VIEW_NAME,
  NULL AS TABLE_CATALOG,
  VIEWS.REFERENCED_OWNER AS TABLE_SCHEMA,
  VIEWS.REFERENCED_NAME AS TABLE_NAME
FROM
  ${catalogscope}_DEPENDENCIES VIEWS
WHERE
  VIEWS.TYPE = 'VIEW'
  AND VIEWS.OWNER NOT IN
    ('ANONYMOUS', 'APEX_PUBLIC_USER', 'APPQOSSYS', 'AUDSYS', 'BI', 'CTXSYS', 'DBSNMP', 'DIP', 
    'DVF', 'DVSYS', 'EXFSYS', 'FLOWS_30000', 'FLOWS_FILES', 'GSMADMIN_INTERNAL', 'HR', 'IX', 
    'LBACSYS', 'MDDATA', 'MDSYS', 'MGMT_VIEW', 'OE', 'OLAPSYS', 'ORACLE_OCM', 'ORDPLUGINS', 
    'ORDSYS', 'OUTLN', 'OWBSYS', 'PM', 'RDSADMIN', 'SCOTT', 'SH', 'SI_INFORMTN_SCHEMA', 
    'SPATIAL_CSW_ADMIN_USR', 'SPATIAL_WFS_ADMIN_USR', 'SYS', 'SYSMAN', 'TSMSYS', 'WKPROXY', 
    'WKSYS', 'WK_TEST', 'WMSYS', 'XDB', 'XS$NULL', 'SYSTEM')
  AND NOT REGEXP_LIKE(VIEWS.OWNER, '^APEX_[0-9]{6}$')
  AND NOT REGEXP_LIKE(VIEWS.OWNER, '^FLOWS_[0-9]{5,6}$')
  AND REGEXP_LIKE(VIEWS.OWNER, '${schemas}')
  AND VIEWS.NAME NOT LIKE 'BIN$%'
  AND NOT REGEXP_LIKE(VIEWS.NAME, '^(SYS_IOT|MDOS|MDRS|MDRT|MDOT|MDXT)_.*$')
