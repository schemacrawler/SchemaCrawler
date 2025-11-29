/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static schemacrawler.test.utility.LintTestUtility.executableLint;

import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.extensions.AssertNoSystemErrOutput;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
public class LintSqlTest {

  @Test
  public void executableLintSQLReport(final DatabaseConnectionSource dataSource) throws Exception {
    executableLint(
        dataSource, "/schemacrawler-linter-configs-sql.yaml", null, "executableLintSQLReport");
  }
}
