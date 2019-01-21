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
package schemacrawler.tools.integration.embeddeddb;


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.checkConnection;
import static sf.util.IOUtility.createTempFilePath;
import static sf.util.IOUtility.isFileReadable;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.databaseconnector.ConnectionOptions;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import sf.util.SchemaCrawlerLogger;

public abstract class EmbeddedDatabaseWrapper
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(EmbeddedDatabaseWrapper.class.getName());

  public abstract ConnectionOptions createConnectionOptions()
    throws SchemaCrawlerException;

  public Connection createDatabaseConnection()
    throws SchemaCrawlerException
  {
    try
    {
      return createConnectionOptions().getConnection();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Cannot load database file", e);
    }
  }

  public Path createDiagram(final String extension)
    throws Exception
  {
    try (final Connection connection = createDatabaseConnection();)
    {
      return createDiagram(connection, extension);
    }
  }

  public abstract String getConnectionUrl();

  public abstract String getDatabase();

  public abstract String getPassword();

  public abstract String getUser();

  public abstract void loadDatabaseFile(final Path dbFile)
    throws IOException;

  public abstract void startServer()
    throws SchemaCrawlerException;

  public abstract void stopServer()
    throws SchemaCrawlerException;

  protected final Path checkDatabaseFile(final Path dbFile)
    throws IOException
  {
    final Path databaseFile = requireNonNull(dbFile,
                                             "No database file path provided")
                                               .normalize().toAbsolutePath();
    if (!isFileReadable(databaseFile))
    {
      final IOException e = new IOException("Cannot read database file, "
                                            + databaseFile);
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

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.standard())
      .includeRoutines(new ExcludeAll());
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final Path diagramFile = createTempFilePath("schemacrawler", extension);
    final OutputOptions outputOptions = OutputOptionsBuilder
      .newOutputOptions(extension, diagramFile);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("schema");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(outputOptions);
    executable.setConnection(connection);
    executable.execute();

    return diagramFile;
  }

}
