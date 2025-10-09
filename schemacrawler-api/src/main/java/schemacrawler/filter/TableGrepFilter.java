/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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

  private final GrepOptions options;

  public TableGrepFilter(final GrepOptions options) {
    this.options = requireNonNull(options, "No grep options provided");
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
    final boolean checkIncludeForTables = options.isGrepTables();
    final boolean checkIncludeForColumns = options.isGrepColumns();
    final boolean checkIncludeForDefinitions = options.isGrepDefinitions();

    if (!checkIncludeForTables && !checkIncludeForColumns && !checkIncludeForDefinitions) {
      if (options.grepInvertMatch()) {
        LOGGER.log(
            Level.FINE,
            new StringFormat(
                "Ignoring the invert match setting for table <%s>, "
                    + "since no inclusion rules are set",
                table));
      }
      return true;
    }

    boolean includeForTables = checkIncludeForTables(table);
    boolean includeForColumns = checkIncludeForColumns(table);
    boolean includeForDefinitions = checkIncludeForDefinitions(table);

    boolean include =
        checkIncludeForTables && includeForTables
            || checkIncludeForColumns && includeForColumns
            || checkIncludeForDefinitions && includeForDefinitions;
    if (options.grepInvertMatch()) {
      include = !include;
    }

    if (!include) {
      LOGGER.log(Level.FINE, new StringFormat("Excluding table <%s>", table));
    }
    return include;
  }

  private boolean checkIncludeForColumns(final Table table) {

    final InclusionRule grepColumnInclusionRule = options.grepColumnInclusionRule();

    final List<Column> columns = table.getColumns();
    if (columns.isEmpty()) {
      return true;
    }
    for (final Column column : columns) {
      if (grepColumnInclusionRule != null && grepColumnInclusionRule.test(column.getFullName())) {
        return true;
      }
    }
    return false;
  }

  private boolean checkIncludeForDefinitions(final Table table) {

    final InclusionRule grepDefinitionInclusionRule = options.grepDefinitionInclusionRule();

    if (grepDefinitionInclusionRule != null) {
      if (grepDefinitionInclusionRule.test(table.getRemarks())
          || grepDefinitionInclusionRule.test(table.getDefinition())) {
        return true;
      }
      for (final Trigger trigger : table.getTriggers()) {
        if (grepDefinitionInclusionRule.test(trigger.getActionStatement())) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean checkIncludeForTables(final Table table) {
    return options.isGrepTables() && options.grepTableInclusionRule().test(table.getFullName());
  }
}
