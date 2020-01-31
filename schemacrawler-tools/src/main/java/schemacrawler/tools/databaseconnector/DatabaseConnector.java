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
package schemacrawler.tools.databaseconnector;


import static java.util.Objects.requireNonNull;
import static schemacrawler.plugin.EnumDataTypeHelper.NO_OP_ENUM_DATA_TYPE_HELPER;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.iosource.InputResource;
import schemacrawler.utility.PropertiesUtility;

public abstract class DatabaseConnector
  implements Options
{

  protected static final DatabaseConnector UNKNOWN = new DatabaseConnector()
  {
    @Override
    protected Predicate<String> supportsUrlPredicate()
    {
      return url -> false;
    }
  };
  private final InputResource configResource;
  private final DatabaseServerType dbServerType;
  private final BiConsumer<InformationSchemaViewsBuilder, Connection>
    informationSchemaViewsBuilderForConnection;
  private final EnumDataTypeHelper enumDataTypeHelper;

  protected DatabaseConnector(final DatabaseServerType dbServerType,
                              final InputResource configResource,
                              final BiConsumer<InformationSchemaViewsBuilder, Connection> informationSchemaViewsBuilderForConnection,
                              final EnumDataTypeHelper enumDataTypeHelper)
  {
    this.dbServerType =
      requireNonNull(dbServerType, "No database server type provided");

    this.configResource =
      requireNonNull(configResource, "No config resource provided");

    this.informationSchemaViewsBuilderForConnection =
      informationSchemaViewsBuilderForConnection;

    this.enumDataTypeHelper =
      requireNonNull(enumDataTypeHelper, "No database server type provided");
  }

  /**
   * Constructor for unknown databases. Bypass the null-checks of the main
   * constructor
   */
  private DatabaseConnector()
  {
    dbServerType = DatabaseServerType.UNKNOWN;
    configResource = null;
    informationSchemaViewsBuilderForConnection = null;
    enumDataTypeHelper = NO_OP_ENUM_DATA_TYPE_HELPER;
  }

  /**
   * Gets the complete bundled database configuration set. This is useful in
   * building the SchemaCrawler options.
   */
  public final Config getConfig()
  {
    if (configResource == null)
    {
      return new Config();
    }
    return PropertiesUtility.loadConfig(configResource);
  }

  public DatabaseServerType getDatabaseServerType()
  {
    return dbServerType;
  }

  public EnumDataTypeHelper getEnumDataTypeHelper()
  {
    return enumDataTypeHelper;
  }

  /**
   * Gets the complete bundled database specific configuration set, including
   * the SQL for information schema views.
   *
   * @param connection
   *   Database connection
   */
  public SchemaRetrievalOptionsBuilder getSchemaRetrievalOptionsBuilder(final Connection connection)
  {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
      SchemaRetrievalOptionsBuilder
        .builder()
        .withDatabaseServerType(dbServerType)
        .withEnumDataTypeHelper(enumDataTypeHelper)
        .withInformationSchemaViewsForConnection(
          informationSchemaViewsBuilderForConnection,
          connection)
        .fromConnnection(connection);

    return schemaRetrievalOptionsBuilder;
  }

  /**
   * Creates a datasource for connecting to a database. Additional connection
   * options are provided, from the command-line, and configuration file.
   *
   * @param databaseConnectorOptions
   *   Configuration from the command-line
   */
  public DatabaseConnectionSource newDatabaseConnectionSource(final DatabaseConnectorOptions databaseConnectorOptions)
    throws SchemaCrawlerException
  {
    requireNonNull(databaseConnectorOptions,
                   "No database connection options provided");

    final DatabaseConnectionSource connectionOptions =
      databaseConnectorOptions.toDatabaseConnectionSource(getConfig());

    return connectionOptions;
  }

  public final boolean supportsUrl(final String url)
  {
    if (isBlank(url))
    {
      return false;
    }
    return supportsUrlPredicate().test(url);
  }

  @Override
  public String toString()
  {
    if (dbServerType.isUnknownDatabaseSystem())
    {
      return "Database connector for unknown database system type";
    }
    else
    {
      return "Database connector for " + dbServerType;
    }
  }

  public PluginCommand getHelpCommand()
  {

    final PluginCommand pluginCommand =
      new PluginCommand(dbServerType.getDatabaseSystemIdentifier(),
                        "** Connect to "
                        + dbServerType.getDatabaseSystemName());
    return pluginCommand;
  }

  protected abstract Predicate<String> supportsUrlPredicate();

}
