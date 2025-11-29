/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.TableTypes;

/** SchemaCrawler options controlling inclusion/ limits. */
public record LimitOptions(
    @NonNull Map<DatabaseObjectRuleForInclusion, InclusionRule> inclusionRules,
    @NonNull TableTypes tableTypes,
    @Nullable String tableNamePattern,
    @NonNull EnumSet<RoutineType> routineTypes)
    implements Options {

  /**
   * Canonical constructor with non-null checks and defensive copy for enum collections.
   *
   * @param inclusionRules
   * @param tableTypes Table types requested for output.
   * @param tableNamePattern Table name pattern. A null value indicates do not take table pattern
   *     into account.
   * @param routineTypes Routine types requested for output.
   */
  public LimitOptions {
    requireNonNull(inclusionRules, "No inclusion rules provided");
    inclusionRules = new EnumMap<>(inclusionRules);

    tableTypes = requireNonNull(tableTypes, "No table types provided");

    requireNonNull(routineTypes, "No routine types provided");
    routineTypes = EnumSet.copyOf(routineTypes);
  }

  /**
   * Gets the inclusion rule.
   *
   * @return Inclusion rule.
   */
  public InclusionRule get(final DatabaseObjectRuleForInclusion inclusionRuleKey) {
    final InclusionRule defaultInclusionRule;
    if (inclusionRuleKey.isExcludeByDefault()) {
      defaultInclusionRule = new ExcludeAll();
    } else {
      defaultInclusionRule = new IncludeAll();
    }
    return inclusionRules.getOrDefault(inclusionRuleKey, defaultInclusionRule);
  }

  public EnumSet<RoutineType> routineTypes() {
    return EnumSet.copyOf(routineTypes);
  }

  /**
   * Gets the table name pattern. A null value indicates do not take table pattern into account.
   *
   * @return Table name pattern
   */
  public String tableNamePattern() {
    return tableNamePattern;
  }

  public boolean isExcludeAll(final DatabaseObjectRuleForInclusion inclusionRuleKey) {
    return get(inclusionRuleKey).equals(new ExcludeAll());
  }

  public boolean isIncludeAll(final DatabaseObjectRuleForInclusion inclusionRuleKey) {
    return get(inclusionRuleKey).equals(new IncludeAll());
  }
}
