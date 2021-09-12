/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.ResultsCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.DatabaseUtility;
import us.fatehi.utility.PropertiesUtility;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.ObjectToStringFormat;
import us.fatehi.utility.string.StringFormat;

/**
 * SchemaCrawler utility methods.
 *
 * @author sfatehi
 */
@UtilityMarker
public final class SchemaCrawlerUtility {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerUtility.class.getName());

  /**
   * Crawls a database, and returns a catalog.
   *
   * @param connection Live database connection.
   * @param schemaCrawlerOptions Options.
   * @return Database catalog.
   * @throws SchemaCrawlerException On an exception.
   */
  public static Catalog getCatalog(
      final Connection connection, final SchemaCrawlerOptions schemaCrawlerOptions)
      throws SchemaCrawlerException {
    checkConnection(connection);
    LOGGER.log(Level.CONFIG, new ObjectToStringFormat(schemaCrawlerOptions));

    final SchemaRetrievalOptions schemaRetrievalOptions = matchSchemaRetrievalOptions(connection);

    return getCatalog(connection, schemaRetrievalOptions, schemaCrawlerOptions, new Config());
  }

  public static Catalog getCatalog(
      final Connection connection,
      final SchemaRetrievalOptions schemaRetrievalOptions,
      final SchemaCrawlerOptions schemaCrawlerOptions,
      final Config additionalConfig)
      throws SchemaCrawlerException {

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
   * @throws SchemaCrawlerException On an exception.
   */
  public static ResultsColumns getResultsColumns(final ResultSet resultSet)
      throws SchemaCrawlerException {
    try {
      // NOTE: Some JDBC drivers like SQLite may not work with closed
      // result-sets
      checkResultSet(resultSet);
      final ResultsCrawler resultSetCrawler = new ResultsCrawler(resultSet);
      final ResultsColumns resultsColumns = resultSetCrawler.crawl();
      return resultsColumns;
    } catch (final SQLException e) {
      throw new SchemaCrawlerException("Could not retrieve result-set metadata", e);
    }
  }

  /**
   * Returns database specific options using an existing SchemaCrawler database plugin.
   *
   * @return SchemaRetrievalOptions
   * @throws SchemaCrawlerException On an exception.
   */
  public static SchemaRetrievalOptions matchSchemaRetrievalOptions(final Connection connection)
      throws SchemaCrawlerException {
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
   * @throws SchemaCrawlerException On an exception.
   */
  private static SchemaRetrievalOptionsBuilder buildSchemaRetrievalOptions(
      final Connection connection) throws SchemaCrawlerException {
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

  private static void checkConnection(final Connection connection) throws SchemaCrawlerException {
    try {
      DatabaseUtility.checkConnection(connection);
    } catch (final SQLException e) {
      throw new SchemaCrawlerException("Bad database connection", e);
    }
  }

  private static void checkResultSet(final ResultSet resultSet) throws SchemaCrawlerException {
    try {
      DatabaseUtility.checkResultSet(resultSet);
    } catch (final SQLException e) {
      throw new SchemaCrawlerException("Bad result-set", e);
    }
  }

  private static void logConnection(final Connection connection) {
    if (connection == null || !LOGGER.isLoggable(Level.INFO)) {
      return;
    }
    try {
      final DatabaseMetaData dbMetaData = connection.getMetaData();
      final String connectionUrl = dbMetaData.getURL();

      LOGGER.log(
          Level.INFO,
          new StringFormat(
              "Connected to %n%s %s %nusing JDBC driver %n%s %s%nwith %n\"%s\"",
              dbMetaData.getDatabaseProductName(),
              dbMetaData.getDatabaseProductVersion(),
              dbMetaData.getDriverName(),
              dbMetaData.getDriverVersion(),
              connectionUrl));

      // Log JDBC driver class name. There is no reliable way to get the driver class, so this is a
      // best guess. The JDBC connection (including the Driver may have been loaded using another
      // classloader, and so may not be available with the driver manager.
      try {
        final Driver driver = DriverManager.getDriver(connectionUrl);
        if (driver != null) {
          final String driverClassName = driver.getClass().getName();
          LOGGER.log(Level.INFO, "JDBC driver class name, " + driverClassName);
        }
      } catch (final SQLException e) {
        LOGGER.log(Level.CONFIG, "Database connection may have be provided on another classloader");
      }
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, "Could not log connection information", e);
    }
  }

  private SchemaCrawlerUtility() {
    // Prevent instantiation
  }
}
