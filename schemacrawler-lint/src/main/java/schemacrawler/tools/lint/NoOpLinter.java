/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.lint;

import java.sql.Connection;
import schemacrawler.schema.Table;
import us.fatehi.utility.property.PropertyName;

public final class NoOpLinter extends BaseLinter {

  NoOpLinter() {
    super(new PropertyName("schemacrawler.NO_OP_LINTER", ""), new LintCollector());
  }

  @Override
  public String getSummary() {
    return "No-op linter";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    // No-op
  }
}
