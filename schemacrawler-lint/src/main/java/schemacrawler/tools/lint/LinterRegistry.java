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

package schemacrawler.tools.lint;


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

/**
 * Linter registry for mapping linters by id.
 *
 * @author Sualeh Fatehi
 */
public final class LinterRegistry
  implements Iterable<String>
{

  private static Map<String, Linter> loadLinterRegistry()
    throws SchemaCrawlerException
  {

    final List<Linter> linterProviders = new ArrayList<Linter>();

    try
    {
      final ServiceLoader<Linter> serviceLoader = ServiceLoader
        .load(Linter.class);
      for (final Linter linterRegistryEntry: serviceLoader)
      {
        final String linterId = linterRegistryEntry.getId();
        LOGGER.log(Level.FINER, "Loading linter, " + linterId + "="
                                + linterRegistryEntry.getClass().getName());
        linterProviders.add(linterRegistryEntry);
      }
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not load extended command registry",
                                       e);
    }

    final Map<String, Linter> linterRegistry = new HashMap<>();
    for (final Linter linter: linterProviders)
    {
      linterRegistry.put(linter.getId(), linter);
    }
    return linterRegistry;
  }

  private static final Logger LOGGER = Logger.getLogger(LinterRegistry.class
    .getName());

  private final Map<String, Linter> linterRegistry;

  public LinterRegistry()
    throws SchemaCrawlerException
  {
    linterRegistry = loadLinterRegistry();
  }

  public boolean hasLinter(final String linterId)
  {
    return linterRegistry.containsKey(linterId);
  }

  @Override
  public Iterator<String> iterator()
  {
    return lookupAvailableLinters().iterator();
  }

  public Linter lookupLinter(final String linterId)
  {
    if (hasLinter(linterId))
    {
      return linterRegistry.get(linterId);
    }
    else
    {
      return null;
    }
  }

  private Collection<String> lookupAvailableLinters()
  {
    final List<String> availableLinters = new ArrayList<>(linterRegistry.keySet());
    Collections.sort(availableLinters);
    return availableLinters;
  }

}
