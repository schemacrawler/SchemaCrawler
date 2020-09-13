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
import static schemacrawler.tools.executable.commandline.PluginCommand.newDatabasePluginCommand;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.utility.PropertiesUtility;
import us.fatehi.utility.ioresource.InputResource;

public abstract class DatabaseConnector
  implements Options
{

  public static final DatabaseConnector UNKNOWN =
    new UnknownDatabaseConnector();

  private final InputResource configResource;
  private final DatabaseServerType dbServerType;
  private final BiConsumer<InformationSchemaViewsBuilder, Connection>
    informationSchemaViewsBuildProcess;
  private final BiConsumer<SchemaRetrievalOptionsBuilder, Connection>
  schemaRetrievalOptionsBuildProcess;

  protected DatabaseConnector(final DatabaseServerType dbServerType,
                              final InputResource configResource,
                              final BiConsumer<InformationSchemaViewsBuilder, Connection> informationSchemaViewsBuildProcess,
                              final BiConsumer<SchemaRetrievalOptionsBuilder, Connection>
  schemaRetrievalOptionsBuildProcess)
  {
    this.dbServerType =
      requireNonNull(dbServerType, "No database server type provided");

    this.configResource =
      requireNonNull(configResource, "No config resource provided");

    this.informationSchemaViewsBuildProcess =
        requireNonNull(informationSchemaViewsBuildProcess, "No information schema views build process provided");
    
    this.schemaRetrievalOptionsBuildProcess =
        requireNonNull(schemaRetrievalOptionsBuildProcess, "No schema retrieval options build process provided");
  }

  /**
   * Gets the complete bundled database configuration set. This is useful in
   * building the SchemaCrawler options.
   */
  public final Config getConfig()
  {
    // configResource is not null - checked in the constructor
    return PropertiesUtility.loadConfig(configResource);
  }

  public DatabaseServerType getDatabaseServerType()
  {
    return dbServerType;
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
    final InformationSchemaViews informationSchemaViews =
      InformationSchemaViewsBuilder
        .builder()
        .withFunction(informationSchemaViewsBuildProcess, connection)
        .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
      SchemaRetrievalOptionsBuilder
        .builder()
        .withDatabaseServerType(dbServerType)
        .withInformationSchemaViews(informationSchemaViews)
        .fromConnnection(connection);
    
    // Allow database plugins to intercept and do further customization
    schemaRetrievalOptionsBuildProcess.accept(schemaRetrievalOptionsBuilder, connection);

    return schemaRetrievalOptionsBuilder;
  }

  /**
   * Creates a datasource for connecting to a database.
   *
   * @param connectionUrl
   *   Database connection URL
   */
  public DatabaseConnectionSource newDatabaseConnectionSource(final String connectionUrl)
    throws SchemaCrawlerException
  {
    requireNonNull(connectionUrl,
                   "No database connection URL provided");

    final DatabaseConnectionSource connectionOptions =
      new DatabaseConnectionSource(connectionUrl);

    return connectionOptions;
  }
  
  public DatabaseConnectionSource newDatabaseConnectionSource(final String host,
      final Integer port, final String database, final Map<String, String> urlx)
      throws SchemaCrawlerException
  {
    final String connectionUrl = constructConnectionUrl(host,
        port, database, urlx);

    return newDatabaseConnectionSource(connectionUrl);
  }

  protected abstract String constructConnectionUrl(String host,
      Integer port, String database, Map<String, String> urlx)
    throws SchemaCrawlerException;

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
      newDatabasePluginCommand(dbServerType.getDatabaseSystemIdentifier(),
                               "** Connect to "
                               + dbServerType.getDatabaseSystemName());
    return pluginCommand;
  }

  protected abstract Predicate<String> supportsUrlPredicate();

}
