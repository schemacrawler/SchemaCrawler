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

package schemacrawler.tools.utility;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.ConnectionInfoBuilder;
import schemacrawler.crawl.ResultsCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ConnectionInfo;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.PropertiesUtility;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.string.ObjectToStringFormat;
import us.fatehi.utility.string.StringFormat;

/** SchemaCrawler utility methods. */
@UtilityMarker
public final class SchemaCrawlerUtility {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerUtility.class.getName());

  /**
   * Crawls a database, and returns a catalog.
   *
   * @param connection Live database connection.
   * @param schemaCrawlerOptions Options.
   * @return Database catalog.
   */
  public static Catalog getCatalog(
      final Connection connection, final SchemaCrawlerOptions schemaCrawlerOptions) {
    checkConnection(connection);
    LOGGER.log(Level.CONFIG, new ObjectToStringFormat(schemaCrawlerOptions));

    final SchemaRetrievalOptions schemaRetrievalOptions = matchSchemaRetrievalOptions(connection);

    return getCatalog(connection, schemaRetrievalOptions, schemaCrawlerOptions, new Config());
  }

  public static Catalog getCatalog(
      final Connection connection,
      final SchemaRetrievalOptions schemaRetrievalOptions,
      final SchemaCrawlerOptions schemaCrawlerOptions,
      final Config additionalConfig) {

    final CatalogLoaderRegistry catalogLoaderRegistry = new CatalogLoaderRegistry();
    final CatalogLoader catalogLoader = catalogLoaderRegistry.newChainedCatalogLoader();

    LOGGER.log(Level.CONFIG, new StringFormat("Catalog loader: %s", catalogLoader));
    logConnection(connection);

    catalogLoader.setConnection(connection);
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
      checkResultSet(resultSet);
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
  public static SchemaRetrievalOptions matchSchemaRetrievalOptions(final Connection connection) {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        buildSchemaRetrievalOptions(connection);

    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();

    return schemaRetrievalOptions;
  }

  /**
   * Allows building of database specific options programatically, using an existing SchemaCrawler
   * database plugin as a starting point.
   *
   * @return SchemaRetrievalOptionsBuilder
   */
  private static SchemaRetrievalOptionsBuilder buildSchemaRetrievalOptions(
      final Connection connection) {
    checkConnection(connection);
    final DatabaseConnectorRegistry registry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    DatabaseConnector dbConnector = registry.findDatabaseConnector(connection);
    final DatabaseServerType databaseServerType = dbConnector.getDatabaseServerType();
    LOGGER.log(Level.INFO, "Using database plugin for " + databaseServerType);

    final String withoutDatabasePlugin =
        PropertiesUtility.getSystemConfigurationProperty("SC_WITHOUT_DATABASE_PLUGIN", "");

    if (!databaseServerType.isUnknownDatabaseSystem()
        && databaseServerType
            .getDatabaseSystemIdentifier()
            .equalsIgnoreCase(withoutDatabasePlugin)) {
      dbConnector = DatabaseConnector.UNKNOWN;
    }

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        dbConnector.getSchemaRetrievalOptionsBuilder(connection);
    return schemaRetrievalOptionsBuilder;
  }

  private static void checkConnection(final Connection connection) {
    try {
      DatabaseUtility.checkConnection(connection);
    } catch (final SQLException e) {
      throw new InternalRuntimeException("Bad database connection", e);
    }
  }

  private static void checkResultSet(final ResultSet resultSet) {
    try {
      DatabaseUtility.checkResultSet(resultSet);
    } catch (final SQLException e) {
      throw new DatabaseAccessException("Bad result-set", e);
    }
  }

  private static void logConnection(final Connection connection) {
    if (connection == null || !LOGGER.isLoggable(Level.INFO)) {
      return;
    }
    try {
      final ConnectionInfo connectionInfo = ConnectionInfoBuilder.builder(connection).build();
      LOGGER.log(Level.INFO, connectionInfo.toString());
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, "Could not log connection information");
      LOGGER.log(Level.FINE, "Could not log connection information", e);
    }
  }

  private SchemaCrawlerUtility() {
    // Prevent instantiation
  }
}
