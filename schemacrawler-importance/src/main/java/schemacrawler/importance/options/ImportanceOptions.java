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

  ImportanceOptions(final ImportanceOptionsBuilder builder) {
    super(builder);
    tableInclusionRule = builder.tableInclusionRule;
  }

  public InclusionRule getTableInclusionRule() {
    return tableInclusionRule;
  }
}
