/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.nio.file.Files.lines;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.integration.test.utility.PostgreSQLTestUtility.newPostgreSQLContainer;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.tools.command.serialize.options.SerializationFormat;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputFormat;

@TestInstance(Lifecycle.PER_CLASS)
@HeavyDatabaseTest("postgresql")
@Testcontainers
@DisplayName("Test for issue #284 - support enum values")
public class PostgreSQLEnumColumnTest extends BaseAdditionalDatabaseTest {

  @Container private static final JdbcDatabaseContainer<?> dbContainer = newPostgreSQLContainer();

  @Test
  public void columnWithEnum() throws Exception {

    final SchemaCrawlerOptions schemaCrawlerOptions =
        schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final Catalog catalog = getCatalog(getDataSource(), schemaCrawlerOptions);
    final Schema schema = catalog.lookupSchema("public").orElse(null);
    assertThat(schema, notNullValue());
    final Table table = catalog.lookupTable(schema, "person").orElse(null);
    assertThat(table, notNullValue());

    final Column nameColumn = table.lookupColumn("name").orElse(null);
    assertThat(nameColumn, notNullValue());
    assertThat(nameColumn.getColumnDataType().isEnumerated(), is(false));
    assertThat(nameColumn.getColumnDataType().getEnumValues(), emptyCollectionOf(String.class));

    final Column currentMoodColumn = table.lookupColumn("current_mood").orElse(null);
    assertThat(currentMoodColumn, notNullValue());
    assertThat(currentMoodColumn.getColumnDataType().isEnumerated(), is(true));
    assertThat(
        currentMoodColumn.getColumnDataType().getEnumValues(),
        containsInAnyOrder("sad", "ok", "happy"));
  }

  @Test
  public void columnWithEnumOutputFormats() throws Exception {

    final SchemaCrawlerOptions schemaCrawlerOptions =
        schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.noInfo().noRoutines();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();
    final Config config = SchemaTextOptionsBuilder.builder(textOptions).toConfig();

    final SchemaCrawlerExecutable executable;
    executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(config);

    for (final OutputFormat outputFormat :
        new OutputFormat[] {
          DiagramOutputFormat.scdot, TextOutputFormat.text, TextOutputFormat.html
        }) {
      assertThat(
          outputOf(executableExecution(getDataSource(), executable, outputFormat)),
          hasSameContentAs(classpathResource("testColumnWithEnum." + outputFormat.getFormat())));
    }
  }

  @Test
  public void columnWithEnumSerialization() throws Exception {

    final SchemaCrawlerOptions schemaCrawlerOptions =
        schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    SchemaCrawlerExecutable executable;
    executable = new SchemaCrawlerExecutable("serialize");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);

    for (final OutputFormat outputFormat :
        new OutputFormat[] {SerializationFormat.json, SerializationFormat.yaml}) {
      final Path outputFile = executableExecution(getDataSource(), executable, outputFormat);
      final List<String> enumOutput =
          lines(outputFile).filter(line -> line.contains("happy")).collect(Collectors.toList());
      assertThat(enumOutput, is(not(empty())));
      assertThat(enumOutput.get(0), matchesPattern(".*happy.*"));
    }
  }

  @BeforeAll
  public void createDatabase() throws Exception {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    createDataSource(
        dbContainer.getJdbcUrl(), dbContainer.getUsername(), dbContainer.getPassword());

    try (final Connection connection = getConnection();
        final Statement stmt = connection.createStatement(); ) {
      stmt.execute("CREATE TYPE mood AS ENUM ('sad', 'ok', 'happy')");
      stmt.execute("CREATE TABLE person (name text, current_mood mood)");
      // Auto-commited
    }
  }
}
