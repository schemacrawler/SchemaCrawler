/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import sf.util.StringFormat;

class TableGrepFilter
  implements Predicate<Table>
{

  private static final Logger LOGGER = Logger
    .getLogger(TableGrepFilter.class.getName());

  private final boolean invertMatch;
  private final InclusionRule grepColumnInclusionRule;
  private final InclusionRule grepDefinitionInclusionRule;

  public TableGrepFilter(final SchemaCrawlerOptions options)
  {
    invertMatch = options.isGrepInvertMatch();

    grepColumnInclusionRule = options.getGrepColumnInclusionRule();
    grepDefinitionInclusionRule = options.getGrepDefinitionInclusionRule();
  }

  /**
   * Special case for "grep" like functionality. Handle table if a table
   * column inclusion rule is found, and at least one column matches the
   * rule.
   *
   * @param options
   *        Options
   * @param table
   *        Table to check
   * @return Whether the column should be included
   */
  @Override
  public boolean test(final Table table)
  {
    final boolean checkIncludeForColumns = grepColumnInclusionRule != null;
    final boolean checkIncludeForDefinitions = grepDefinitionInclusionRule != null;

    if (!checkIncludeForColumns && !checkIncludeForDefinitions)
    {
      return true;
    }

    boolean includeForColumns = false;
    boolean includeForDefinitions = false;
    for (final Column column: table.getColumns())
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
      if (grepDefinitionInclusionRule.test(table.getRemarks()))
      {
        includeForDefinitions = true;
      }
      if (grepDefinitionInclusionRule.test(table.getDefinition()))
      {
        includeForDefinitions = true;
      }
      for (final Trigger trigger: table.getTriggers())
      {
        if (grepDefinitionInclusionRule.test(trigger.getActionStatement()))
        {
          includeForDefinitions = true;
          break;
        }
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
                 new StringFormat("Removing table since it does not match the grep pattern, %s",
                                  table));
    }

    return include;
  }

}
