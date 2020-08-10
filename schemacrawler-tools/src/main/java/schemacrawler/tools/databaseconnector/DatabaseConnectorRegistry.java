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


import static java.util.Comparator.naturalOrder;
import static us.fatehi.utility.DatabaseUtility.checkConnection;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.SchemaCrawlerLogger;
import us.fatehi.utility.string.StringFormat;

/**
 * Registry for mapping database connectors from DatabaseConnector-line switch.
 *
 * @author Sualeh Fatehi
 */
public final class DatabaseConnectorRegistry
  implements Iterable<DatabaseServerType>
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(DatabaseConnectorRegistry.class.getName());

  public static DatabaseConnectorRegistry getDatabaseConnectorRegistry()
  {
    if (databaseConnectorRegistrySingleton == null)
    {
      databaseConnectorRegistrySingleton = new DatabaseConnectorRegistry();
    }
    return databaseConnectorRegistrySingleton;
  }

  private static Map<String, DatabaseConnector> loadDatabaseConnectorRegistry()
  {

    final Map<String, DatabaseConnector> databaseConnectorRegistry =
      new HashMap<>();

    try
    {
      final ServiceLoader<DatabaseConnector> serviceLoader =
        ServiceLoader.load(DatabaseConnector.class);
      for (final DatabaseConnector databaseConnector : serviceLoader)
      {
        final String databaseSystemIdentifier = databaseConnector
          .getDatabaseServerType()
          .getDatabaseSystemIdentifier();
        try
        {
          LOGGER.log(Level.CONFIG,
                     new StringFormat("Loading database connector, %s=%s",
                                      databaseSystemIdentifier,
                                      databaseConnector
                                        .getClass()
                                        .getName()));
          // Put in map
          databaseConnectorRegistry.put(databaseSystemIdentifier,
                                        databaseConnector);
        }
        catch (final Exception e)
        {
          LOGGER.log(Level.CONFIG,
                     new StringFormat("Could not load database connector, %s=%s",
                                      databaseSystemIdentifier,
                                      databaseConnector
                                        .getClass()
                                        .getName()),
                     e);
        }
      }
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerRuntimeException(
        "Could not load database connector registry",
        e);
    }

    return databaseConnectorRegistry;
  }

  private static void logRegisteredJdbcDrivers()
  {
    if (!LOGGER.isLoggable(Level.CONFIG))
    {
      return;
    }

    try
    {
      final StringBuilder buffer = new StringBuilder(1024);
      buffer.append("Registered JDBC drivers:");
      for (final Driver driver : Collections.list(DriverManager.getDrivers()))
      {
        buffer.append(String.format("%n%s %d.%d",
                                    driver
                                      .getClass()
                                      .getName(),
                                    driver.getMajorVersion(),
                                    driver.getMinorVersion()));
      }
      LOGGER.log(Level.CONFIG, buffer.toString());
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.FINE, "Could not log registered JDBC drivers", e);
    }
  }

  private static DatabaseConnectorRegistry databaseConnectorRegistrySingleton;
  private final Map<String, DatabaseConnector> databaseConnectorRegistry;

  private DatabaseConnectorRegistry()
  {
    databaseConnectorRegistry = loadDatabaseConnectorRegistry();
    logRegisteredJdbcDrivers();
  }

  public boolean hasDatabaseSystemIdentifier(final String databaseSystemIdentifier)
  {
    return databaseConnectorRegistry.containsKey(databaseSystemIdentifier);
  }

  @Override
  public Iterator<DatabaseServerType> iterator()
  {
    final List<DatabaseServerType> databaseServerTypes = new ArrayList<>();
    for (final DatabaseConnector databaseConnector : databaseConnectorRegistry.values())
    {
      databaseServerTypes.add(databaseConnector.getDatabaseServerType());
    }
    databaseServerTypes.sort(naturalOrder());
    return databaseServerTypes.iterator();
  }

  public DatabaseConnector lookupDatabaseConnector(final Connection connection)
  {
    try
    {
      checkConnection(connection);
      final String url = connection
        .getMetaData()
        .getURL();
      return lookupDatabaseConnectorFromUrl(url);
    }
    catch (final SQLException e)
    {
      return DatabaseConnector.UNKNOWN;
    }
  }

  public DatabaseConnector lookupDatabaseConnector(final String databaseSystemIdentifier)
  {
    if (hasDatabaseSystemIdentifier(databaseSystemIdentifier))
    {
      return databaseConnectorRegistry.get(databaseSystemIdentifier);
    }
    else
    {
      return DatabaseConnector.UNKNOWN;
    }
  }

  public DatabaseConnector lookupDatabaseConnectorFromUrl(final String url)
  {
    if (isBlank(url))
    {
      return DatabaseConnector.UNKNOWN;
    }

    for (final DatabaseConnector databaseConnector : databaseConnectorRegistry.values())
    {
      if (databaseConnector.supportsUrl(url))
      {
        return databaseConnector;
      }
    }

    return DatabaseConnector.UNKNOWN;
  }

}
