/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.test.integration.utility.SnowflakeTestUtility;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;

@Disabled("Under development")
@DisableLogging
@HeavyDatabaseTest("snowflake")
@Testcontainers(disabledWithoutDocker = true)
public class SnowflakeTest extends BaseAdditionalDatabaseTest {

  @Container
  private final JdbcDatabaseContainer<?> dbContainer = SnowflakeTestUtility.newSnowflakeContainer();

  @BeforeEach
  public void createDatabase() {

    try {
      Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
    } catch (final Exception e) {
      fail("Could not load the Snowflake JDBC driver", e);
    }

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    createDataSource(
        dbContainer.getJdbcUrl(), dbContainer.getUsername(), dbContainer.getPassword());

    createDatabase("/snowflake.scripts.txt");
  }

  @Test
  public void testSnowflakeWithConnection() throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("BOOKS"))
            .tableTypes("TABLE", "VIEW");
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder().toConfig());

    final Path outputPath =
        Path.of(
            outputOf(executableExecution(getConnectionSource(), executable)).getResourceString());
    final String commandOutput = Files.readString(outputPath);
    assertThat(commandOutput, containsString("AUTHORS"));
  }
}
