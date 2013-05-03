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
package schemacrawler.crawl.filter;


import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class TableGrepFilter
  implements DatabaseObjectFilter<Table>
{

  private static final Logger LOGGER = Logger.getLogger(TableGrepFilter.class
    .getName());

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
  public boolean include(final Table table)
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
        if (grepColumnInclusionRule.include(column.getFullName()))
        {
          includeForColumns = true;
          break;
        }
      }
      if (checkIncludeForDefinitions)
      {
        if (grepDefinitionInclusionRule.include(column.getRemarks()))
        {
          includeForDefinitions = true;
          break;
        }
      }
    }
    // Additional include checks for definitions
    if (checkIncludeForDefinitions)
    {
      if (grepDefinitionInclusionRule.include(table.getRemarks()))
      {
        includeForDefinitions = true;
      }
      if (table instanceof View)
      {
        if (grepDefinitionInclusionRule.include(((View) table).getDefinition()))
        {
          includeForDefinitions = true;
        }
      }
      for (final Trigger trigger: table.getTriggers())
      {
        if (grepDefinitionInclusionRule.include(trigger.getActionStatement()))
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
      LOGGER.log(Level.FINE, "Removing table " + table
                             + " since it does not match the grep pattern");
    }

    return include;
  }

}
