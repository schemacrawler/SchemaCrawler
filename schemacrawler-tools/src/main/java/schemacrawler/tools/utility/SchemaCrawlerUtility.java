/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.utility;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import schemacrawler.crawl.ResultsCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.databaseconnector.UnknownDatabaseConnector;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.PropertiesUtility;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseServerType;
import us.fatehi.utility.string.ObjectToStringFormat;
import us.fatehi.utility.string.StringFormat;

/** SchemaCrawler utility methods. */
@UtilityMarker
public final class SchemaCrawlerUtility {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerUtility.class.getName());

  /**
   * Crawls a database, and returns a catalog.
   *
   * @param dataSource Database connection source.
   * @param schemaCrawlerOptions Options.
   * @return Database catalog.
   */
  public static Catalog getCatalog(
      final DatabaseConnectionSource dataSource, final SchemaCrawlerOptions schemaCrawlerOptions) {
    final SchemaRetrievalOptions schemaRetrievalOptions = matchSchemaRetrievalOptions(dataSource);
    return getCatalog(dataSource, schemaRetrievalOptions, schemaCrawlerOptions, new Config());
  }

  /**
   * Crawls a database, and returns a catalog.
   *
   * @param dataSource Database connection source.
   * @param schemaCrawlerOptions Options.
   * @return Database catalog.
   */
  public static Catalog getCatalog(
      final DatabaseConnectionSource dataSource,
      final SchemaRetrievalOptions schemaRetrievalOptions,
      final SchemaCrawlerOptions schemaCrawlerOptions,
      final Config additionalConfig) {

    LOGGER.log(Level.CONFIG, new ObjectToStringFormat(schemaCrawlerOptions));

    updateConnectionDataSource(dataSource, schemaRetrievalOptions);

    final CatalogLoaderRegistry catalogLoaderRegistry =
        CatalogLoaderRegistry.getCatalogLoaderRegistry();
    final CatalogLoader catalogLoader = catalogLoaderRegistry.newChainedCatalogLoader();

    LOGGER.log(Level.CONFIG, new StringFormat("Catalog loader: %s", catalogLoader));

    catalogLoader.setDataSource(dataSource);
    catalogLoader.setSchemaRetrievalOptions(schemaRetrievalOptions);
    catalogLoader.setSchemaCrawlerOptions(schemaCrawlerOptions);
    catalogLoader.setAdditionalConfiguration(additionalConfig);

    catalogLoader.loadCatalog();
    final Catalog catalog = catalogLoader.getCatalog();
    requireNonNull(catalog, "Catalog could not be retrieved");
    return catalog;
  }

  /**
   * Obtains result-set metadata from a live result-set.
   *
   * @param resultSet Live result-set.
   * @return Result-set metadata.
   */
  public static ResultsColumns getResultsColumns(final ResultSet resultSet) {
    try {
      // NOTE: Some JDBC drivers like SQLite may not work with closed
      // result-sets
      DatabaseUtility.checkResultSet(resultSet);
      final ResultsCrawler resultSetCrawler = new ResultsCrawler(resultSet);
      final ResultsColumns resultsColumns = resultSetCrawler.crawl();
      return resultsColumns;
    } catch (final SQLException e) {
      throw new DatabaseAccessException("Could not retrieve result-set metadata", e);
    }
  }

  /**
   * Returns database specific options using an existing SchemaCrawler database plugin.
   *
   * @return SchemaRetrievalOptions
   */
  public static SchemaRetrievalOptions matchSchemaRetrievalOptions(
      final DatabaseConnectionSource dataSource) {
    try (final Connection connection = dataSource.get()) {
      DatabaseUtility.checkConnection(connection);
      final DatabaseConnector dbConnector = findDatabaseConnector(connection);
      final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
          dbConnector.getSchemaRetrievalOptionsBuilder(connection);
      final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.build();
      return schemaRetrievalOptions;
    } catch (final SQLException e) {
      throw new InternalRuntimeException("Could not obtain schema retrieval options", e);
    }
  }

  /**
   * Updates the connection data source by attaching a connection initializer.
   *
   * @param dataSource Database connection source
   * @param schemaRetrievalOptions SchemaCrawler retrieval options to convey the connection
   *     initializer from the database plugin
   */
  public static void updateConnectionDataSource(
      final DatabaseConnectionSource dataSource,
      final SchemaRetrievalOptions schemaRetrievalOptions) {

    if (dataSource == null) {
      LOGGER.log(Level.CONFIG, "No database connection source provided");
      return;
    }
    if (schemaRetrievalOptions == null) {
      LOGGER.log(Level.CONFIG, "No schema retrieval options provided");
      return;
    }

    dataSource.setFirstConnectionInitializer(schemaRetrievalOptions.getConnectionInitializer());
  }

  private static String extractDatabaseServerTypeFromUrl(final String url) {
    final Pattern urlPattern = Pattern.compile("jdbc:(.*?):.*");
    final Matcher matcher = urlPattern.matcher(url);
    if (!matcher.matches()) {
      return "";
    }
    final String urlDBServerType;
    if (matcher.groupCount() == 1) {
      final String matchedDBServerType = matcher.group(1);
      if (List.of(
              "db2", "hsqldb", "mariadb", "mysql", "oracle", "postgresql", "sqlite", "sqlserver")
          .contains(matchedDBServerType)) {
        urlDBServerType = matchedDBServerType;
      } else {
        urlDBServerType = null;
      }
    } else {
      urlDBServerType = null;
    }
    if (isBlank(urlDBServerType)) {
      return "";
    }
    if ("mariadb".equals(urlDBServerType)) {
      // Special case: MariaDB is handled by the MySQL plugin
      return "mysql";
    }
    return urlDBServerType;
  }

  private static DatabaseConnector findDatabaseConnector(final Connection connection) {
    final DatabaseConnectorRegistry registry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    DatabaseConnector dbConnector = registry.findDatabaseConnector(connection);
    final DatabaseServerType databaseServerType = dbConnector.getDatabaseServerType();

    // Log SchemaCrawler database plugin being used
    if (databaseServerType.isUnknownDatabaseSystem()) {
      LOGGER.log(Level.INFO, "Not using any SchemaCrawler database plugin");
    } else {
      LOGGER.log(Level.INFO, "Using SchemaCrawler database plugin for " + databaseServerType);
    }

    final boolean useMatchedDatabasePlugin =
        useMatchedDatabasePlugin(connection, databaseServerType);
    if (!useMatchedDatabasePlugin) {
      dbConnector = UnknownDatabaseConnector.UNKNOWN;
    }
    return dbConnector;
  }

  private static String getConnectionUrl(final Connection connection) {
    requireNonNull(connection, "No connection provided");
    final String url;
    try {
      url = connection.getMetaData().getURL();
    } catch (final SQLException e) {
      LOGGER.log(Level.CONFIG, "Cannot get connection URL");
      return "";
    }
    return url;
  }

  private static boolean useMatchedDatabasePlugin(
      final Connection connection, final DatabaseServerType dbServerType) {

    // Get database connection URL
    final String url = getConnectionUrl(connection);
    if (isBlank(url)) {
      return true;
    }

    // Extract database server type
    final String urlDBServerType = extractDatabaseServerTypeFromUrl(url);
    if (isBlank(urlDBServerType)) {
      return true;
    }

    // Find out what is matched
    final boolean dbConnectorPresent =
        urlDBServerType.equalsIgnoreCase(dbServerType.getDatabaseSystemIdentifier());

    final String withoutDatabasePlugin =
        PropertiesUtility.getSystemConfigurationProperty("SC_WITHOUT_DATABASE_PLUGIN", "");
    final boolean useWithoutDatabasePlugin =
        urlDBServerType.equalsIgnoreCase(withoutDatabasePlugin);

    // Throw exception if plugin is needed, but not found
    if (!dbConnectorPresent && !useWithoutDatabasePlugin) {
      throw new InternalRuntimeException(
          """
          Add the SchemaCrawler database plugin for <%s> to the CLASSPATH for
          %s
          or set "SC_WITHOUT_DATABASE_PLUGIN=%s"
          either as an environmental variable or as a Java system property
          """
              .formatted(urlDBServerType, url, urlDBServerType));
    }

    final boolean useMatchedDatabasePlugin = dbConnectorPresent && !useWithoutDatabasePlugin;

    return useMatchedDatabasePlugin;
  }

  private SchemaCrawlerUtility() {
    // Prevent instantiation
  }
}
