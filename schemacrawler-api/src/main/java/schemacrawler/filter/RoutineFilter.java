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
package schemacrawler.filter;


import java.util.Collection;

import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class RoutineFilter
  implements NamedObjectFilter<Routine>
{

  private final InclusionRule schemaInclusionRule;
  private final InclusionRule routineInclusionRule;
  private final Collection<RoutineType> routineTypes;

  public RoutineFilter(final SchemaCrawlerOptions options)
  {
    if (options != null)
    {
      schemaInclusionRule = options.getSchemaInclusionRule();
      routineInclusionRule = options.getRoutineInclusionRule();
      routineTypes = options.getRoutineTypes();
    }
    else
    {
      schemaInclusionRule = null;
      routineInclusionRule = null;
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
  public boolean include(final Routine routine)
  {
    boolean include = true;

    if (include && schemaInclusionRule != null)
    {
      include = schemaInclusionRule.include(routine.getSchema().getFullName());
    }
    if (include && routineInclusionRule != null)
    {
      include = routineInclusionRule.include(routine.getFullName());
    }
    if (include && routineTypes != null)
    {
      include = routineTypes.contains(routine.getRoutineType());
    }

    return include;
  }

}
