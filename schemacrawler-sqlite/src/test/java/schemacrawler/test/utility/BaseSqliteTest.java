/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.testdb.SqlScript;
import us.fatehi.utility.IOUtility;

public abstract class BaseSqliteTest {

  protected Connection createConnection(final Path sqliteDbFile)
      throws SQLException, SchemaCrawlerException {
    return createDataSource(sqliteDbFile).getConnection();
  }

  protected DataSource createDataSource(final Path sqliteDbFile) {
    final BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUrl("jdbc:sqlite:" + sqliteDbFile);
    dataSource.setUsername(null);
    dataSource.setPassword(null);
    dataSource.setDefaultAutoCommit(false);

    return dataSource;
  }

  protected Path createTestDatabase(final String databaseSqlResource) throws Exception {
    final Path sqliteDbFile =
        IOUtility.createTempFilePath("resource", "db").normalize().toAbsolutePath();

    final DataSource dataSource = createDataSource(sqliteDbFile);

    try (final Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);

      final SqlScript sqlScript = new SqlScript(databaseSqlResource, connection);
      sqlScript.run();
    }

    return sqliteDbFile;
  }
}
