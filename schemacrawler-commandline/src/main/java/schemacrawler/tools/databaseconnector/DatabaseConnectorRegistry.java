/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package schemacrawler.tools.databaseconnector;


import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
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

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.DatabaseServerType;

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
                     "Loading database connector, " + databaseSystemIdentifier
                                   + "="
                                   + databaseConnector.getClass().getName());
          // Validate that the JDBC driver is available
          databaseConnector.checkDatabaseConnectionOptions();
          // Put in map
          databaseConnectorRegistry.put(databaseSystemIdentifier,
                                        databaseConnector);
        }
        catch (final Exception e)
        {
          LOGGER.log(Level.CONFIG,
                     "Could not load database connector, "
                                   + databaseSystemIdentifier + "="
                                   + databaseConnector.getClass().getName(),
                     e);
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

  public boolean hasDatabaseSystemIdentifier(final DatabaseServerType databaseServerType)
  {
    if (databaseServerType == null
        || databaseServerType.isUnknownDatabaseSystem())
    {
      return false;
    }
    return databaseConnectorRegistry
      .containsKey(databaseServerType.getDatabaseSystemIdentifier());
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

  public DatabaseConnector lookupDatabaseSystemIdentifier(final Connection connection)
  {
    for (final DatabaseConnector databaseConnector: databaseConnectorRegistry
      .values())
    {
      if (databaseConnector.getDatabaseServerType().isUnknownDatabaseSystem())
      {
        continue;
      }
      try
      {
        if (databaseConnector.isConnectionForConnector(connection))
        {
          return databaseConnector;
        }
      }
      catch (final SchemaCrawlerException e)
      {
        // Ignore and continue
        continue;
      }
    }
    return DatabaseConnector.UNKNOWN;
  }

  public DatabaseConnector lookupDatabaseSystemIdentifier(final String databaseSystemIdentifier)
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
      LOGGER.log(Level.CONFIG, "Registered JDBC drivers: " + drivers);
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
