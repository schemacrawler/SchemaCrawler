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
package schemacrawler.tools.databaseconnector;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConfigConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.SingleUseUserCredentials;
import schemacrawler.schemacrawler.UserCredentials;
import schemacrawler.tools.iosource.InputResource;
import schemacrawler.utility.PropertiesUtility;

public abstract class DatabaseConnector
  implements Options
{

  protected static final DatabaseConnector UNKNOWN = new DatabaseConnector()
  {
  };

  private final DatabaseServerType dbServerType;
  private final InputResource connectionHelpResource;
  private final InputResource configResource;
  private final BiConsumer<InformationSchemaViewsBuilder, Connection> informationSchemaViewsBuilderForConnection;
  private final Predicate<String> supportsUrlPredicate;

  protected DatabaseConnector(final DatabaseServerType dbServerType,
                              final InputResource connectionHelpResource,
                              final InputResource configResource,
                              final BiConsumer<InformationSchemaViewsBuilder, Connection> informationSchemaViewsBuilderForConnection,
                              final Predicate<String> supportsUrlPredicate)
  {
    this.dbServerType = requireNonNull(dbServerType,
                                       "No database server type provided");

    this.connectionHelpResource = requireNonNull(connectionHelpResource,
                                                 "No connection help provided");

    this.configResource = requireNonNull(configResource,
                                         "No config resource provided");

    this.informationSchemaViewsBuilderForConnection = informationSchemaViewsBuilderForConnection;

    this.supportsUrlPredicate = requireNonNull(supportsUrlPredicate,
                                               "No database connection URL predicate provided");
  }

  /**
   * Constructor for unknown databases. Bypass the null-checks of the
   * main constructor
   */
  private DatabaseConnector()
  {
    dbServerType = DatabaseServerType.UNKNOWN;
    connectionHelpResource = null;
    configResource = null;
    informationSchemaViewsBuilderForConnection = null;
    supportsUrlPredicate = url -> false;
  }

  /**
   * Gets the complete bundled database configuration set. This is
   * useful in building the SchemaCrawler options.
   */
  public final Config getConfig()
  {
    if (configResource == null)
    {
      return new Config();
    }
    return PropertiesUtility.loadConfig(configResource);
  }

  public InputResource getConnectionHelpResource()
  {
    return connectionHelpResource;
  }

  public DatabaseServerType getDatabaseServerType()
  {
    return dbServerType;
  }

  /**
   * Gets the complete bundled database specific configuration set,
   * including the SQL for information schema views.
   *
   * @param connection
   *        Database connection
   */
  public SchemaRetrievalOptionsBuilder getSchemaRetrievalOptionsBuilder(final Connection connection)
  {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder
      .builder().withDatabaseServerType(dbServerType)
      .withInformationSchemaViewsForConnection(informationSchemaViewsBuilderForConnection,
                                               connection)
      .fromConnnection(connection);

    return schemaRetrievalOptionsBuilder;
  }

  public boolean isUnknownDatabaseSystem()
  {
    return dbServerType.isUnknownDatabaseSystem();
  }

  /**
   * Creates a datasource for connecting to a database. Additional
   * connection options are provided, from the command-line, and
   * configuration file.
   *
   * @param additionalConfig
   *        Configuration from the command-line, and from configuration
   *        files.
   */
  public ConnectionOptions newDatabaseConnectionOptions(final UserCredentials userCredentials,
                                                        final Config additionalConfig)
    throws SchemaCrawlerException
  {
    requireNonNull(userCredentials,
                   "No database connection user credentials provided");

    final Config config = getConfig();
    if (additionalConfig != null)
    {
      config.putAll(additionalConfig);
      // Remove sensitive properties from the original configuration
      additionalConfig.remove("user");
      additionalConfig.remove("password");
    }

    final ConnectionOptions connectionOptions;
    if (dbServerType.isUnknownDatabaseSystem() || config.hasValue("url"))
    {
      connectionOptions = new DatabaseConnectionOptions(userCredentials,
                                                        config);
    }
    else
    {
      connectionOptions = new DatabaseConfigConnectionOptions(userCredentials,
                                                              config);
    }

    return connectionOptions;
  }

  public final boolean supportsUrl(final String url)
  {
    if (isBlank(url))
    {
      return false;
    }
    return supportsUrlPredicate.test(url);
  }

  @Override
  public String toString()
  {
    return "Database connector for " + dbServerType;
  }

  /**
   * Checks if the database connection options are valid, the JDBC
   * driver class can be loaded, and so on. Throws an exception if there
   * is a problem.
   *
   * @throws SchemaCrawlerException
   *         If there is a problem with creating connection options.
   */
  void checkDatabaseConnectionOptions()
    throws SchemaCrawlerException
  {
    newDatabaseConnectionOptions(new SingleUseUserCredentials(), null);
  }

}
