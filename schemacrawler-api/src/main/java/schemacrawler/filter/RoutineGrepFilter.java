/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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
