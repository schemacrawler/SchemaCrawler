/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.GrepOptions;
import us.fatehi.utility.string.StringFormat;

class TableGrepFilter implements Predicate<Table> {

  private static final Logger LOGGER = Logger.getLogger(TableGrepFilter.class.getName());

  private final InclusionRule grepTableInclusionRule;
  private final InclusionRule grepColumnInclusionRule;
  private final InclusionRule grepDefinitionInclusionRule;
  private final boolean invertMatch;

  public TableGrepFilter(final GrepOptions options) {

    requireNonNull(options, "No grep options provided");

    invertMatch = options.isGrepInvertMatch();

    grepTableInclusionRule = options.getGrepTableInclusionRule().orElse(null);
    grepColumnInclusionRule = options.getGrepColumnInclusionRule().orElse(null);
    grepDefinitionInclusionRule = options.getGrepDefinitionInclusionRule().orElse(null);
  }

  /**
   * Special case for "grep" like functionality. Handle table if a table column inclusion rule is
   * found, and at least one column matches the rule.
   *
   * @param table Table to check
   * @return Whether the column should be included
   */
  @Override
  public boolean test(final Table table) {
    final boolean checkIncludeForTables = grepTableInclusionRule != null;
    final boolean checkIncludeForColumns = grepColumnInclusionRule != null;
    final boolean checkIncludeForDefinitions = grepDefinitionInclusionRule != null;

    if (!checkIncludeForTables && !checkIncludeForColumns && !checkIncludeForDefinitions) {
      return true;
    }

    boolean includeForTables = false;
    boolean includeForColumns = false;
    boolean includeForDefinitions = false;

    if (checkIncludeForTables && grepTableInclusionRule.test(table.getFullName())) {
      includeForTables = true;
    }

    final List<Column> columns = table.getColumns();
    // Check if info-level=minimum, and no columns were retrieved
    if (columns.isEmpty()) {
      includeForColumns = true;
      includeForDefinitions = true;
    }
    for (final Column column : columns) {
      if (checkIncludeForColumns && grepColumnInclusionRule.test(column.getFullName())) {
        includeForColumns = true;
        break;
      }
      if (checkIncludeForDefinitions && grepDefinitionInclusionRule.test(column.getRemarks())) {
        includeForDefinitions = true;
        break;
      }
    }
    // Additional include checks for definitions
    if (checkIncludeForDefinitions) {
      if (grepDefinitionInclusionRule.test(table.getRemarks())) {
        includeForDefinitions = true;
      }
      if (grepDefinitionInclusionRule.test(table.getDefinition())) {
        includeForDefinitions = true;
      }
      for (final Trigger trigger : table.getTriggers()) {
        if (grepDefinitionInclusionRule.test(trigger.getActionStatement())) {
          includeForDefinitions = true;
          break;
        }
      }
    }

    boolean include =
        checkIncludeForTables && includeForTables
            || checkIncludeForColumns && includeForColumns
            || checkIncludeForDefinitions && includeForDefinitions;
    if (invertMatch) {
      include = !include;
    }

    if (!include) {
      LOGGER.log(Level.FINE, new StringFormat("Excluding table <%s>", table));
    }
    return include;
  }
}
