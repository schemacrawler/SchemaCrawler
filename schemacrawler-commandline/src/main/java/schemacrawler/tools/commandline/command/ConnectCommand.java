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

package schemacrawler.tools.commandline.command;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.UserCredentials;
import schemacrawler.SchemaCrawlerLogger;
import us.fatehi.utility.string.StringFormat;

@Command(name = "connect",
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
         customSynopsis = {
           "connect"
         },
         optionListHeading = "Options:%n")
public class ConnectCommand
  extends BaseStateHolder
  implements Runnable
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(ConnectCommand.class.getName());

  @ArgGroup(exclusive = true)
  private DatabaseConnectionOptions databaseConnectionOptions;
  @Spec
  private Model.CommandSpec spec;
  @Mixin
  private UserCredentialsOptions userCredentialsOptions;

  public ConnectCommand(final SchemaCrawlerShellState state)
  {
    super(state);
  }

  @Override
  public void run()
  {

    try
    {
      // Match the database connector in the best possible way, using the
      // server argument, or the JDBC connection URL
      final DatabaseConnectable databaseConnectable = getDatabaseConnectable();
      requireNonNull(databaseConnectable,
                     "No database connection options provided");
      final DatabaseConnector databaseConnector =
        databaseConnectable.getDatabaseConnector();
      requireNonNull(databaseConnector,
                     "No database connection options provided");
      LOGGER.log(Level.INFO,
                 new StringFormat("Using database plugin <%s>",
                                  databaseConnector.getDatabaseServerType()));

      final Config config = new Config();
      config.putAll(state.getAdditionalConfiguration());
      config.putAll(databaseConnector.getConfig());
      config.putAll(state.getBaseConfiguration());

      state.sweep();

      state.addAdditionalConfiguration(config);
      loadSchemaCrawlerOptionsBuilder();
      createDataSource(databaseConnector,
                       databaseConnectable,
                       getUserCredentials());
      loadSchemaRetrievalOptionsBuilder(databaseConnector);

    }
    catch (final SchemaCrawlerException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
    catch (final SQLException e)
    {
      throw new RuntimeException("Cannot connect to database", e);
    }
  }

  public DatabaseConnectable getDatabaseConnectable()
  {
    if (databaseConnectionOptions == null)
    {
      throw new ParameterException(spec.commandLine(),
                                   "No database connection options provided");
    }

    final DatabaseConnectable databaseConnectable =
      databaseConnectionOptions.getDatabaseConnectable();
    if (databaseConnectable == null)
    {
      throw new ParameterException(spec.commandLine(),
                                   "No database connection options provided");
    }

    return databaseConnectable;
  }

  private UserCredentials getUserCredentials()
  {

    if (userCredentialsOptions == null)
    {
      throw new ParameterException(spec.commandLine(),
                                   "No database connection credentials provided");
    }
    final UserCredentials userCredentials =
      userCredentialsOptions.getUserCredentials();
    return userCredentials;
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
    final DatabaseConnectionSource databaseConnectionSource =
      databaseConnector.newDatabaseConnectionSource(databaseConnectable);
    databaseConnectionSource.setUserCredentials(userCredentials);

    state.setDataSource(databaseConnectionSource);
  }

  private void loadSchemaCrawlerOptionsBuilder()
  {
    LOGGER.log(Level.FINE, () -> "Creating SchemaCrawler options builder");

    final Config config = state.getAdditionalConfiguration();
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder =
      SchemaCrawlerOptionsBuilder
        .builder()
        .fromConfig(config);
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
    try (
      final Connection connection = state
        .getDataSource()
        .get()
    )
    {
      final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        databaseConnector.getSchemaRetrievalOptionsBuilder(connection);
      schemaRetrievalOptionsBuilder.fromConfig(config);
      state.setSchemaRetrievalOptionsBuilder(schemaRetrievalOptionsBuilder);
    }
  }

}
