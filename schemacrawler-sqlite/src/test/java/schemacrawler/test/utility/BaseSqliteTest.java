/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static sf.util.Utility.applyApplicationLogLevel;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.logging.Level;

import javax.sql.DataSource;

import org.junit.BeforeClass;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SingleUseUserCredentials;
import schemacrawler.testdb.SqlScript;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.sqlite.SQLiteDatabaseConnector;
import sf.util.IOUtility;

public abstract class BaseSqliteTest
{

  @BeforeClass
  public static void setApplicationLogLevel()
    throws Exception
  {
    applyApplicationLogLevel(Level.OFF);
  }

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

  protected void executeExecutable(final Path sqliteDbFile,
                                   final SchemaCrawlerExecutable executable,
                                   final String referenceFileName)
    throws Exception
  {
    final String outputFormatValue = TextOutputFormat.text.name();
    try (final TestWriter out = new TestWriter(outputFormatValue);)
    {
      final OutputOptions outputOptions = OutputOptionsBuilder
        .newOutputOptions(outputFormatValue, out);

      executable.setOutputOptions(outputOptions);
      try (Connection connection = createDataSource(sqliteDbFile)
        .getConnection();)
      {
        executable.setConnection(connection);
        executable.execute();
      }

      out.assertEquals(referenceFileName);
    }
  }

}
