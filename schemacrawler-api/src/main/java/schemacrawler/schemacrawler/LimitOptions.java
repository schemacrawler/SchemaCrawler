/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.TableTypes;
import us.fatehi.utility.ObjectToString;

/** SchemaCrawler options. */
public final class LimitOptions implements Options {

  private final Map<DatabaseObjectRuleForInclusion, InclusionRule> inclusionRules;
  private final EnumSet<RoutineType> routineTypes;
  private final String tableNamePattern;
  private final TableTypes tableTypes;

  LimitOptions(
      final Map<DatabaseObjectRuleForInclusion, InclusionRule> inclusionRules,
      final TableTypes tableTypes,
      final String tableNamePattern,
      final EnumSet<RoutineType> routineTypes) {
    this.inclusionRules = requireNonNull(inclusionRules, "No inclusion rules provided");

    this.tableTypes = requireNonNull(tableTypes, "No table types provided");
    this.tableNamePattern = tableNamePattern;

    requireNonNull(routineTypes, "No routine types provided");
    this.routineTypes = EnumSet.copyOf(routineTypes);
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

  public Collection<RoutineType> getRoutineTypes() {
    return EnumSet.copyOf(routineTypes);
  }

  /**
   * Gets the table name pattern. A null value indicates do not take table pattern into account.
   *
   * @return Table name pattern
   */
  public String getTableNamePattern() {
    return tableNamePattern;
  }

  /**
   * Returns the table types requested for output. This can be null, if all supported table types
   * are required in the output.
   *
   * @return All table types requested for output
   */
  public TableTypes getTableTypes() {
    return tableTypes;
  }

  public boolean isExcludeAll(final DatabaseObjectRuleForInclusion inclusionRuleKey) {
    return get(inclusionRuleKey).equals(new ExcludeAll());
  }

  public boolean isIncludeAll(final DatabaseObjectRuleForInclusion inclusionRuleKey) {
    return get(inclusionRuleKey).equals(new IncludeAll());
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
