/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.crawl;


import static schemacrawler.filter.FilterFactory.grepRoutinesFilter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import schemacrawler.filter.NamedObjectFilter;
import schemacrawler.schema.Routine;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class RoutinesReducer
{

  private final SchemaCrawlerOptions options;

  public RoutinesReducer(final SchemaCrawlerOptions options)
  {
    this.options = options;
  }

  public void filter(final NamedObjectList<MutableRoutine> allRoutines)
  {
    final Collection<MutableRoutine> filteredRoutines = doFilter(allRoutines);
    for (final MutableRoutine routine: allRoutines)
    {
      if (!filteredRoutines.contains(routine))
      {
        allRoutines.remove(routine);
      }
    }
  }

  private Collection<MutableRoutine> doFilter(final NamedObjectList<MutableRoutine> allRoutines)
  {
    final NamedObjectFilter<Routine> routineFilter = grepRoutinesFilter(options);

    final Set<MutableRoutine> filteredRoutines = new HashSet<>();
    for (final MutableRoutine routine: allRoutines)
    {
      if (routineFilter.include(routine))
      {
        filteredRoutines.add(routine);
      }
    }

    return filteredRoutines;
  }

}
