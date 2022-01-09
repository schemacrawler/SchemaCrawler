/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static schemacrawler.test.utility.TestUtility.failTestSetup;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import schemacrawler.testdb.SqlScript;
import schemacrawler.testdb.TestSchemaCreatorMain;
import us.fatehi.utility.IOUtility;

public abstract class BaseSqliteTest {

  protected Connection createConnection(final Path sqliteDbFile) {
    try {
      return createDataSource("jdbc:sqlite:" + sqliteDbFile).getConnection();
    } catch (final SQLException e) {
      failTestSetup(
          String.format("Could not create a database connection for SQLite file", sqliteDbFile), e);
      return null; // Appease compiler
    }
  }

  protected DataSource createDatabaseInMemoryFromScript(final String databaseSqlResource)
      throws Exception {

    final DataSource dataSource = createDataSource("jdbc:sqlite::memory:");

    try (final Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);

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

  protected Path createTestDatabase() throws Exception {
    final Path sqliteDbFile =
        IOUtility.createTempFilePath("sc", ".db").normalize().toAbsolutePath();
    TestSchemaCreatorMain.call("--url", "jdbc:sqlite:" + sqliteDbFile);
    return sqliteDbFile;
  }

  private DataSource createDataSource(final String connectionUrl) {
    final BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUrl(connectionUrl);
    dataSource.setUsername(null);
    dataSource.setPassword(null);
    dataSource.setDefaultAutoCommit(false);

    return dataSource;
  }
}
