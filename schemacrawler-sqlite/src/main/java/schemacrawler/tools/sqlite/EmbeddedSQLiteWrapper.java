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
package schemacrawler.tools.sqlite;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.DatabaseUtility.checkConnection;
import static us.fatehi.utility.IOUtility.createTempFilePath;
import static us.fatehi.utility.IOUtility.isFileReadable;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import schemacrawler.schemacrawler.SchemaCrawlerDatabaseRuntimeException;
import schemacrawler.schemacrawler.SchemaCrawlerIORuntimeException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.DatabaseUrlConnectionOptions;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;

public class EmbeddedSQLiteWrapper {

  private Path databaseFile;

  public DatabaseConnectionSource createDatabaseConnectionSource() {
    requireNonNull(databaseFile, "Database file not loaded");

    final DatabaseUrlConnectionOptions urlConnectionOptions =
        new DatabaseUrlConnectionOptions(getConnectionUrl());
    final DatabaseConnectionSource connectionOptions =
        new SQLiteDatabaseConnector().newDatabaseConnectionSource(urlConnectionOptions);
    return connectionOptions;
  }

  public Path createDiagram(final String title, final String extension) {
    try (final Connection connection = createDatabaseConnectionSource().get()) {
      return createDiagram(connection, title, extension);
    } catch (final SQLException e) {
      throw new SchemaCrawlerDatabaseRuntimeException("Could not create database connection", e);
    }
  }

  public String getConnectionUrl() {
    requireNonNull(databaseFile, "Database file not loaded");
    return "jdbc:sqlite:" + databaseFile.toString();
  }

  public String getDatabase() {
    if (databaseFile == null) {
      return "";
    } else {
      return databaseFile.toString();
    }
  }

  public void loadDatabaseFile(final Path dbFile) {
    databaseFile = checkDatabaseFile(dbFile);
  }

  protected final Path checkDatabaseFile(final Path dbFile) {
    final Path databaseFile =
        requireNonNull(dbFile, "No database file path provided").normalize().toAbsolutePath();
    if (!isFileReadable(databaseFile)) {
      throw new SchemaCrawlerIORuntimeException(
          String.format("Could not read database file <%s>", dbFile));
    }
    return databaseFile;
  }

  private Path createDiagram(
      final Connection connection, final String title, final String extension) {
    try {
      checkConnection(connection);

      final SchemaCrawlerOptions schemaCrawlerOptions =
          SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

      final Path diagramFile = createTempFilePath("schemacrawler", extension);
      final OutputOptions outputOptions =
          OutputOptionsBuilder.builder()
              .title(title)
              .withOutputFormatValue(extension)
              .withOutputFile(diagramFile)
              .toOptions();

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("schema");
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.setConnection(connection);
      executable.execute();

      return diagramFile;
    } catch (final Exception e) {
      throw new SchemaCrawlerRuntimeException(
          String.format("Could not create database schema diagram <%s>", title), e);
    }
  }
}
