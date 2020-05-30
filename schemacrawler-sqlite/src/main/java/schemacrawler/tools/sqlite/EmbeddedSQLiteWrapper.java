/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import sf.util.SchemaCrawlerLogger;

public class EmbeddedSQLiteWrapper
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(EmbeddedSQLiteWrapper.class.getName());

  private Path databaseFile;

  public DatabaseConnectionSource createDatabaseConnectionSource()
    throws SchemaCrawlerException
  {
    requireNonNull(databaseFile, "Database file not loaded");

    try
    {
      final DatabaseConnectionSource connectionOptions =
        new SQLiteDatabaseConnector().newDatabaseConnectionSource(config -> new DatabaseConnectionSource(
          getConnectionUrl(),
          config));
      return connectionOptions;
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException(
        "Cannot read SQLite database file, " + databaseFile, e);
    }
  }

  public String getConnectionUrl()
  {
    requireNonNull(databaseFile, "Database file not loaded");
    return "jdbc:sqlite:" + databaseFile.toString();
  }

  public String getDatabase()
  {
    if (databaseFile == null)
    {
      return "";
    }
    else
    {
      return databaseFile.toString();
    }
  }

  public void loadDatabaseFile(final Path dbFile)
    throws IOException
  {
    databaseFile = checkDatabaseFile(dbFile);
  }

  public Path createDiagram(final String extension)
    throws Exception
  {
    try (final Connection connection = createDatabaseConnectionSource().get())
    {
      return createDiagram(connection, extension);
    }
  }

  protected final Path checkDatabaseFile(final Path dbFile)
    throws IOException
  {
    final Path databaseFile =
      requireNonNull(dbFile, "No database file path provided")
        .normalize()
        .toAbsolutePath();
    if (!isFileReadable(databaseFile))
    {
      final IOException e =
        new IOException("Cannot read database file, " + databaseFile);
      LOGGER.log(Level.FINE, e.getMessage(), e);
      throw e;
    }
    return databaseFile;
  }

  private Path createDiagram(final Connection connection,
                             final String extension)
    throws Exception
  {
    checkConnection(connection);

    final SchemaCrawlerOptions schemaCrawlerOptions =
      SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final Path diagramFile = createTempFilePath("schemacrawler", extension);
    final OutputOptions outputOptions =
      OutputOptionsBuilder.newOutputOptions(extension, diagramFile);

    final SchemaCrawlerExecutable executable =
      new SchemaCrawlerExecutable("schema");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(outputOptions);
    executable.setConnection(connection);
    executable.execute();

    return diagramFile;
  }

}
