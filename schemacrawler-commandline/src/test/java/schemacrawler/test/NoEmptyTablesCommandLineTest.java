/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.TestUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@WithTestDatabase
@ResolveTestContext
public class NoEmptyTablesCommandLineTest {

  private static final String HIDE_EMPTY_TABLES_OUTPUT = "no_empty_tables_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(HIDE_EMPTY_TABLES_OUTPUT);
  }

  @Test
  public void noEmptyTables(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {

    final String referenceFile = testContext.testMethodName() + ".txt";

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.maximum.name());
    argsMap.put("--no-info", "true");
    argsMap.put("--load-row-counts", "true");
    argsMap.put("--no-empty-tables", "true");

    assertThat(
        outputOf(
            commandlineExecution(
                connectionInfo,
                SchemaTextDetailType.schema.name(),
                argsMap,
                TextOutputFormat.text)),
        hasSameContentAs(classpathResource(HIDE_EMPTY_TABLES_OUTPUT + referenceFile)));
  }
}
