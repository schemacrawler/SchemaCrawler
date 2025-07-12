/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.commandline.command;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.utility.SchemaCrawlerOptionsConfig;
import schemacrawler.tools.commandline.utility.SchemaRetrievalOptionsConfig;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.UserCredentials;
import us.fatehi.utility.string.StringFormat;

@Command(
    name = "connect",
    header = "** Connect to the database",
    description = {
      "",
      "For database connections, please read",
      "https://www.schemacrawler.com/database-support.html",
      "first, before running SchemaCrawler",
      ""
    },
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"connect"},
    optionListHeading = "Options:%n",
    footer = {
      "",
      "For additional options, specific to individual database server plugins,",
      "run SchemaCrawler with: `-h servers`",
      "or from the SchemaCrawler interactive shell: `help servers`"
    })
public class ConnectCommand extends BaseStateHolder implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(ConnectCommand.class.getName());

  @ArgGroup(exclusive = true)
  private DatabaseConnectionGroupOptions databaseConnectionGroupOptions;

  @Spec private Model.CommandSpec spec;
  @Mixin private UserCredentialsOptions userCredentialsOptions;

  public ConnectCommand(final ShellState state) {
    super(state);
  }

  public DatabaseConnectionOptions getDatabaseConnectionOptions() {
    if (databaseConnectionGroupOptions == null) {
      throw new ParameterException(spec.commandLine(), "No database connection options provided");
    }

    final DatabaseConnectionOptions databaseConnectionOptions =
        databaseConnectionGroupOptions.getDatabaseConnectionOptions();
    if (databaseConnectionOptions == null) {
      throw new ParameterException(spec.commandLine(), "No database connection options provided");
    }

    return databaseConnectionOptions;
  }

  @Override
  public void run() {

    try {
      // Match the database connector in the best possible way, using the
      // server argument, or the JDBC connection URL
      final DatabaseConnectionOptions databaseConnectionOptions = getDatabaseConnectionOptions();
      requireNonNull(databaseConnectionOptions, "No database connection options provided");
      final DatabaseConnector databaseConnector = databaseConnectionOptions.getDatabaseConnector();
      requireNonNull(databaseConnector, "No database plugin located (not even unknown)");
      LOGGER.log(
          Level.INFO,
          new StringFormat(
              "Using database plugin <%s>", databaseConnector.getDatabaseServerType()));

      loadSchemaCrawlerOptionsBuilder(databaseConnector);
      createDataSource(databaseConnector, databaseConnectionOptions, getUserCredentials());
      loadSchemaRetrievalOptionsBuilder(databaseConnector);

    } catch (final SQLException e) {
      throw new DatabaseAccessException("Cannot connect to database", e);
    }
  }

  private void createDataSource(
      final DatabaseConnector databaseConnector,
      final DatabaseConnectionOptions connectionOptions,
      final UserCredentials userCredentials) {
    requireNonNull(databaseConnector, "No database plugin provided");
    requireNonNull(connectionOptions, "No database connection options provided");
    requireNonNull(userCredentials, "No database connection user credentials provided");

    LOGGER.log(Level.FINE, "Creating data-source");

    // Connect using connection options provided from the command-line,
    // provided configuration, and database plugin defaults
    final DatabaseConnectionSource databaseConnectionSource =
        databaseConnector.newDatabaseConnectionSource(connectionOptions, userCredentials);

    state.setDataSource(databaseConnectionSource);
  }

  private UserCredentials getUserCredentials() {

    if (userCredentialsOptions == null) {
      throw new ParameterException(
          spec.commandLine(), "No database connection credentials provided");
    }
    final UserCredentials userCredentials = userCredentialsOptions.getUserCredentials();
    return userCredentials;
  }

  private void loadSchemaCrawlerOptionsBuilder(final DatabaseConnector databaseConnector) {
    LOGGER.log(Level.FINE, "Creating SchemaCrawler options builder");

    SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    // Set defaults from database plugin, such as default schema excludes
    schemaCrawlerOptions = databaseConnector.setSchemaCrawlerOptionsDefaults(schemaCrawlerOptions);
    // Override with options from config file
    final Config config = state.getConfig();
    schemaCrawlerOptions = SchemaCrawlerOptionsConfig.fromConfig(schemaCrawlerOptions, config);

    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
  }

  private void loadSchemaRetrievalOptionsBuilder(final DatabaseConnector databaseConnector)
      throws SQLException {
    requireNonNull(databaseConnector, "No database connection options provided");

    LOGGER.log(Level.FINE, "Creating SchemaCrawler retrieval options builder");

    final Config config = state.getConfig();
    try (final Connection connection = state.getDataSource().get(); ) {
      final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
          databaseConnector.getSchemaRetrievalOptionsBuilder(connection);
      state.setSchemaRetrievalOptions(
          SchemaRetrievalOptionsConfig.fromConfig(schemaRetrievalOptionsBuilder, config)
              .toOptions());
    }
  }
}
