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
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.databaseconnector.UnknownDatabaseConnector;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.datasource.DatabaseServerType;
import us.fatehi.utility.readconfig.SystemPropertiesConfig;

/** SchemaCrawler utility methods. */
@UtilityMarker
public final class DatabaseConnectorUtility {

  private static final Logger LOGGER = Logger.getLogger(DatabaseConnectorUtility.class.getName());

  public static DatabaseConnector findDatabaseConnector(final Connection connection) {

    requireNonNull(connection, "No database connection provided");

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

    // Get database connection URL
    final String url = getConnectionUrl(connection);
    if (isBlank(url)) {
      return dbConnector;
    }

    final boolean useMatchedDatabasePlugin = useMatchedDatabasePlugin(url, databaseServerType);
    if (!useMatchedDatabasePlugin) {
      dbConnector = UnknownDatabaseConnector.UNKNOWN;
    }
    return dbConnector;
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
      final String url, final DatabaseServerType dbServerType) {

    // Extract database server type
    final String urlDBServerType = extractDatabaseServerTypeFromUrl(url);
    if (isBlank(urlDBServerType)) {
      return true;
    }

    // Find out what is matched
    final boolean dbConnectorPresent =
        urlDBServerType.equalsIgnoreCase(dbServerType.getDatabaseSystemIdentifier());

    final String withoutDatabasePlugin =
        new SystemPropertiesConfig().getStringValue("SC_WITHOUT_DATABASE_PLUGIN");
    final boolean useWithoutDatabasePlugin =
        urlDBServerType.equalsIgnoreCase(withoutDatabasePlugin);

    // Throw exception if plugin is needed, but not found
    if (!dbConnectorPresent && !useWithoutDatabasePlugin) {
      throw new InternalRuntimeException(
          """
          Add the SchemaCrawler database plugin for <%s> to the CLASSPATH for
          %s
          or set
          SC_WITHOUT_DATABASE_PLUGIN=%s
          either as an environmental variable or as a Java system property
          """
              .formatted(urlDBServerType, url, urlDBServerType));
    }

    final boolean useMatchedDatabasePlugin = dbConnectorPresent && !useWithoutDatabasePlugin;

    return useMatchedDatabasePlugin;
  }

  private DatabaseConnectorUtility() {
    // Prevent instantiation
  }
}
