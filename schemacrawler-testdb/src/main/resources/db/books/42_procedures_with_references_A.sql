-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Microsoft SQL Server syntax

-- Stored procedures
CREATE PROCEDURE GetTableColumnCount
    @TableSchema NVARCHAR(128),
    @TableName NVARCHAR(128),
    @ColumnCount INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
	  @ColumnCount = COUNT(*)
    FROM 
	  INFORMATION_SCHEMA.COLUMNS
    WHERE 
	  TABLE_SCHEMA = @TableSchema
      AND TABLE_NAME = @TableName;
END
