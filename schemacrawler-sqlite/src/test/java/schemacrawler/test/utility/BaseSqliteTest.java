/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test.utility;

import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.TestUtility.failTestSetup;

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
          String.format(
              "Could not create a database connection for SQL script", databaseSqlResource),
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
    return createDataSource(String.format("jdbc:sqlite::resource:%s", sqliteDbResource));
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
