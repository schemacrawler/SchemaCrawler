/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
{

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
        LOGGER.log(Level.FINER, "Loading database connector, "
                                + databaseSystemIdentifier + "="
                                + databaseConnector.getClass().getName());
        databaseConnectorRegistry.put(databaseSystemIdentifier,
                                      databaseConnector);
      }
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not load database connector registry",
                                       e);
    }

    return databaseConnectorRegistry;
  }

  private static final DatabaseConnector uknownDatabaseConnector = new DatabaseConnector(new DatabaseServerType("unknown",
                                                                                                                null),
    null,
    null,
    null)
  {
  };

  private static final Logger LOGGER = Logger
    .getLogger(DatabaseConnectorRegistry.class.getName());

  private final Map<String, DatabaseConnector> databaseConnectorRegistry;

  public DatabaseConnectorRegistry()
    throws SchemaCrawlerException
  {
    databaseConnectorRegistry = loadDatabaseConnectorRegistry();
  }

  public boolean hasDatabaseSystemIdentifier(final String databaseSystemIdentifier)
  {
    return databaseConnectorRegistry.containsKey(databaseSystemIdentifier);
  }

  public Collection<String> lookupAvailableDatabaseConnectors()
  {
    final List<String> availableDatabaseConnectors = new ArrayList<>(databaseConnectorRegistry
      .keySet());
    Collections.sort(availableDatabaseConnectors);
    return availableDatabaseConnectors;
  }

  public DatabaseConnector lookupDatabaseSystemIdentifier(final String databaseSystemIdentifier)
  {
    if (hasDatabaseSystemIdentifier(databaseSystemIdentifier))
    {
      return databaseConnectorRegistry.get(databaseSystemIdentifier);
    }
    else
    {
      return uknownDatabaseConnector;
    }
  }

}
