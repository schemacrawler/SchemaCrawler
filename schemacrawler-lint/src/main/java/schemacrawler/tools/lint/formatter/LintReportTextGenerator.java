/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.lint.formatter;

import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.traversal.SchemaTraverser;

public final class LintReportTextGenerator extends SchemaTraverser implements LintReportGenerator {

  @Override
  public void generateLintReport(final Lints report) {
    ((LintTraversalHandler) getHandler()).setReport(report);
    traverse();
  }
}
