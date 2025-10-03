/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;
import static schemacrawler.test.utility.TestUtility.javaVersion;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.Main;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import schemacrawler.tools.options.OutputFormat;

@DisableLogging
public class SqliteDistributionTest extends BaseSqliteTest {

  private DatabaseConnector dbConnector;

  @BeforeEach
  public void setup() {
    final DatabaseConnectorRegistry registry =
        DatabaseConnectorRegistry.getDatabaseConnectorRegistry();
    dbConnector = registry.findDatabaseConnectorFromDatabaseSystemIdentifier("sqlite");
  }

  @Test
  public void testIdentifierQuoteString() throws Exception {

    final Connection connection = null;
    assertThat(
        dbConnector
            .getSchemaRetrievalOptionsBuilder(connection)
            .toOptions()
            .getIdentifierQuoteString(),
        is("\""));
  }

  @Test
  public void testSqliteMain() throws Exception {
    final OutputFormat outputFormat = TextOutputFormat.text;
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Path sqliteDbFile = createTestDatabase();
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("--server", "sqlite");
      argsMap.put("--database", sqliteDbFile.toString());
      argsMap.put("--no-info", Boolean.FALSE.toString());
      argsMap.put("--command", "details");
      argsMap.put("--info-level", InfoLevel.maximum.name());
      argsMap.put("--output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }
    final String expectedResource =
        "sqlite.main.%s.%s".formatted(javaVersion(), outputFormat.getFormat());
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }
}
