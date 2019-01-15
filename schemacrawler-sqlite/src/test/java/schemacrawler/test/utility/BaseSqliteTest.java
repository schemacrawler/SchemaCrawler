/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SingleUseUserCredentials;
import schemacrawler.testdb.SqlScript;
import schemacrawler.tools.sqlite.SQLiteDatabaseConnector;
import sf.util.IOUtility;

public abstract class BaseSqliteTest
{

  protected DataSource createDataSource(final Path sqliteDbFile)
    throws SchemaCrawlerException
  {

    final Config config = new Config();
    config.put("server", "sqlite");
    config.put("database", sqliteDbFile.toString());

    DataSource dataSource;
    try
    {
      dataSource = new SQLiteDatabaseConnector()
        .newDatabaseConnectionOptions(new SingleUseUserCredentials(), config);
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }

    return dataSource;
  }

  protected Path createTestDatabase(final String databaseSqlResource)
    throws Exception
  {
    final Path sqliteDbFile = IOUtility.createTempFilePath("resource", "db")
      .normalize().toAbsolutePath();

    final DataSource dataSource = createDataSource(sqliteDbFile);

    try (Connection connection = dataSource.getConnection();)
    {
      connection.setAutoCommit(false);

      final SqlScript sqlScript = new SqlScript(databaseSqlResource,
                                                connection);
      sqlScript.run();
    }

    return sqliteDbFile;
  }

  protected Connection createConnection(Path sqliteDbFile)
    throws SQLException, SchemaCrawlerException
  {
    return createDataSource(sqliteDbFile).getConnection();
  }

}
