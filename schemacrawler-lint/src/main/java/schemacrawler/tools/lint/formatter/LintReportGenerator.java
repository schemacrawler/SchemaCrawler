/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint.formatter;

import schemacrawler.tools.lint.Lints;

public interface LintReportGenerator {

  void generateLintReport(Lints report);
}
