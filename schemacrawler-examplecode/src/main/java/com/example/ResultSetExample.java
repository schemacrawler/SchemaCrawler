/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

public final class ResultSetExample {

  public static void main(final String[] args) throws Exception {

    // Set log level
    new LoggingConfig(Level.OFF);

    final String query =
        """
        SELECT
          T1.*,
          T2.*
        FROM
          TABLE1_PK T1
          JOIN TABLE2_PK T2
            ON T1.ENTITY_ID = T2.ENTITY_ID
        """;
    try (final Connection connection = getDatabaseConnectionSource().get();
        final Statement statement = connection.createStatement();
        final ResultSet results = statement.executeQuery(query)) {
      // Get result set metadata
      final ResultsColumns resultColumns = SchemaCrawlerUtility.getResultsColumns(results);
      for (final ResultsColumn column : resultColumns) {
        System.out.printf("o--> %s%n", column);
        System.out.printf("     - label:     %s%n", column.getLabel());
        System.out.printf("     - data-type: %s%n", column.getColumnDataType());
        System.out.printf("     - table:     %s%n", column.getParent());
      }
    }
  }

  private static DatabaseConnectionSource getDatabaseConnectionSource() {
    final String connectionUrl = "jdbc:sqlite::resource:test.db";
    return DatabaseConnectionSources.newDatabaseConnectionSource(
        connectionUrl, new MultiUseUserCredentials("", ""));
  }
}
