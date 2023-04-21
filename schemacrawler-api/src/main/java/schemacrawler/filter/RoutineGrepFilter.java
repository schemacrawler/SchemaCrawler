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

    grepColumnInclusionRule = options.getGrepRoutineParameterInclusionRule().orElse(null);
    grepDefinitionInclusionRule = options.getGrepDefinitionInclusionRule().orElse(null);
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
