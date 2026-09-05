/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.options;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.tools.text.options.BaseTextOptions;

/** Options controlling which tables appear in an importance report. */
public final class ImportanceOptions extends BaseTextOptions {

  private final InclusionRule tableInclusionRule;
  private final int maxCommunities;
  private final int maxCommunitySize;
  private final int maxImportantTables;

  ImportanceOptions(final ImportanceOptionsBuilder builder) {
    super(builder);
    tableInclusionRule = builder.tableInclusionRule;
    maxImportantTables = builder.maxImportantTables;
    maxCommunities = builder.maxCommunities;
    maxCommunitySize = builder.maxCommunitySize;
  }

  public int getMaxCommunities() {
    return maxCommunities;
  }

  public int getMaxCommunitySize() {
    return maxCommunitySize;
  }

  public int getMaxImportantTables() {
    return maxImportantTables;
  }

  public InclusionRule getTableInclusionRule() {
    return tableInclusionRule;
  }
}
