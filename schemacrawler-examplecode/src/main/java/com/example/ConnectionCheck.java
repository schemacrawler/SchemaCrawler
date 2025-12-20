/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.example;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import us.fatehi.utility.database.ConnectionInfoBuilder;

public class ConnectionCheck {

  public static void main(final String[] args) throws SQLException {

    final String connectionUrl = "jdbc:hsqldb:hsql://localhost:9001/schemacrawler";

    final Connection connection = DriverManager.getConnection(connectionUrl);
    final ConnectionInfoBuilder builder = ConnectionInfoBuilder.builder(connection);
    System.out.println(builder.buildDatabaseInformation());
    System.out.println(builder.buildJdbcDriverInformation());

    final DatabaseMetaData dbMetaData = connection.getMetaData();
    final ResultSet results = dbMetaData.getTables(null, null, "%", null);
    while (results.next()) {
      final String catalogName = results.getString("TABLE_CAT");
      final String schemaName = results.getString("TABLE_SCHEM");
      final String tableName = results.getString("TABLE_NAME");
      final String tableType = results.getString("TABLE_TYPE");
      System.out.printf("o--> %s//%s//%s (%s)%n", catalogName, schemaName, tableName, tableType);
    }
  }
}
