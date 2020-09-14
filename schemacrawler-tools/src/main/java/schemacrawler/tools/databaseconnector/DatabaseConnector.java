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
import static schemacrawler.tools.executable.commandline.PluginCommand.newDatabasePluginCommand;
import static us.fatehi.utility.Utility.isBlank;
import java.sql.Connection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
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
  private final BiConsumer<LimitOptionsBuilder, Connection>
    limitOptionsBuildProcess;  
  private final Supplier<DatabaseConnectionUrlBuilder> urlBuildProcess;

  protected DatabaseConnector(final DatabaseServerType dbServerType,
                              final InputResource configResource,
                              final BiConsumer<InformationSchemaViewsBuilder, Connection> informationSchemaViewsBuildProcess,
                              final BiConsumer<SchemaRetrievalOptionsBuilder, Connection> schemaRetrievalOptionsBuildProcess,
                              final BiConsumer<LimitOptionsBuilder, Connection> limitOptionsBuildProcess,
                              final Supplier<DatabaseConnectionUrlBuilder> urlBuildProcess)
  {
    this.dbServerType =
        requireNonNull(dbServerType, "No database server type provided");

    this.configResource =
        requireNonNull(configResource, "No config resource provided");

    this.informationSchemaViewsBuildProcess =
        requireNonNull(informationSchemaViewsBuildProcess,
            "No information schema views build process provided");

    this.schemaRetrievalOptionsBuildProcess =
        requireNonNull(schemaRetrievalOptionsBuildProcess,
            "No schema retrieval options build process provided");

    this.limitOptionsBuildProcess = requireNonNull(limitOptionsBuildProcess,
        "No limit options build process provided");

    this.urlBuildProcess =
        requireNonNull(urlBuildProcess, "No URL builder provided");
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
  
  public void setDefaultsForSchemaCrawlerOptionsBuilder(
      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder,
      final Connection connection)
  {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().fromOptions(schemaCrawlerOptionsBuilder.getLimitOptions());    
    limitOptionsBuildProcess.accept(limitOptionsBuilder, connection);
    
    schemaCrawlerOptionsBuilder.withLimitOptionsBuilder(limitOptionsBuilder);
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

    final DatabaseConnectionSource databaseConnectionSource =
      new DatabaseConnectionSource(connectionUrl);

    return databaseConnectionSource;
  }
  
  public DatabaseConnectionSource newDatabaseConnectionSource(final String host,
      final Integer port, final String database, final Map<String, String> urlx)
      throws SchemaCrawlerException
  {
    final DatabaseConnectionUrlBuilder databaseConnectionUrlBuilder = urlBuildProcess.get();
    databaseConnectionUrlBuilder.withHost(host);
    databaseConnectionUrlBuilder.withPort(port);
    databaseConnectionUrlBuilder.withDatabase(database);
    databaseConnectionUrlBuilder.withUrlx(urlx);

    final String connectionUrl = databaseConnectionUrlBuilder.toURL();
    final Map<String, String> connectionUrlx = databaseConnectionUrlBuilder.toURLx();
    final DatabaseConnectionSource databaseConnectionSource =
        new DatabaseConnectionSource(connectionUrl, connectionUrlx);
    
    return databaseConnectionSource;
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
      newDatabasePluginCommand(dbServerType.getDatabaseSystemIdentifier(),
                               "** Connect to "
                               + dbServerType.getDatabaseSystemName());
    return pluginCommand;
  }

  protected abstract Predicate<String> supportsUrlPredicate();

}
