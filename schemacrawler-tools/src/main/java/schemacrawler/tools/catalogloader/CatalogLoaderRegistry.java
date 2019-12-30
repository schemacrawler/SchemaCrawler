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

package schemacrawler.tools.catalogloader;


import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * Registry for mapping database connectors from DatabaseConnector-line switch.
 *
 * @author Sualeh Fatehi
 */
public final class CatalogLoaderRegistry
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(CatalogLoaderRegistry.class.getName());

  private static Map<String, CatalogLoader> loadCatalogLoaderRegistry()
    throws SchemaCrawlerException
  {

    final Map<String, CatalogLoader> catalogLoaderRegistry = new HashMap<>();

    try
    {
      final ServiceLoader<CatalogLoader> serviceLoader =
        ServiceLoader.load(CatalogLoader.class);
      for (final CatalogLoader catalogLoader : serviceLoader)
      {
        final String databaseSystemIdentifier =
          catalogLoader.getDatabaseSystemIdentifier();
        try
        {
          LOGGER.log(Level.CONFIG,
                     new StringFormat("Loading catalog loader, %s=%s",
                                      databaseSystemIdentifier,
                                      catalogLoader
                                        .getClass()
                                        .getName()));

          catalogLoaderRegistry.put(databaseSystemIdentifier, catalogLoader);
        }
        catch (final Exception e)
        {
          LOGGER.log(Level.CONFIG,
                     new StringFormat("Could not load catalog loader, %s=%s",
                                      databaseSystemIdentifier,
                                      catalogLoader
                                        .getClass()
                                        .getName()),
                     e);
        }
      }
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not load catalog loader registry",
                                       e);
    }

    return catalogLoaderRegistry;
  }

  private final Map<String, CatalogLoader> catalogLoaderRegistry;

  public CatalogLoaderRegistry()
    throws SchemaCrawlerException
  {
    catalogLoaderRegistry = loadCatalogLoaderRegistry();
  }

  public boolean hasDatabaseSystemIdentifier(final String databaseSystemIdentifier)
  {
    return catalogLoaderRegistry.containsKey(databaseSystemIdentifier);
  }

  public CatalogLoader lookupCatalogLoader(final String databaseSystemIdentifier)
  {
    if (hasDatabaseSystemIdentifier(databaseSystemIdentifier))
    {
      return catalogLoaderRegistry.get(databaseSystemIdentifier);
    }
    else
    {
      return new SchemaCrawlerCatalogLoader();
    }
  }

}
