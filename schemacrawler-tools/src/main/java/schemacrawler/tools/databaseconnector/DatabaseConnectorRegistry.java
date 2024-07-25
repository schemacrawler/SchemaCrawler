/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Comparator.naturalOrder;
import static schemacrawler.tools.databaseconnector.UnknownDatabaseConnector.UNKNOWN;
import static us.fatehi.utility.database.DatabaseUtility.checkConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.registry.BasePluginRegistry;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/** Registry for database plugins. */
public final class DatabaseConnectorRegistry extends BasePluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(DatabaseConnectorRegistry.class.getName());

  private static DatabaseConnectorRegistry databaseConnectorRegistrySingleton;

  public static DatabaseConnectorRegistry getDatabaseConnectorRegistry() {
    if (databaseConnectorRegistrySingleton == null) {
      databaseConnectorRegistrySingleton = new DatabaseConnectorRegistry();
    }
    return databaseConnectorRegistrySingleton;
  }

  private static Map<String, DatabaseConnector> loadDatabaseConnectorRegistry() {

    // Use thread-safe map
    final Map<String, DatabaseConnector> databaseConnectorRegistry = new ConcurrentHashMap<>();

    try {
      final ServiceLoader<DatabaseConnector> serviceLoader =
          ServiceLoader.load(
              DatabaseConnector.class, DatabaseConnectorRegistry.class.getClassLoader());
      for (final DatabaseConnector databaseConnector : serviceLoader) {
        final String databaseSystemIdentifier =
            databaseConnector.getDatabaseServerType().getDatabaseSystemIdentifier();

        LOGGER.log(
            Level.CONFIG,
            new StringFormat(
                "Loading database connector, %s=%s",
                databaseSystemIdentifier, databaseConnector.getClass().getName()));
        // Put in map
        databaseConnectorRegistry.put(databaseSystemIdentifier, databaseConnector);
      }
    } catch (final Throwable e) {
      throw new InternalRuntimeException("Could not load database connector registry", e);
    }

    LOGGER.log(
        Level.CONFIG,
        new StringFormat("Loaded %d database connectors", databaseConnectorRegistry.size()));

    return databaseConnectorRegistry;
  }

  private final Map<String, DatabaseConnector> databaseConnectorRegistry;

  private DatabaseConnectorRegistry() {
    databaseConnectorRegistry = loadDatabaseConnectorRegistry();
  }

  public DatabaseConnector findDatabaseConnector(final Connection connection) {
    try {
      checkConnection(connection);
      final String url = connection.getMetaData().getURL();
      return findDatabaseConnectorFromUrl(url);
    } catch (final SQLException e) {
      return UNKNOWN;
    }
  }

  public DatabaseConnector findDatabaseConnectorFromDatabaseSystemIdentifier(
      final String databaseSystemIdentifier) {
    if (hasDatabaseSystemIdentifier(databaseSystemIdentifier)) {
      return databaseConnectorRegistry.get(databaseSystemIdentifier);
    }
    return UNKNOWN;
  }

  public DatabaseConnector findDatabaseConnectorFromUrl(final String url) {
    if (isBlank(url)) {
      return UNKNOWN;
    }

    for (final DatabaseConnector databaseConnector : databaseConnectorRegistry.values()) {
      if (databaseConnector.supportsUrl(url)) {
        return databaseConnector;
      }
    }

    return UNKNOWN;
  }

  @Override
  public Collection<PluginCommand> getHelpCommands() {
    final Collection<PluginCommand> commandLineHelpCommands = new ArrayList<>();
    for (final DatabaseConnector databaseConnector : databaseConnectorRegistry.values()) {
      commandLineHelpCommands.add(databaseConnector.getHelpCommand());
    }
    return commandLineHelpCommands;
  }

  public boolean hasDatabaseSystemIdentifier(final String databaseSystemIdentifier) {
    return databaseConnectorRegistry.containsKey(databaseSystemIdentifier);
  }

  @Override
  public Collection<PropertyName> getRegisteredPlugins() {
    final List<PropertyName> availableServers = new ArrayList<>();
    for (final DatabaseServerType serverType : getDatabaseServerTypes()) {
      final PropertyName serverDescription =
          new PropertyName(
              serverType.getDatabaseSystemIdentifier(), serverType.getDatabaseSystemName());
      availableServers.add(serverDescription);
    }
    return availableServers;
  }

  public List<DatabaseServerType> getDatabaseServerTypes() {
    final List<DatabaseServerType> databaseServerTypes = new ArrayList<>();
    for (final DatabaseConnector databaseConnector : databaseConnectorRegistry.values()) {
      databaseServerTypes.add(databaseConnector.getDatabaseServerType());
    }
    databaseServerTypes.sort(naturalOrder());
    return databaseServerTypes;
  }

  @Override
  public String getName() {
    return "SchemaCrawler database server plugins";
  }
}
