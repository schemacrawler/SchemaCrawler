/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static schemacrawler.schema.RoutineType.function;
import static schemacrawler.schema.RoutineType.procedure;
import static schemacrawler.schema.RoutineType.unknown;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForColumnInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineParameterInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSequenceInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSynonymInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForTableInclusion;
import static us.fatehi.utility.CollectionsUtility.splitList;
import static us.fatehi.utility.EnumUtility.enumValue;
import static us.fatehi.utility.Utility.isBlank;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.TableTypes;

/** SchemaCrawler options builder, to build the immutable options to crawl a schema. */
public final class LimitOptionsBuilder
    implements OptionsBuilder<LimitOptionsBuilder, LimitOptions> {

  public static LimitOptionsBuilder builder() {
    return new LimitOptionsBuilder();
  }

  public static LimitOptions newLimitOptions() {
    return builder().toOptions();
  }

  private static EnumSet<RoutineType> defaultRoutineTypes() {
    return EnumSet.of(function, procedure);
  }

  private static TableTypes defaultTableTypes() {
    return TableTypes.from("BASE TABLE", "TABLE", "VIEW");
  }

  private final Map<DatabaseObjectRuleForInclusion, InclusionRule> inclusionRules;
  private String tableNamePattern;
  private TableTypes tableTypes;
  private EnumSet<RoutineType> routineTypes;

  /** Default options. */
  private LimitOptionsBuilder() {
    inclusionRules = new EnumMap<>(DatabaseObjectRuleForInclusion.class);

    for (final DatabaseObjectRuleForInclusion ruleForInclusion :
        DatabaseObjectRuleForInclusion.values()) {
      resetToDefault(ruleForInclusion);
    }

    tableTypes = defaultTableTypes();
    routineTypes = defaultRoutineTypes();
  }

  @Override
  public LimitOptionsBuilder fromOptions(final LimitOptions options) {
    if (options == null) {
      return this;
    }

    for (final DatabaseObjectRuleForInclusion ruleForInclusion :
        DatabaseObjectRuleForInclusion.values()) {
      inclusionRules.put(ruleForInclusion, options.get(ruleForInclusion));
    }

    tableTypes = options.tableTypes();
    tableNamePattern = options.tableNamePattern();
    routineTypes = EnumSet.copyOf(options.routineTypes());

    return this;
  }

  public LimitOptionsBuilder include(
      final DatabaseObjectRuleForInclusion ruleForInclusion, final InclusionRule inclusionRule) {
    if (inclusionRule == null) {
      resetToDefault(ruleForInclusion);
    } else {
      inclusionRules.put(ruleForInclusion, inclusionRule);
    }
    return this;
  }

  public LimitOptionsBuilder includeAllRoutines() {
    includeRoutines(new IncludeAll());
    return this;
  }

  public LimitOptionsBuilder includeAllSequences() {
    includeSequences(new IncludeAll());
    return this;
  }

  public LimitOptionsBuilder includeAllSynonyms() {
    includeSynonyms(new IncludeAll());
    return this;
  }

  public LimitOptionsBuilder includeAllTables() {
    includeTables(new IncludeAll());
    return this;
  }

  public LimitOptionsBuilder includeColumns(final InclusionRule columnInclusionRule) {
    return include(ruleForColumnInclusion, columnInclusionRule);
  }

  public LimitOptionsBuilder includeColumns(final Pattern columnPattern) {
    return include(ruleForColumnInclusion, columnPattern);
  }

  public LimitOptionsBuilder includeRoutineParameters(
      final InclusionRule routineParameterInclusionRule) {
    return include(ruleForRoutineParameterInclusion, routineParameterInclusionRule);
  }

  public LimitOptionsBuilder includeRoutineParameters(final Pattern routineParameterPattern) {
    return include(ruleForRoutineParameterInclusion, routineParameterPattern);
  }

  public LimitOptionsBuilder includeRoutines(final InclusionRule routineInclusionRule) {
    return include(ruleForRoutineInclusion, routineInclusionRule);
  }

  public LimitOptionsBuilder includeRoutines(final Pattern routinePattern) {
    return include(ruleForRoutineInclusion, routinePattern);
  }

  public LimitOptionsBuilder includeSchemas(final InclusionRule schemaInclusionRule) {
    return include(ruleForSchemaInclusion, schemaInclusionRule);
  }

  public LimitOptionsBuilder includeSchemas(final Pattern schemaPattern) {
    return include(ruleForSchemaInclusion, schemaPattern);
  }

  public LimitOptionsBuilder includeSequences(final InclusionRule sequenceInclusionRule) {
    return include(ruleForSequenceInclusion, sequenceInclusionRule);
  }

  public LimitOptionsBuilder includeSequences(final Pattern sequencePattern) {
    return include(ruleForSequenceInclusion, sequencePattern);
  }

  public LimitOptionsBuilder includeSynonyms(final InclusionRule synonymInclusionRule) {
    return include(ruleForSynonymInclusion, synonymInclusionRule);
  }

  public LimitOptionsBuilder includeSynonyms(final Pattern synonymPattern) {
    return include(ruleForSynonymInclusion, synonymPattern);
  }

  public LimitOptionsBuilder includeTables(final InclusionRule tableInclusionRule) {
    return include(ruleForTableInclusion, tableInclusionRule);
  }

  public LimitOptionsBuilder includeTables(final Pattern tablePattern) {
    return include(ruleForTableInclusion, tablePattern);
  }

  /**
   * Sets routine types from a collection of routine types.
   *
   * @param routineTypes Collection of routine types. Can be null if all supported routine types are
   *     requested.
   */
  public LimitOptionsBuilder routineTypes(final Collection<RoutineType> routineTypes) {
    if (routineTypes == null) {
      // null signifies include all routine types (except unknown)
      this.routineTypes = defaultRoutineTypes();
    } else if (routineTypes.isEmpty()) {
      this.routineTypes = EnumSet.noneOf(RoutineType.class);
    } else {
      this.routineTypes = EnumSet.copyOf(routineTypes);
    }
    return this;
  }

  /**
   * Sets routine types from a comma-separated list of routine types. A null string resets to the
   * defaults, which includes all procedures and functions.
   *
   * @param routineTypesString Comma-separated list of routine types. Can be null if all supported
   *     routine types are requested.
   */
  public LimitOptionsBuilder routineTypes(final String routineTypesString) {
    final Collection<RoutineType> routineTypesCollection;
    if (routineTypesString != null) {
      routineTypesCollection = new HashSet<>();
      final String[] routineTypeStrings = splitList(routineTypesString);
      for (final String routineTypeString : routineTypeStrings) {
        final RoutineType routineType =
            enumValue(routineTypeString.toLowerCase(Locale.ENGLISH), unknown);
        if (routineType != unknown) {
          routineTypesCollection.add(routineType);
        }
      }
    } else {
      routineTypesCollection = null;
    }

    return routineTypes(routineTypesCollection);
  }

  public LimitOptionsBuilder tableNamePattern(final String tableNamePattern) {
    if (isBlank(tableNamePattern)) {
      this.tableNamePattern = null;
    } else {
      this.tableNamePattern = tableNamePattern;
    }
    return this;
  }

  /**
   * Sets table types from a collection of table types.
   *
   * @param tableTypeStrings Collection of table types. Can be null if all supported table types are
   *     requested.
   */
  public LimitOptionsBuilder tableTypes(final Collection<String> tableTypeStrings) {
    tableTypes = TableTypes.from(tableTypeStrings);
    return this;
  }

  /**
   * Sets table types from an array of table types.
   *
   * @param tableTypeStrings Collection of table types. Can be null if all supported table types are
   *     requested.
   */
  public LimitOptionsBuilder tableTypes(final String... tableTypeStrings) {
    tableTypes = TableTypes.from(tableTypeStrings);
    return this;
  }

  /**
   * Sets table types requested for output from a comma-separated list of table types. For example:
   * TABLE,VIEW,SYSTEM_TABLE,GLOBAL TEMPORARY,ALIAS,SYNONYM
   *
   * @param tableTypesString Comma-separated list of table types. Can be null if all supported table
   *     types are requested.
   */
  public LimitOptionsBuilder tableTypes(final String tableTypesString) {
    tableTypes = TableTypes.from(tableTypesString);
    return this;
  }

  @Override
  public LimitOptions toOptions() {
    return new LimitOptions(
        new EnumMap<>(inclusionRules), tableTypes, tableNamePattern, routineTypes);
  }

  private InclusionRule getDefaultInclusionRule(
      final DatabaseObjectRuleForInclusion ruleForInclusion) {
    final InclusionRule defaultInclusionRule;
    if (ruleForInclusion.isExcludeByDefault()) {
      defaultInclusionRule = new ExcludeAll();
    } else {
      defaultInclusionRule = new IncludeAll();
    }
    return defaultInclusionRule;
  }

  private LimitOptionsBuilder include(
      final DatabaseObjectRuleForInclusion ruleForInclusion, final Pattern pattern) {
    return include(ruleForInclusion, new RegularExpressionInclusionRule(pattern));
  }

  private void resetToDefault(final DatabaseObjectRuleForInclusion ruleForInclusion) {
    inclusionRules.put(ruleForInclusion, getDefaultInclusionRule(ruleForInclusion));
  }
}
