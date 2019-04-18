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

package schemacrawler.tools.commandline.command;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import org.apache.commons.dbcp2.BasicDataSource;
import picocli.CommandLine;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.commandline.AvailableServers;
import schemacrawler.tools.commandline.parser.ConnectionOptions;
import schemacrawler.tools.commandline.parser.DatabaseConnectable;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.UserCredentials;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public class ConnectCommands
{
  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ConnectCommands.class.getName());

  @CommandLine.Command(description = "List available SchemaCrawler database plugins")
  public static void servers()
  {
    LOGGER.log(Level.INFO, "servers");

    for (String server : new AvailableServers())
    {
      System.out.println(server);
    }
  }

  private final SchemaCrawlerShellState state;

  public ConnectCommands(final SchemaCrawlerShellState state)
  {
    this.state = requireNonNull(state, "No state provided");
  }

  @CommandLine.Command(name = "connect", description = "Connect to a database")
  public String connect(@CommandLine.Mixin final ConnectionOptions connectionOptions)
  {
    try
    {
      // Match the database connector in the best possible way, using the
      // server argument, or the JDBC connection URL
      final DatabaseConnectable databaseConnectable = connectionOptions
        .getDatabaseConnectable();
      requireNonNull(databaseConnectable,
                     "No database connection options provided");
      final DatabaseConnector databaseConnector = databaseConnectable
        .getDatabaseConnector();
      requireNonNull(databaseConnector,
                     "No database connection options provided");
      LOGGER.log(Level.INFO,
                 new StringFormat("Using database plugin <%s>",
                                  databaseConnector.getDatabaseServerType()));

      final Config config = new Config();
      config.putAll(databaseConnector.getConfig());
      config.putAll(state.getBaseConfiguration());

      sweep();

      state.setAdditionalConfiguration(config);
      loadSchemaCrawlerOptionsBuilder();
      createDataSource(databaseConnector,
                       databaseConnectable,
                       connectionOptions.getUserCredentials());
      loadSchemaRetrievalOptionsBuilder(databaseConnector);

      return success();
    }
    catch (final SchemaCrawlerException | SQLException e)
    {
      throw new RuntimeException("Cannot connect to database", e);
    }
  }

  @CommandLine.Command(description = "Disconnect from a database")
  public void disconnect()
  {
    LOGGER.log(Level.INFO, "disconnect");

    state.disconnect();
  }

  @CommandLine.Command(description = "Connect to a database, using a connection URL specification")
  public boolean isConnected()
  {
    final boolean isConnected = state.isConnected();
    LOGGER.log(Level.INFO, new StringFormat("isConnected=%b", isConnected));
    return isConnected;
  }

  @CommandLine.Command(description = "Disconnect from a database, and clear loaded catalog")
  public void sweep()
  {
    LOGGER.log(Level.INFO, "sweep");

    state.sweep();
  }

  private void createDataSource(final DatabaseConnector databaseConnector,
                                final DatabaseConnectable databaseConnectable,
                                final UserCredentials userCredentials)
    throws SchemaCrawlerException
  {
    requireNonNull(databaseConnector,
                   "No database connection options provided");
    requireNonNull(databaseConnectable,
                   "No database connection options provided");
    requireNonNull(userCredentials,
                   "No database connection user credentials provided");

    LOGGER.log(Level.FINE, () -> "Creating data-source");

    // Connect using connection options provided from the command-line,
    // provided configuration, and bundled configuration
    final DatabaseConnectionSource databaseConnectionSource = databaseConnector
      .newDatabaseConnectionSource(databaseConnectable);
    databaseConnectionSource.setUserCredentials(userCredentials);

    final BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUsername(userCredentials.getUser());
    dataSource.setPassword(userCredentials.getPassword());
    dataSource.setUrl(databaseConnectionSource.getConnectionUrl());
    dataSource.setDefaultAutoCommit(false);
    dataSource.setInitialSize(1);
    dataSource.setMaxTotal(1);

    state.setDataSource(dataSource);
  }

  private void loadSchemaCrawlerOptionsBuilder()
  {
    LOGGER.log(Level.FINE, () -> "Creating SchemaCrawler options builder");

    final Config config = state.getAdditionalConfiguration();
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().fromConfig(config);
    state.setSchemaCrawlerOptionsBuilder(schemaCrawlerOptionsBuilder);
  }

  private void loadSchemaRetrievalOptionsBuilder(final DatabaseConnector databaseConnector)
    throws SQLException
  {
    requireNonNull(databaseConnector,
                   "No database connection options provided");

    LOGGER.log(Level.FINE,
               () -> "Creating SchemaCrawler retrieval options builder");

    final Config config = state.getAdditionalConfiguration();
    try (final Connection connection = state.getDataSource().getConnection())
    {
      final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = databaseConnector
        .getSchemaRetrievalOptionsBuilder(connection);
      schemaRetrievalOptionsBuilder.fromConfig(config);
      state.setSchemaRetrievalOptionsBuilder(schemaRetrievalOptionsBuilder);
    }
  }

  private String success()
  {
    if (isConnected())
    {
      return "Connected";
    }
    else
    {
      return "Not connected";
    }
  }

}
