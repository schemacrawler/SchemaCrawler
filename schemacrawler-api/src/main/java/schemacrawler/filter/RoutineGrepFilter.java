/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.filter;

import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineParameter;
import schemacrawler.schemacrawler.GrepOptions;
import us.fatehi.utility.string.StringFormat;

class RoutineGrepFilter implements Predicate<Routine> {

  private static final Logger LOGGER = Logger.getLogger(RoutineGrepFilter.class.getName());

  private final InclusionRule grepColumnInclusionRule;
  private final InclusionRule grepDefinitionInclusionRule;
  private final boolean invertMatch;

  public RoutineGrepFilter(final GrepOptions options) {
    invertMatch = options.isGrepInvertMatch();

    grepColumnInclusionRule = options.grepRoutineParameterInclusionRule();
    grepDefinitionInclusionRule = options.grepDefinitionInclusionRule();
  }

  /**
   * Special case for "grep" like functionality. Handle table if a table column inclusion rule is
   * found, and at least one column matches the rule.
   *
   * @param routine Table to check
   * @return Whether the column should be included
   */
  @Override
  public boolean test(final Routine routine) {
    final boolean checkIncludeForColumns = grepColumnInclusionRule != null;
    final boolean checkIncludeForDefinitions = grepDefinitionInclusionRule != null;

    if (!checkIncludeForColumns && !checkIncludeForDefinitions) {
      return true;
    }

    boolean includeForColumns = false;
    boolean includeForDefinitions = false;
    for (final RoutineParameter<?> parameter : routine.getParameters()) {
      if (checkIncludeForColumns && grepColumnInclusionRule.test(parameter.getFullName())) {
        includeForColumns = true;
        break;
      }
      if (checkIncludeForDefinitions && grepDefinitionInclusionRule.test(parameter.getRemarks())) {
        includeForDefinitions = true;
        break;
      }
    }
    // Additional include checks for definitions
    if (checkIncludeForDefinitions) {
      if (grepDefinitionInclusionRule.test(routine.getRemarks())) {
        includeForDefinitions = true;
      }
      if (grepDefinitionInclusionRule.test(routine.getDefinition())) {
        includeForDefinitions = true;
      }
    }

    boolean include =
        checkIncludeForColumns && includeForColumns
            || checkIncludeForDefinitions && includeForDefinitions;
    if (invertMatch) {
      include = !include;
    }

    if (!include) {
      LOGGER.log(Level.FINE, new StringFormat("Excluding routine <%s>", routine));
    }

    return include;
  }
}
