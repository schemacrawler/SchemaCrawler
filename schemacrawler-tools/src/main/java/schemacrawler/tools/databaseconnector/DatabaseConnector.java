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
package schemacrawler.tools.databaseconnector;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.executable.commandline.PluginCommand.newDatabasePluginCommand;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.UserCredentials;

public abstract class DatabaseConnector implements Options {

  public static final DatabaseConnector UNKNOWN = new UnknownDatabaseConnector();

  private final DatabaseServerType dbServerType;
  private final Predicate<String> supportsUrl;
  private final BiConsumer<InformationSchemaViewsBuilder, Connection>
      informationSchemaViewsBuildProcess;
  private final BiConsumer<SchemaRetrievalOptionsBuilder, Connection>
      schemaRetrievalOptionsBuildProcess;
  private final Consumer<LimitOptionsBuilder> limitOptionsBuildProcess;
  private final Supplier<DatabaseConnectionSourceBuilder> dbConnectionSourceBuildProcess;

  protected DatabaseConnector(
      final DatabaseServerType dbServerType,
      final Predicate<String> supportsUrl,
      final BiConsumer<InformationSchemaViewsBuilder, Connection>
          informationSchemaViewsBuildProcess,
      final BiConsumer<SchemaRetrievalOptionsBuilder, Connection>
          schemaRetrievalOptionsBuildProcess,
      final Consumer<LimitOptionsBuilder> limitOptionsBuildProcess,
      final Supplier<DatabaseConnectionSourceBuilder> connectionSourceBuildProcess) {
    this.dbServerType = requireNonNull(dbServerType, "No database server type provided");

    this.supportsUrl = requireNonNull(supportsUrl, "No predicate for URL support provided");

    this.informationSchemaViewsBuildProcess =
        requireNonNull(
            informationSchemaViewsBuildProcess,
            "No information schema views build process provided");

    this.schemaRetrievalOptionsBuildProcess =
        requireNonNull(
            schemaRetrievalOptionsBuildProcess,
            "No schema retrieval options build process provided");

    this.limitOptionsBuildProcess =
        requireNonNull(limitOptionsBuildProcess, "No limit options build process provided");

    this.dbConnectionSourceBuildProcess =
        requireNonNull(
            connectionSourceBuildProcess, "No database connection source builder provided");
  }

  public final DatabaseServerType getDatabaseServerType() {
    return dbServerType;
  }

  public PluginCommand getHelpCommand() {

    final PluginCommand pluginCommand =
        newDatabasePluginCommand(
            dbServerType.getDatabaseSystemIdentifier(),
            "** Connect to " + dbServerType.getDatabaseSystemName());
    return pluginCommand;
  }

  /**
   * Gets the complete bundled database specific configuration set, including the SQL for
   * information schema views.
   *
   * @param connection Database connection
   */
  public final SchemaRetrievalOptionsBuilder getSchemaRetrievalOptionsBuilder(
      final Connection connection) {

    final DatabaseConnectionSourceBuilder dbConnectionSourceBuilder =
        dbConnectionSourceBuildProcess.get();
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withFunction(informationSchemaViewsBuildProcess, connection)
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder()
            .withDatabaseServerType(dbServerType)
            .withInformationSchemaViews(informationSchemaViews)
            .withConnectionInitializer(dbConnectionSourceBuilder.getConnectionInitializer())
            .fromConnnection(connection);

    // Allow database plugins to intercept and do further customization
    schemaRetrievalOptionsBuildProcess.accept(schemaRetrievalOptionsBuilder, connection);

    return schemaRetrievalOptionsBuilder;
  }

  /**
   * Creates a datasource for connecting to a database.
   *
   * @param connectionUrl Database connection URL
   */
  public DatabaseConnectionSource newDatabaseConnectionSource(
      final DatabaseConnectionOptions connectionOptions, final UserCredentials userCredentials) {
    requireNonNull(connectionOptions, "No database connection options provided");

    // Connect using connection options provided from the command-line,
    // provided configuration, and bundled configuration
    final DatabaseConnectionSource databaseConnectionSource;
    if (connectionOptions instanceof DatabaseUrlConnectionOptions) {
      final DatabaseUrlConnectionOptions databaseUrlConnectionOptions =
          (DatabaseUrlConnectionOptions) connectionOptions;
      databaseConnectionSource =
          DatabaseConnectionSources.newDatabaseConnectionSource(
              databaseUrlConnectionOptions.getConnectionUrl(), userCredentials);
    } else if (connectionOptions instanceof DatabaseServerHostConnectionOptions) {
      final DatabaseServerHostConnectionOptions serverHostConnectionOptions =
          (DatabaseServerHostConnectionOptions) connectionOptions;

      final String host = serverHostConnectionOptions.getHost();
      final Integer port = serverHostConnectionOptions.getPort();
      final String database = serverHostConnectionOptions.getDatabase();
      final Map<String, String> urlx = serverHostConnectionOptions.getUrlx();

      final DatabaseConnectionSourceBuilder dbConnectionSourceBuilder =
          dbConnectionSourceBuildProcess.get();
      dbConnectionSourceBuilder.withHost(host);
      dbConnectionSourceBuilder.withPort(port);
      dbConnectionSourceBuilder.withDatabase(database);
      dbConnectionSourceBuilder.withUrlx(urlx);
      dbConnectionSourceBuilder.withUserCredentials(userCredentials);

      databaseConnectionSource = dbConnectionSourceBuilder.build();
    } else {
      throw new ConfigurationException("Could not create new database connection source");
    }

    return databaseConnectionSource;
  }

  public final SchemaCrawlerOptions setSchemaCrawlerOptionsDefaults(
      final SchemaCrawlerOptions schemaCrawlerOptions) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().fromOptions(schemaCrawlerOptions.getLimitOptions());
    limitOptionsBuildProcess.accept(limitOptionsBuilder);

    return schemaCrawlerOptions.withLimitOptions(limitOptionsBuilder.toOptions());
  }

  public final boolean supportsUrl(final String url) {
    if (isBlank(url)) {
      return false;
    }
    return supportsUrl.test(url);
  }

  @Override
  public String toString() {
    if (dbServerType.isUnknownDatabaseSystem()) {
      return "Database connector for unknown database system type";
    } else {
      return "Database connector for " + dbServerType;
    }
  }
}
