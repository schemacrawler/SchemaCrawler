/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.integration.test.utility.MySQLTestUtility.newMySQLContainer;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.javaVersion;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
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
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;

@HeavyDatabaseTest("mysql")
@Testcontainers
@ResolveTestContext
public class WithoutPluginMySQLTest extends BaseAdditionalDatabaseTest {

  @Container
  private final JdbcDatabaseContainer<?> dbContainer =
      newMySQLContainer().withUsername("schemacrawler").withDatabaseName("books");

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    // Use default connection properties from MySQLDatabaseConnector
    final Properties connectionProperties = new Properties();
    connectionProperties.put("nullNamePatternMatchesAll", "true");
    connectionProperties.put("getProceduresReturnsFunctions", "false");
    connectionProperties.put("noAccessToProcedureBodies", "true");
    connectionProperties.put("logger", "Jdk14Logger");
    connectionProperties.put("dumpQueriesOnException", "true");
    connectionProperties.put("dumpMetadataOnColumnNotFound", "true");
    connectionProperties.put("maxQuerySizeToLog", "4096");
    connectionProperties.put("disableMariaDbDriver", "true");
    connectionProperties.put("useInformationSchema", "true");

    final StringBuilder connectionPropertiesString = new StringBuilder();
    connectionProperties.entrySet().stream()
        .forEach(
            entry ->
                connectionPropertiesString
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(";"));

    createDataSource(
        dbContainer.getJdbcUrl(),
        dbContainer.getUsername(),
        dbContainer.getPassword(),
        connectionPropertiesString.toString());

    createDatabase("/mysql.scripts.txt");
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "mysql")
  public void testMySQLWithConnection() throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("books"))
            .includeAllSequences()
            .includeAllSynonyms()
            .includeAllRoutines();
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.showDatabaseInfo().showJdbcDriverInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    // -- Schema output tests for "details" command
    final SchemaCrawlerExecutable executableDetails = new SchemaCrawlerExecutable("details");
    executableDetails.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executableDetails.setAdditionalConfiguration(
        SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    final String expectedResource = String.format("testMySQLWithConnection.%s.txt", javaVersion());
    assertThat(
        outputOf(executableExecution(getDataSource(), executableDetails)),
        hasSameContentAs(classpathResource(expectedResource)));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "mysql")
  public void testMySQLPortable(final TestContext testContext) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("books"))
            .includeAllSequences()
            .includeAllSynonyms()
            .includeAllRoutines();
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.noInfo().portableNames();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();
    final Config config = SchemaTextOptionsBuilder.builder(textOptions).toConfig();

    // -- Schema output tests
    final SchemaCrawlerExecutable executableDetails = new SchemaCrawlerExecutable("schema");
    executableDetails.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executableDetails.setAdditionalConfiguration(config);

    final String expectedResource = testContext.testMethodName() + ".txt";
    assertThat(
        outputOf(executableExecution(getDataSource(), executableDetails)),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}