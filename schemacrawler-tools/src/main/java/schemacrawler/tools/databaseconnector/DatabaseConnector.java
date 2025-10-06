/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
import us.fatehi.utility.datasource.DatabaseServerType;
import us.fatehi.utility.datasource.UserCredentials;

public abstract class DatabaseConnector implements Options {

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

    dbConnectionSourceBuildProcess =
        requireNonNull(
            connectionSourceBuildProcess, "No database connection source builder provided");
  }

  public final DatabaseServerType getDatabaseServerType() {
    return dbServerType;
  }

  public PluginCommand getHelpCommand() {

    final PluginCommand pluginCommand = newDatabasePluginCommand(dbServerType);
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
   * @param connectionOptions Connection options
   * @param userCredentials Username and password
   * @return Database connection source
   */
  public DatabaseConnectionSource newDatabaseConnectionSource(
      final DatabaseConnectionOptions connectionOptions, final UserCredentials userCredentials) {
    requireNonNull(connectionOptions, "No database connection options provided");

    // Connect using connection options provided from the command-line,
    // provided configuration, and bundled configuration
    final DatabaseConnectionSourceBuilder dbConnectionSourceBuilder;
    if (connectionOptions instanceof DatabaseUrlConnectionOptions databaseUrlConnectionOptions) {

      final String connectionUrl = databaseUrlConnectionOptions.getConnectionUrl();

      dbConnectionSourceBuilder = DatabaseConnectionSourceBuilder.builder(connectionUrl);
    } else if (connectionOptions
        instanceof DatabaseServerHostConnectionOptions serverHostConnectionOptions) {

      final String host = serverHostConnectionOptions.getHost();
      final Integer port = serverHostConnectionOptions.getPort();
      final String database = serverHostConnectionOptions.getDatabase();
      final Map<String, String> urlx = serverHostConnectionOptions.getUrlx();

      dbConnectionSourceBuilder = databaseConnectionSourceBuilder();
      dbConnectionSourceBuilder.withHost(host);
      dbConnectionSourceBuilder.withPort(port);
      dbConnectionSourceBuilder.withDatabase(database);
      dbConnectionSourceBuilder.withUrlx(urlx);

    } else {
      throw new ConfigurationException("Could not create new database connection source");
    }

    dbConnectionSourceBuilder.withUserCredentials(userCredentials);
    final DatabaseConnectionSource databaseConnectionSource = dbConnectionSourceBuilder.build();
    return databaseConnectionSource;
  }

  public final SchemaCrawlerOptions setSchemaCrawlerOptionsDefaults(
      final SchemaCrawlerOptions schemaCrawlerOptions) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().fromOptions(schemaCrawlerOptions.limitOptions());
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
    }
    return "Database connector for " + dbServerType;
  }

  protected DatabaseConnectionSourceBuilder databaseConnectionSourceBuilder() {
    return dbConnectionSourceBuildProcess.get();
  }
}
