/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSequenceInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSynonymInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForTableInclusion;

import java.util.function.Predicate;

import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public final class FilterFactory {

  public static Predicate<Routine> routineFilter(final SchemaCrawlerOptions options) {
    final LimitOptions limitOptions = options.getLimitOptions();
    final Predicate<Routine> routineFilter =
        new RoutineTypesFilter(limitOptions)
            .and(new DatabaseObjectFilter<>(limitOptions, ruleForRoutineInclusion))
            .and(new RoutineGrepFilter(options.getGrepOptions()));

    return routineFilter;
  }

  public static Predicate<Schema> schemaFilter(final SchemaCrawlerOptions options) {
    return new InclusionRuleFilter<>(options.getLimitOptions().get(ruleForSchemaInclusion), true);
  }

  public static Predicate<Sequence> sequenceFilter(final SchemaCrawlerOptions options) {
    return new DatabaseObjectFilter<>(options.getLimitOptions(), ruleForSequenceInclusion);
  }

  public static Predicate<Synonym> synonymFilter(final SchemaCrawlerOptions options) {
    return new DatabaseObjectFilter<>(options.getLimitOptions(), ruleForSynonymInclusion);
  }

  public static Predicate<Table> tableFilter(final SchemaCrawlerOptions options) {
    final LimitOptions limitOptions = options.getLimitOptions();
    final Predicate<Table> tableFilter =
        new TableTypesFilter(limitOptions)
            .and(new DatabaseObjectFilter<>(limitOptions, ruleForTableInclusion))
            .and(new TableGrepFilter(options.getGrepOptions()));

    return tableFilter;
  }

  private FilterFactory() {}
}
