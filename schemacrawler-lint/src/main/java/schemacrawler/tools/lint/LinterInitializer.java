/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.lint;

import java.util.Set;

public interface LinterInitializer {

  Set<String> getRegisteredLinters();

  Linter newLinter(String linterId, LintCollector lintCollector);
}
