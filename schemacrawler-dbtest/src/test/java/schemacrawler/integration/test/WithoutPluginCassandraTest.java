/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.integration.test.utility.CassandraTestUtility.newCassandraContainer;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.javaVersion;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;

@HeavyDatabaseTest("cassandra")
@Testcontainers
public class WithoutPluginCassandraTest extends BaseAdditionalDatabaseTest {

  @Container private final CassandraContainer dbContainer = newCassandraContainer();

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    final InetSocketAddress contactPoint = dbContainer.getContactPoint();
    final String host = contactPoint.getHostName();
    final int port = contactPoint.getPort();
    final String keyspace = "books";
    final String localDatacenter = dbContainer.getLocalDatacenter();
    final String connectionUrl =
        String.format(
            "jdbc:cassandra://%s:%d/%s?localdatacenter=%s", host, port, keyspace, localDatacenter);
    // System.out.printf("url=%s%n", connectionUrl);
    createDataSource(connectionUrl, dbContainer.getUsername(), dbContainer.getPassword());
  }

  @Test
  public void testCassandraWithConnection() throws Exception {

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(Pattern.compile("books"))
            .includeRoutines(new IncludeAll());
    final SchemaInfoLevelBuilder schemaInfoLevelBuilder =
        SchemaInfoLevelBuilder.builder().withInfoLevel(InfoLevel.maximum);
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevelBuilder(schemaInfoLevelBuilder);
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());
    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    final String expectedResource =
        String.format("testCassandraWithConnection.%s.txt", javaVersion());
    assertThat(
        outputOf(executableExecution(getDataSource(), executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}
