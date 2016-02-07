/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.StringFormat;

/**
 * Linter registry for mapping linters by id.
 *
 * @author Sualeh Fatehi
 */
public final class LinterRegistry
  implements Iterable<String>
{

  private static final Logger LOGGER = Logger
    .getLogger(LinterRegistry.class.getName());

  private static Map<String, Class<Linter>> loadLinterRegistry()
    throws SchemaCrawlerException
  {

    final Map<String, Class<Linter>> linterRegistry = new HashMap<>();
    try
    {
      final ServiceLoader<Linter> serviceLoader = ServiceLoader
        .load(Linter.class);
      for (final Linter linter: serviceLoader)
      {
        final String linterId = linter.getId();
        final Class<Linter> linterClass = (Class<Linter>) linter.getClass();
        LOGGER.log(Level.FINER,
                   new StringFormat("Loading linter, %s=%s",
                                    linterId,
                                    linterClass.getName()));
        linterRegistry.put(linterId, linterClass);
      }
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not load linter registry", e);
    }

    return linterRegistry;
  }

  private final Map<String, Class<Linter>> linterRegistry;

  public LinterRegistry()
    throws SchemaCrawlerException
  {
    linterRegistry = loadLinterRegistry();
  }

  public Set<String> allRegisteredLinters()
  {
    return new TreeSet<>(linterRegistry.keySet());
  }

  public boolean hasLinter(final String linterId)
  {
    return linterRegistry.containsKey(linterId);
  }

  @Override
  public Iterator<String> iterator()
  {
    return allRegisteredLinters().iterator();
  }

  public Linter newLinter(final String linterId)
  {
    if (hasLinter(linterId))
    {
      final Class<Linter> linterClass = linterRegistry.get(linterId);
      try
      {
        final Linter linter = linterClass.newInstance();
        return linter;
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.WARNING,
                   e,
                   new StringFormat("Could not instantiate linter, %s",
                                    linterClass.getName()));
        return null;
      }
    }
    else
    {
      return null;
    }
  }

}
