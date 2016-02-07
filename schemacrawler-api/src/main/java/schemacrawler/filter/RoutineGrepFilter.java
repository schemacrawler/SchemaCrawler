/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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


import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineColumn;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import sf.util.StringFormat;

class RoutineGrepFilter
  implements Predicate<Routine>
{

  private static final Logger LOGGER = Logger
    .getLogger(RoutineGrepFilter.class.getName());

  private final boolean invertMatch;
  private final InclusionRule grepColumnInclusionRule;
  private final InclusionRule grepDefinitionInclusionRule;

  public RoutineGrepFilter(final SchemaCrawlerOptions options)
  {
    invertMatch = options.isGrepInvertMatch();

    grepColumnInclusionRule = options.getGrepRoutineColumnInclusionRule();
    grepDefinitionInclusionRule = options.getGrepDefinitionInclusionRule();
  }

  /**
   * Special case for "grep" like functionality. Handle table if a table
   * column inclusion rule is found, and at least one column matches the
   * rule.
   *
   * @param options
   *        Options
   * @param routine
   *        Table to check
   * @return Whether the column should be included
   */
  @Override
  public boolean test(final Routine routine)
  {
    final boolean checkIncludeForColumns = grepColumnInclusionRule != null;
    final boolean checkIncludeForDefinitions = grepDefinitionInclusionRule != null;

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
        if (grepColumnInclusionRule.test(column.getFullName()))
        {
          includeForColumns = true;
          break;
        }
      }
      if (checkIncludeForDefinitions)
      {
        if (grepDefinitionInclusionRule.test(column.getRemarks()))
        {
          includeForDefinitions = true;
          break;
        }
      }
    }
    // Additional include checks for definitions
    if (checkIncludeForDefinitions)
    {
      if (grepDefinitionInclusionRule.test(routine.getRemarks()))
      {
        includeForDefinitions = true;
      }
      if (grepDefinitionInclusionRule.test(routine.getDefinition()))
      {
        includeForDefinitions = true;
      }
    }

    boolean include = checkIncludeForColumns && includeForColumns
                      || checkIncludeForDefinitions && includeForDefinitions;
    if (invertMatch)
    {
      include = !include;
    }

    if (!include)
    {
      LOGGER.log(Level.FINE,
                 new StringFormat("Removing routine since it does not match the grep pattern, %s",
                                  routine));
    }

    return include;
  }

}
