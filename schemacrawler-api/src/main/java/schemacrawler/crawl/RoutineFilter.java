/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Procedure;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineColumn;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class RoutineFilter
{

  private static final Logger LOGGER = Logger.getLogger(RoutineFilter.class
    .getName());

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
    // Filter for grep
    final Set<MutableRoutine> greppedRoutines = new HashSet<MutableRoutine>();
    for (final MutableRoutine routine: allRoutines)
    {
      if (grepMatch(options, routine))
      {
        greppedRoutines.add(routine);
      }
    }

    return greppedRoutines;
  }

  /**
   * Special case for "grep" like functionality. Handle routine if a
   * routine column inclusion rule is found, and at least one column
   * matches the rule.
   * 
   * @param options
   *        Options
   * @param routine
   *        Routine to check
   * @return Whether the column should be included
   */
  private boolean grepMatch(final SchemaCrawlerOptions options,
                            final Routine routine)
  {
    final boolean invertMatch = options.isGrepInvertMatch();
    final boolean checkIncludeForColumns = options.isGrepRoutineColumns();
    final boolean checkIncludeForDefinitions = options.isGrepDefinitions();

    final InclusionRule grepRoutineColumnInclusionRule = options
      .getGrepRoutineColumnInclusionRule();
    final InclusionRule grepDefinitionInclusionRule = options
      .getGrepDefinitionInclusionRule();

    if (!checkIncludeForColumns && !checkIncludeForDefinitions)
    {
      return true;
    }

    boolean includeForColumns = false;
    boolean includeForDefinitions = false;
    for (final RoutineColumn<?> column: routine.getColumns())
    {
      if (checkIncludeForColumns)
      {
        if (grepRoutineColumnInclusionRule.include(column.getFullName()))
        {
          includeForColumns = true;
          break;
        }
      }
    }
    // Additional include checks for definitions
    if (checkIncludeForDefinitions)
    {
      if (grepDefinitionInclusionRule.include(routine.getRemarks()))
      {
        includeForDefinitions = true;
      }
      if (routine instanceof Procedure
          && grepDefinitionInclusionRule.include(((Procedure) routine)
            .getDefinition()))
      {
        includeForDefinitions = true;
      }
    }

    boolean include = includeForColumns || includeForDefinitions;
    if (invertMatch)
    {
      include = !include;
    }

    if (!include)
    {
      LOGGER.log(Level.FINE, "Removing routine " + routine
                             + " since it does not match the grep pattern");
    }

    return include;
  }

}
