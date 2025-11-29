/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static org.junit.jupiter.api.Assertions.fail;
import static us.fatehi.test.utility.TestUtility.failTestSetup;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import schemacrawler.testdb.TestSchemaCreatorMain;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.database.SqlScript;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

public abstract class BaseSqliteTest {

  protected DatabaseConnectionSource createDatabaseFromScript(
      final DatabaseConnectionSource dataSource, final String databaseSqlResource)
      throws Exception {

    try (final Connection connection = dataSource.get()) {

      SqlScript.executeScriptFromResource(databaseSqlResource, connection);

    } catch (final SQLException e) {
      failTestSetup(
          "Could not create a database connection for SQL script".formatted(databaseSqlResource),
          e);
      return null; // Appease compiler
    }

    return dataSource;
  }

  protected DatabaseConnectionSource createDatabaseFromScriptInMemory(
      final String databaseSqlResource) throws Exception {

    final DatabaseConnectionSource dataSource = createDataSourceInMemory();
    return createDatabaseFromScript(dataSource, databaseSqlResource);
  }

  protected DatabaseConnectionSource createDataSourceFromFile(final Path sqliteDbFile) {
    return createDataSource("jdbc:sqlite:" + sqliteDbFile);
  }

  protected DatabaseConnectionSource createDataSourceFromResource(final String sqliteDbResource) {
    return createDataSource("jdbc:sqlite::resource:%s".formatted(sqliteDbResource));
  }

  protected DatabaseConnectionSource createDataSourceInMemory() {
    try {
      final Path tempFilePath = IOUtility.createTempFilePath("sc", ".db");
      return createDataSource("jdbc:sqlite:" + tempFilePath);
    } catch (final IOException e) {
      fail(e);
      return null;
    }
  }

  protected Path createTestDatabase() {
    try {
      final Path sqliteDbFile =
          IOUtility.createTempFilePath("sc", ".db").normalize().toAbsolutePath();
      TestSchemaCreatorMain.call("--url", "jdbc:sqlite:" + sqliteDbFile);
      return sqliteDbFile;
    } catch (final IOException e) {
      fail(e);
      return null;
    }
  }

  private DatabaseConnectionSource createDataSource(final String connectionUrl) {
    return DatabaseConnectionSources.newDatabaseConnectionSource(
        connectionUrl, new MultiUseUserCredentials());
  }
}
