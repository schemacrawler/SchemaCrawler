/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.contentsOf;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import com.ginsberg.junit.exit.SystemExitPreventedException;

import schemacrawler.Main;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

@CaptureSystemStreams
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
  public void testSqliteMain() throws Exception {
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
    assertThat(outputOf(testout), hasSameContentAs(classpathResource("sqlite.main.list.txt")));
  }

  @Test
  @ExpectSystemExitWithStatus(1)
  public void testSqliteMainMissingDatabase(final CapturedSystemStreams streams) throws Exception {

    final Path sqliteDbFile =
        Paths.get(
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

    try {
      Main.main(flattenCommandlineArgs(argsMap));
    } catch (final SystemExitPreventedException e) {

      assertThat(
          "An empty SQLite database should not be created when SchemaCrawler connects",
          sqliteDbFile.toFile(),
          not(anExistingFile()));

      final int exitCode = e.getStatusCode();
      assertThat(exitCode, is(1));
    }

    assertThat(
        contentsOf(streams.err()),
        matchesPattern(
            Pattern.compile(
                ".*Error: Could not connect to <.*>, for <unspecified user>, with properties <\\{\\}>.*",
                Pattern.DOTALL)));
    assertThat(outputOf(streams.out()), hasNoContent());
  }
}
