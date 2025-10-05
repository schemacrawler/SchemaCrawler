/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.utility;

import static schemacrawler.schemacrawler.DatabaseObjectInfoRetrieval.routine;
import static schemacrawler.schemacrawler.DatabaseObjectInfoRetrieval.table;

import java.util.Optional;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schemacrawler.DatabaseObjectInfoRetrieval;
import schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion;
import schemacrawler.schemacrawler.GrepOptions;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptions;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.options.Config;

/** SchemaCrawler options builder, to build the immutable options to crawl a schema. */
public final class SchemaCrawlerOptionsConfig {

  public static GrepOptionsBuilder fromConfig(
      final GrepOptionsBuilder providedBuilder, final Config config) {

    final GrepOptionsBuilder builder;
    if (providedBuilder == null) {
      builder = GrepOptionsBuilder.builder();
    } else {
      builder = providedBuilder;
    }

    if (config == null) {
      return builder;
    }

    final String SC_GREP_TABLE_PATTERN_EXCLUDE = "schemacrawler.grep.table.pattern.exclude";
    final String SC_GREP_TABLE_PATTERN_INCLUDE = "schemacrawler.grep.table.pattern.include";
    final String SC_GREP_COLUMN_PATTERN_EXCLUDE = "schemacrawler.grep.column.pattern.exclude";
    final String SC_GREP_COLUMN_PATTERN_INCLUDE = "schemacrawler.grep.column.pattern.include";
    final String SC_GREP_DEFINITION_PATTERN_EXCLUDE =
        "schemacrawler.grep.definition.pattern.exclude";
    final String SC_GREP_DEFINITION_PATTERN_INCLUDE =
        "schemacrawler.grep.definition.pattern.include";
    final String SC_GREP_ROUTINE_PARAMETER_PATTERN_EXCLUDE =
        "schemacrawler.grep.routine.inout.pattern.exclude";
    final String SC_GREP_ROUTINE_PARAMETER_PATTERN_INCLUDE =
        "schemacrawler.grep.routine.inout.pattern.include";

    builder.includeGreppedTables(
        config
            .getOptionalInclusionRule(SC_GREP_TABLE_PATTERN_INCLUDE, SC_GREP_TABLE_PATTERN_EXCLUDE)
            .orElse(null));
    builder.includeGreppedColumns(
        config
            .getOptionalInclusionRule(
                SC_GREP_COLUMN_PATTERN_INCLUDE, SC_GREP_COLUMN_PATTERN_EXCLUDE)
            .orElse(null));
    builder.includeGreppedRoutineParameters(
        config
            .getOptionalInclusionRule(
                SC_GREP_ROUTINE_PARAMETER_PATTERN_INCLUDE,
                SC_GREP_ROUTINE_PARAMETER_PATTERN_EXCLUDE)
            .orElse(null));
    builder.includeGreppedDefinitions(
        config
            .getOptionalInclusionRule(
                SC_GREP_DEFINITION_PATTERN_INCLUDE, SC_GREP_DEFINITION_PATTERN_EXCLUDE)
            .orElse(null));

    return builder;
  }

  public static LimitOptionsBuilder fromConfig(
      final LimitOptionsBuilder providedBuilder, final Config config) {
    final LimitOptionsBuilder builder;
    if (providedBuilder == null) {
      builder = LimitOptionsBuilder.builder();
    } else {
      builder = providedBuilder;
    }

    if (config == null) {
      return builder;
    }

    for (final DatabaseObjectRuleForInclusion ruleForInclusion :
        DatabaseObjectRuleForInclusion.values()) {
      final Optional<InclusionRule> optionalInclusionRule =
          config.getOptionalInclusionRule(
              getIncludePatternProperty(ruleForInclusion),
              getExcludePatternProperty(ruleForInclusion));

      if (optionalInclusionRule.isPresent()) {
        final InclusionRule inclusionRule = optionalInclusionRule.get();
        builder.include(ruleForInclusion, inclusionRule);
      }
    }

    final String tableTypes =
        config.getStringValue(getLimitTypesProperty(table), "BASE TABLE,TABLE,VIEW");
    builder.tableTypes(tableTypes);
    final String routineTypes =
        config.getStringValue(getLimitTypesProperty(routine), "PROCEDURE,FUNCTION");
    builder.routineTypes(routineTypes);

    return builder;
  }

  public static LoadOptionsBuilder fromConfig(
      final LoadOptionsBuilder providedBuilder, final Config config) {

    final LoadOptionsBuilder builder;
    if (providedBuilder == null) {
      builder = LoadOptionsBuilder.builder();
    } else {
      builder = providedBuilder;
    }

    if (config == null) {
      return builder;
    }

    final String SC_LOAD_MAX_THREADS = "schemacrawler.load.max_threads";

    builder.withMaxThreads(config.getIntegerValue(SC_LOAD_MAX_THREADS, 5));

    return builder;
  }

  public static SchemaCrawlerOptions fromConfig(
      final SchemaCrawlerOptions providedOptions, final Config config) {
    SchemaCrawlerOptions schemaCrawlerOptions;
    if (providedOptions == null) {
      schemaCrawlerOptions = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    } else {
      schemaCrawlerOptions = providedOptions;
    }

    if (config == null) {
      return schemaCrawlerOptions;
    }

    // Load only number of thread for load options
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().fromOptions(schemaCrawlerOptions.loadOptions());
    final LoadOptions loadOptions =
        SchemaCrawlerOptionsConfig.fromConfig(loadOptionsBuilder, config).toOptions();
    schemaCrawlerOptions = schemaCrawlerOptions.withLoadOptions(loadOptions);

    // Load only inclusion rules for limit options
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().fromOptions(schemaCrawlerOptions.limitOptions());
    final LimitOptions limitOptions =
        SchemaCrawlerOptionsConfig.fromConfig(limitOptionsBuilder, config).toOptions();
    schemaCrawlerOptions = schemaCrawlerOptions.withLimitOptions(limitOptions);

    // Load only inclusion rules for grep options
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().fromOptions(schemaCrawlerOptions.grepOptions());
    final GrepOptions grepOptions =
        SchemaCrawlerOptionsConfig.fromConfig(grepOptionsBuilder, config).toOptions();
    return schemaCrawlerOptions.withGrepOptions(grepOptions);
  }

  private static String getExcludePatternProperty(
      final DatabaseObjectRuleForInclusion ruleForInclusion) {
    return "schemacrawler.%s.pattern.exclude".formatted(ruleForInclusion.getKey());
  }

  private static String getIncludePatternProperty(
      final DatabaseObjectRuleForInclusion ruleForInclusion) {
    return "schemacrawler.%s.pattern.include".formatted(ruleForInclusion.getKey());
  }

  private static String getLimitTypesProperty(
      final DatabaseObjectInfoRetrieval databaseObjectType) {
    return "schemacrawler.%s.types".formatted(databaseObjectType.name());
  }
}
