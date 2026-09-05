/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.options;

import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.text.options.BaseTextOptionsBuilder;

/** Builds importance-report options from command-line or configuration values. */
public final class ImportanceOptionsBuilder
    extends BaseTextOptionsBuilder<ImportanceOptionsBuilder, ImportanceOptions> {

  private static final String TABLE_FILTER = "table-filter";
  private static final String TABLE_FILTER_PROPERTY = "schemacrawler.importance.table-filter";
  private static final String MAX_COMMUNITIES = "max-communities";
  private static final String MAX_COMMUNITIES_PROPERTY = "schemacrawler.importance.max-communities";
  private static final String MAX_COMMUNITY_SIZE = "max-community-size";
  private static final String MAX_COMMUNITY_SIZE_PROPERTY =
      "schemacrawler.importance.max-community-size";
  private static final String MAX_IMPORTANT_TABLES = "max-important-tables";
  private static final String MAX_IMPORTANT_TABLES_PROPERTY =
      "schemacrawler.importance.max-important-tables";

  public static ImportanceOptionsBuilder builder() {
    return new ImportanceOptionsBuilder();
  }

  InclusionRule tableInclusionRule;
  int maxCommunities;
  int maxCommunitySize;
  int maxImportantTables;

  private ImportanceOptionsBuilder() {
    tableInclusionRule = new IncludeAll();
    maxImportantTables = 5;
    maxCommunities = 5;
    maxCommunitySize = 5;
  }

  @Override
  public ImportanceOptionsBuilder fromConfig(final Config config) {
    if (config == null) {
      return this;
    }
    super.fromConfig(config);
    final String property = config.containsKey(TABLE_FILTER) ? TABLE_FILTER : TABLE_FILTER_PROPERTY;
    final String filter = config.getStringValue(property, "");
    if (!filter.isBlank()) {
      tableInclusionRule = new RegularExpressionRule(filter, "");
    }
    maxImportantTables = getLimit(config, MAX_IMPORTANT_TABLES, MAX_IMPORTANT_TABLES_PROPERTY);
    maxCommunities = getLimit(config, MAX_COMMUNITIES, MAX_COMMUNITIES_PROPERTY);
    maxCommunitySize = getLimit(config, MAX_COMMUNITY_SIZE, MAX_COMMUNITY_SIZE_PROPERTY);
    return this;
  }

  @Override
  public ImportanceOptionsBuilder fromOptions(final ImportanceOptions options) {
    if (options != null) {
      super.fromOptions(options);
      tableInclusionRule = options.getTableInclusionRule();
      maxImportantTables = options.getMaxImportantTables();
      maxCommunities = options.getMaxCommunities();
      maxCommunitySize = options.getMaxCommunitySize();
    }
    return this;
  }

  @Override
  public ImportanceOptions toOptions() {
    return new ImportanceOptions(this);
  }

  public ImportanceOptionsBuilder withMaxCommunities(final int maxCommunities) {
    this.maxCommunities = maxCommunities;
    return this;
  }

  public ImportanceOptionsBuilder withMaxCommunitySize(final int maxCommunitySize) {
    this.maxCommunitySize = maxCommunitySize;
    return this;
  }

  public ImportanceOptionsBuilder withMaxImportantTables(final int maxImportantTables) {
    this.maxImportantTables = maxImportantTables;
    return this;
  }

  public ImportanceOptionsBuilder withTableInclusionRule(final InclusionRule tableInclusionRule) {
    this.tableInclusionRule = tableInclusionRule == null ? new IncludeAll() : tableInclusionRule;
    return this;
  }

  public ImportanceOptionsBuilder withTableNamePattern(final String tableNamePattern) {
    tableInclusionRule = new RegularExpressionInclusionRule(tableNamePattern);
    return this;
  }

  private static int getLimit(
      final Config config, final String commandLineKey, final String propertyKey) {
    final String key = config.containsKey(commandLineKey) ? commandLineKey : propertyKey;
    return config.containsKey(key) ? config.getIntegerValue(key, 5) : 5;
  }
}
