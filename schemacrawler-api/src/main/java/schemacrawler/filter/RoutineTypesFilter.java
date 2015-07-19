/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
package schemacrawler.filter;


import java.util.Collection;
import java.util.function.Predicate;

import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class RoutineTypesFilter
  implements Predicate<Routine>
{

  private final Collection<RoutineType> routineTypes;

  public RoutineTypesFilter(final SchemaCrawlerOptions options)
  {
    if (options != null)
    {
      routineTypes = options.getRoutineTypes();
    }
    else
    {
      routineTypes = null;
    }
  }

  /**
   * Check for routine limiting rules.
   *
   * @param routine
   *        Routine to check
   * @return Whether the routine should be included
   */
  @Override
  public boolean test(final Routine routine)
  {
    final boolean include;

    if (routineTypes != null)
    {
      include = routineTypes.contains(routine.getRoutineType());
    }
    else
    {
      include = true;
    }

    return include;
  }

}
