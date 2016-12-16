/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static sf.util.DatabaseUtility.checkConnection;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.StringFormat;

/**
 * Registry for mapping database connectors from DatabaseConnector-line
 * switch.
 *
 * @author Sualeh Fatehi
 */
public final class DatabaseConnectorRegistry
  implements Iterable<String>
{

  private static final Logger LOGGER = Logger
    .getLogger(DatabaseConnectorRegistry.class.getName());

  private static Map<String, DatabaseConnector> loadDatabaseConnectorRegistry()
    throws SchemaCrawlerException
  {

    final Map<String, DatabaseConnector> databaseConnectorRegistry = new HashMap<>();

    try
    {
      final ServiceLoader<DatabaseConnector> serviceLoader = ServiceLoader
        .load(DatabaseConnector.class);
      for (final DatabaseConnector databaseConnector: serviceLoader)
      {
        final String databaseSystemIdentifier = databaseConnector
          .getDatabaseServerType().getDatabaseSystemIdentifier();
        try
        {
          LOGGER.log(Level.CONFIG,
                     new StringFormat("Loading database connector, %s=%s",
                                      databaseSystemIdentifier,
                                      databaseConnector.getClass().getName()));
          // Validate that the JDBC driver is available
          databaseConnector.checkDatabaseConnectionOptions();
          // Put in map
          databaseConnectorRegistry.put(databaseSystemIdentifier,
                                        databaseConnector);
        }
        catch (final Exception e)
        {
          LOGGER.log(Level.CONFIG,
                     e,
                     new StringFormat("Could not load database connector, %s=%s",
                                      databaseSystemIdentifier,
                                      databaseConnector.getClass().getName()));
        }
      }
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not load database connector registry",
                                       e);
    }

    return databaseConnectorRegistry;
  }

  private final Map<String, DatabaseConnector> databaseConnectorRegistry;

  public DatabaseConnectorRegistry()
    throws SchemaCrawlerException
  {
    databaseConnectorRegistry = loadDatabaseConnectorRegistry();
    logRegisteredJdbcDrivers();
  }

  public boolean hasDatabaseSystemIdentifier(final String databaseSystemIdentifier)
  {
    return databaseConnectorRegistry.containsKey(databaseSystemIdentifier);
  }

  @Override
  public Iterator<String> iterator()
  {
    return lookupAvailableDatabaseConnectors().iterator();
  }

  public DatabaseConnector lookupDatabaseConnector(final Connection connection)
  {
    try
    {
      checkConnection(connection);
      final String url = connection.getMetaData().getURL();
      return lookupDatabaseConnectorFromUrl(url);
    }
    catch (final SQLException | SchemaCrawlerException e)
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

    for (final DatabaseConnector databaseConnector: databaseConnectorRegistry
      .values())
    {
      final Pattern connectionUrlPattern = databaseConnector
        .getConnectionUrlPattern();
      if (connectionUrlPattern == null)
      {
        continue;
      }

      if (connectionUrlPattern.matcher(url).matches())
      {
        return databaseConnector;
      }
    }

    return DatabaseConnector.UNKNOWN;
  }

  private void logRegisteredJdbcDrivers()
  {
    if (!LOGGER.isLoggable(Level.CONFIG))
    {
      return;
    }

    try
    {
      final List<String> drivers = new ArrayList<>();
      for (final Driver driver: Collections.list(DriverManager.getDrivers()))
      {
        drivers.add(String.format("%s %d.%d",
                                  driver.getClass().getName(),
                                  driver.getMajorVersion(),
                                  driver.getMinorVersion()));
      }
      Collections.sort(drivers);
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Registered JDBC drivers, %s", drivers));
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.FINE, "Could not log registered JDBC drivers", e);
    }
  }

  private Collection<String> lookupAvailableDatabaseConnectors()
  {
    final List<String> availableDatabaseConnectors = new ArrayList<>(databaseConnectorRegistry
      .keySet());
    Collections.sort(availableDatabaseConnectors);
    return availableDatabaseConnectors;
  }

}
