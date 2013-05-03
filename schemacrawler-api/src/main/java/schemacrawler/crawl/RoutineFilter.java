/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import schemacrawler.filter.FilterFactory;
import schemacrawler.filter.NamedObjectFilter;
import schemacrawler.schema.Routine;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class RoutineFilter
{

  private final SchemaCrawlerOptions options;
  private final NamedObjectList<MutableRoutine> allRoutines;

  public RoutineFilter(final SchemaCrawlerOptions options,
                       final NamedObjectList<MutableRoutine> allRoutines)
  {
    this.options = options;
    this.allRoutines = allRoutines;
  }

  public void filter()
  {
    final Collection<MutableRoutine> filteredRoutines = doFilter();
    for (final MutableRoutine routine: allRoutines)
    {
      if (!filteredRoutines.contains(routine))
      {
        allRoutines.remove(routine);
      }
    }
  }

  private Collection<MutableRoutine> doFilter()
  {
    final NamedObjectFilter<Routine> routineFilter = FilterFactory
      .grepRoutinesFilter(options);
    final Set<MutableRoutine> greppedRoutines = new HashSet<>();
    for (final MutableRoutine routine: allRoutines)
    {
      if (routineFilter.include(routine))
      {
        greppedRoutines.add(routine);
      }
    }

    return greppedRoutines;
  }
}
