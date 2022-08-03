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
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.UserCredentials;
import schemacrawler.tools.options.Config;
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
    final Connection connection = state.getDataSource().get();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        databaseConnector.getSchemaRetrievalOptionsBuilder(connection);
    state.setSchemaRetrievalOptions(
        SchemaRetrievalOptionsConfig.fromConfig(schemaRetrievalOptionsBuilder, config).toOptions());
  }
}
