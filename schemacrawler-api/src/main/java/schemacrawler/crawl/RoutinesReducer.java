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


import static schemacrawler.filter.FilterFactory.routineFilter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import schemacrawler.filter.NamedObjectFilter;
import schemacrawler.filter.PassthroughFilter;
import schemacrawler.schema.Routine;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class RoutinesReducer
  implements Reducer<Routine>
{

  private final NamedObjectFilter<Routine> routineFilter;

  public RoutinesReducer(final SchemaCrawlerOptions options)
  {
    if (options == null)
    {
      routineFilter = new PassthroughFilter<Routine>();
    }
    else
    {
      routineFilter = routineFilter(options);
    }
  }

  @Override
  public void reduce(final Collection<? extends Routine> allRoutines)
  {
    if (allRoutines == null)
    {
      return;
    }
    allRoutines.retainAll(doReduce(allRoutines));
  }

  private Collection<Routine> doReduce(final Collection<? extends Routine> allRoutines)
  {
    final Set<Routine> reducedRoutines = new HashSet<>();
    for (final Routine routine: allRoutines)
    {
      if (routineFilter.test(routine))
      {
        reducedRoutines.add(routine);
      }
    }

    return reducedRoutines;
  }

}
