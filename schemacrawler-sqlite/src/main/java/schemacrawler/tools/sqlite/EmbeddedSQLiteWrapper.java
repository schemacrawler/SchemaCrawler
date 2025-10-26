/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.sqlite;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.IOUtility.createTempFilePath;
import static us.fatehi.utility.IOUtility.isFileReadable;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.ListExclusionRule;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.databaseconnector.DatabaseUrlConnectionOptions;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

public class EmbeddedSQLiteWrapper {

  private static InclusionRule sqliteTableExclusionRule =
      new InclusionRule() {

        private static final long serialVersionUID = -7643052797359767051L;

        private final Predicate<String> exclusionRule =
            new ListExclusionRule(
                    List.of(
                        // Django tables
                        "auth_group",
                        "auth_group_permissions",
                        "auth_permission",
                        "auth_user",
                        "auth_user_groups",
                        "auth_user_user_permissions",
                        "otp_totp_totpdevice",
                        // Liquibase
                        "DATABASECHANGELOG",
                        // Flyway
                        "SCHEMA_VERSION",
                        // Entity Framework Core https://github.com/dotnet/efcore
                        "_EFMigrationsHistory",
                        // Android
                        "android_metadata"))
                .and(new RegularExpressionExclusionRule("django_.*"));

        /** {@inheritDoc} */
        @Override
        public boolean test(final String text) {
          return exclusionRule.test(text);
        }
      };

  private Path databaseFile;

  public DatabaseConnectionSource createDatabaseConnectionSource() {
    requireNonNull(databaseFile, "Database file not loaded");

    final DatabaseUrlConnectionOptions urlConnectionOptions =
        new DatabaseUrlConnectionOptions(getConnectionUrl());
    final DatabaseConnectionSource connectionOptions =
        new SQLiteDatabaseConnector()
            .newDatabaseConnectionSource(urlConnectionOptions, new MultiUseUserCredentials());
    return connectionOptions;
  }

  public Path executeForOutput(final String title, final OutputFormat extension) {
    try (final DatabaseConnectionSource dataSource = createDatabaseConnectionSource()) {
      return executeForOutput(dataSource, title, extension);
    } catch (final SQLException e) {
      throw new DatabaseAccessException("Could not run against SQLite database", e);
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Could not run against SQLite database", e);
    }
  }

  public String getConnectionUrl() {
    requireNonNull(databaseFile, "Database file not loaded");
    return "jdbc:sqlite:" + databaseFile.toString();
  }

  public Path getDatabasePath() {
    if (databaseFile == null) {
      return null;
    }
    return databaseFile;
  }

  public void setDatabasePath(final Path dbFile) {
    databaseFile = checkDatabaseFile(dbFile);
  }

  protected final Path checkDatabaseFile(final Path dbFile) {
    final Path databaseFile =
        requireNonNull(dbFile, "No database file path provided").normalize().toAbsolutePath();
    if (!isFileReadable(databaseFile)) {
      throw new IORuntimeException("Could not read database file <%s>".formatted(dbFile));
    }
    return databaseFile;
  }

  private Path executeForOutput(
      final DatabaseConnectionSource dataSource, final String title, final OutputFormat extension) {
    try {

      final LimitOptions limitOptions =
          LimitOptionsBuilder.builder().includeTables(sqliteTableExclusionRule).toOptions();
      final SchemaCrawlerOptions schemaCrawlerOptions =
          SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions().withLimitOptions(limitOptions);

      final Path diagramFile = createTempFilePath("schemacrawler", extension.getFormat());
      final OutputOptions outputOptions =
          OutputOptionsBuilder.builder()
              .title(title)
              .withOutputFormat(extension)
              .withOutputFile(diagramFile)
              .toOptions();

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("schema");
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.setDataSource(dataSource);
      executable.execute();

      return diagramFile;
    } catch (final Exception e) {
      throw new ExecutionRuntimeException(
          "Could not create database schema diagram <%s>".formatted(title), e);
    }
  }
}
