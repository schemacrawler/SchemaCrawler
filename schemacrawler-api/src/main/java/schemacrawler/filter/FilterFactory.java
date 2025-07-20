/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
