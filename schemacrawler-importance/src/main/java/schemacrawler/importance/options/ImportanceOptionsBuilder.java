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
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.text.options.BaseTextOptionsBuilder;

/** Builds importance-report options from command-line or configuration values. */
public final class ImportanceOptionsBuilder
    extends BaseTextOptionsBuilder<ImportanceOptionsBuilder, ImportanceOptions> {

  private static final String TABLE_FILTER = "table-filter";
  private static final String TABLE_FILTER_PROPERTY = "schemacrawler.importance.table-filter";

  public static ImportanceOptionsBuilder builder() {
    return new ImportanceOptionsBuilder();
  }

  InclusionRule tableInclusionRule;

  private ImportanceOptionsBuilder() {
    tableInclusionRule = new IncludeAll();
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
    return this;
  }

  @Override
  public ImportanceOptionsBuilder fromOptions(final ImportanceOptions options) {
    if (options != null) {
      super.fromOptions(options);
      tableInclusionRule = options.getTableInclusionRule();
    }
    return this;
  }

  @Override
  public ImportanceOptions toOptions() {
    return new ImportanceOptions(this);
  }

  public ImportanceOptionsBuilder withTableInclusionRule(final InclusionRule tableInclusionRule) {
    this.tableInclusionRule = tableInclusionRule == null ? new IncludeAll() : tableInclusionRule;
    return this;
  }

  public ImportanceOptionsBuilder withTableNamePattern(final String tableNamePattern) {
    tableInclusionRule = new RegularExpressionRule(tableNamePattern, "");
    return this;
  }
}
