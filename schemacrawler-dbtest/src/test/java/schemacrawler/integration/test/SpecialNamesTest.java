/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.utility.database.SqlScript;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisableLogging
@ResolveTestContext
@HeavyDatabaseTest
@Testcontainers
public class SpecialNamesTest extends BaseAdditionalDatabaseTest {

  private final DockerImageName imageName = DockerImageName.parse(MariaDBContainer.NAME);

  @Container
  private final JdbcDatabaseContainer<?> dbContainer =
      new MariaDBContainer<>(imageName.withTag("10.7.3"))
          .withCommand(
              "mysqld", "--lower_case_table_names=1", "--log_bin_trust_function_creators=1")
          .withUsername("schemacrawler")
          .withDatabaseName("books");

  @BeforeEach
  public void createDatabase() throws SQLException {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    // IMPORTANT: Use root user, since permissions are not granted to the new databases created by
    // the script. Also do not verify the server, and allow public key retrieval
    final String connectionUrl =
        dbContainer.getJdbcUrl()
            + "?verifyServerCertificate=false"
            + "&allowPublicKeyRetrieval=true";
    createDataSource(connectionUrl, "root", dbContainer.getPassword());

    // Note: The database connection needs to be closed for the new schemas to be recognized
    try (Connection connection = getConnection()) {
      SqlScript.executeScriptFromResource("/special-names.sql", connection);
    }
  }

  @Test
  public void specialNames(final TestContext testContext) throws Exception {

    final Identifiers identifiers =
        IdentifiersBuilder.builder().withIdentifierQuoteString("`").toOptions();
    final String catalogName = identifiers.quoteName("some-new-database");
    final String tableName = identifiers.quoteName("some-people");

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(catalogName))
            .includeTables(new RegularExpressionInclusionRule(".*\\." + tableName));
    final SchemaInfoLevelBuilder schemaInfoLevelBuilder =
        SchemaInfoLevelBuilder.builder().withInfoLevel(InfoLevel.standard);
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(schemaInfoLevelBuilder.toOptions());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final DatabaseConnectionSource dataSource = getDataSource();

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.noInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("schema");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    final String expectedResource = testContext.testMethodFullName();
    assertThat(
        outputOf(executableExecution(dataSource, executable)),
        hasSameContentAs(classpathResource(expectedResource)));
  }
}
