-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT NAME, DESCRIPTION, VALUE
FROM (
    -- Compatibility Level of Current Database
    SELECT 'CompatibilityLevel' AS NAME,
           'Database compatibility level (e.g., 150 for SQL Server 2019)' AS DESCRIPTION,
           CAST(compatibility_level AS NVARCHAR(MAX)) AS VALUE
    FROM sys.databases
    WHERE name = DB_NAME()
    UNION ALL
    -- Compatibility Level of Current Database
    SELECT 'ServerLevel',
           'Databse server level (e.g., 150 for SQL Server 2019)',
           CAST(SERVERPROPERTY('ProductMajorVersion') AS NVARCHAR(3)) + 
		   CAST(SERVERPROPERTY('ProductMinorVersion') AS NVARCHAR(3))
    UNION ALL
    SELECT 'Collation'  AS PropertyName, 
	       'Server-level collation' AS Description,
           CAST(SERVERPROPERTY('Collation') AS NVARCHAR(MAX))  AS PropertyValue
    UNION ALL
    SELECT 'Edition', 'SQL Server edition (e.g., Enterprise, Standard)',
           CAST(SERVERPROPERTY('Edition') AS NVARCHAR(MAX))
    UNION ALL
    SELECT 'InstanceName', 'SQL Server instance name (null if default)',
           CAST(SERVERPROPERTY('InstanceName') AS NVARCHAR(MAX))
    UNION ALL
    SELECT 'IsAdvancedAnalyticsInstalled', 'Advanced Analytics (R/Python) installed (true/false)',
           IIF(SERVERPROPERTY('IsAdvancedAnalyticsInstalled') = 1, 'true', 'false')
    UNION ALL
    SELECT 'IsClustered', 'Clustered SQL Server instance (true/false)',
           IIF(SERVERPROPERTY('IsClustered') = 1, 'true', 'false')
    UNION ALL
    SELECT 'IsFilestreamEnabled', 'FILESTREAM feature enabled (true/false)',
           IIF(SERVERPROPERTY('IsFilestreamEnabled') = 1, 'true', 'false')
    UNION ALL
    SELECT 'IsFullTextInstalled', 'Full-Text Search installed (true/false)',
           IIF(SERVERPROPERTY('IsFullTextInstalled') = 1, 'true', 'false')
    UNION ALL
    SELECT 'IsHadrEnabled', 'Always On Availability Groups enabled (true/false)',
           IIF(SERVERPROPERTY('IsHadrEnabled') = 1, 'true', 'false')
    UNION ALL
    SELECT 'ProductBuild', 'Build number only',
           CAST(SERVERPROPERTY('ProductBuild') AS NVARCHAR(MAX))
    UNION ALL
    SELECT 'ProductLevel', 'Release level (RTM, SP1, CU18, etc.)',
           CAST(SERVERPROPERTY('ProductLevel') AS NVARCHAR(MAX))
    UNION ALL
    SELECT 'ProductMajorVersion', 'Major version number (e.g., 15 for SQL Server 2019)',
           CAST(SERVERPROPERTY('ProductMajorVersion') AS NVARCHAR(MAX))
    UNION ALL
    SELECT 'ProductMinorVersion', 'Minor version number',
           CAST(SERVERPROPERTY('ProductMinorVersion') AS NVARCHAR(MAX))
    UNION ALL
    SELECT 'ProductVersion', 'Full version string (e.g., 15.0.4261.1)',
           CAST(SERVERPROPERTY('ProductVersion') AS NVARCHAR(MAX))
) AS SERVER_INFORMATION
