/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.sqlite;


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.checkConnection;
import static sf.util.IOUtility.createTempFilePath;
import static sf.util.IOUtility.isFileReadable;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SingleUseUserCredentials;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.integration.graph.GraphExecutable;
import schemacrawler.tools.options.OutputOptions;

public class SchemaCrawlerSQLiteUtility
{

  public static Connection createDatabaseConnection(final Path dbFile)
    throws SchemaCrawlerException
  {
    requireNonNull(dbFile, "No SQLite database file provided");
    if (!isFileReadable(dbFile))
    {
      throw new SchemaCrawlerException("Cannot read, " + dbFile);
    }

    final Config config = new Config();
    config.put("server", "sqlite");
    config.put("database", dbFile.toString());
    final ConnectionOptions connectionOptions = new SQLiteDatabaseConnector()
      .newDatabaseConnectionOptions(new SingleUseUserCredentials(), config);
    final Connection connection;
    try
    {
      connection = connectionOptions.getConnection();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Cannot read SQLite database " + dbFile,
                                       e);
    }
    return connection;
  }

  public static Path createSchemaCrawlerDiagram(final Path dbFile,
                                                final String extension)
    throws Exception
  {
    try (final Connection connection = createDatabaseConnection(dbFile);)
    {
      return createSchemaCrawlerDiagram(connection, extension);
    }
  }

  private static Path createSchemaCrawlerDiagram(final Connection connection,
                                                 final String extension)
    throws Exception
  {
    checkConnection(connection);

    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
    options.setRoutineInclusionRule(new ExcludeAll());

    final Path diagramFile = createTempFilePath("schemacrawler", extension);
    final OutputOptions outputOptions = new OutputOptions(extension,
                                                          diagramFile);

    final Executable executable = new GraphExecutable("schema");
    executable.setSchemaCrawlerOptions(options);
    executable.setOutputOptions(outputOptions);
    executable.execute(connection);

    return diagramFile;
  }

  private SchemaCrawlerSQLiteUtility()
  {
    // Disable instantiation
  }

}
