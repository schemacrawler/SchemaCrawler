/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static us.fatehi.test.utility.TestUtility.flattenCommandlineArgs;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.contentsOf;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.Main;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.tools.command.text.schema.options.PortableType;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.CaptureSystemStreams;
import us.fatehi.test.utility.extensions.CapturedSystemStreams;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.SystemExitException;

@CaptureSystemStreams
@ResolveTestContext
public class SqliteCommandlineTest extends BaseSqliteTest {

  private DatabaseConnector dbConnector;

  @BeforeEach
  public void setUpDatabaseConnector() {
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
  public void testSqliteMainList(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Path sqliteDbFile = createTestDatabase();

      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("--server", "sqlite");
      argsMap.put("--database", sqliteDbFile.toString());
      argsMap.put("--no-info", Boolean.TRUE.toString());
      argsMap.put("--command", "list");
      argsMap.put("--info-level", InfoLevel.minimum.name());
      argsMap.put("--output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }

    final String expectedResource = testContext.testMethodName() + ".txt";
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void testSqlitePortable(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Path sqliteDbFile = createTestDatabase();

      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("--server", "sqlite");
      argsMap.put("--database", sqliteDbFile.toString());
      argsMap.put("--no-info", Boolean.TRUE.toString());
      argsMap.put("--command", "schema");
      argsMap.put("--info-level", InfoLevel.maximum.name());
      argsMap.put("--portable", PortableType.names.name());
      argsMap.put("--output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }

    final String expectedResource = testContext.testMethodName() + ".txt";
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  public void testSqliteMainMissingDatabase(final CapturedSystemStreams streams) throws Exception {

    final Path sqliteDbFile =
        Path.of(
            System.getProperty("java.io.tmpdir"),
            RandomStringUtils.randomAlphanumeric(12).toLowerCase() + ".db");
    assertThat(
        "SQLite database should exist before the test",
        sqliteDbFile.toFile(),
        not(anExistingFile()));

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--server", "sqlite");
    argsMap.put("--database", sqliteDbFile.toString());
    argsMap.put("--no-info", Boolean.TRUE.toString());
    argsMap.put("--command", "list");
    argsMap.put("--info-level", InfoLevel.minimum.name());

    restoreSystemProperties(
        () -> {
          System.setProperty("SC_EXIT_WITH_EXCEPTION", "true");
          assertThrows(SystemExitException.class, () -> Main.main(flattenCommandlineArgs(argsMap)));
        });

    assertThat(
        "An empty SQLite database should not be created when SchemaCrawler connects",
        sqliteDbFile.toFile(),
        not(anExistingFile()));

    assertThat(
        contentsOf(streams.err()),
        matchesPattern(
            Pattern.compile(
                ".*Error: Could not connect to <.*>, for <unspecified user>, with properties"
                    + " <\\{\\}>.*",
                Pattern.DOTALL)));
    assertThat(outputOf(streams.out()), hasNoContent());
  }
}
