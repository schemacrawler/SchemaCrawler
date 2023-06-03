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
import static schemacrawler.integration.test.utility.OracleTestUtility.newOracle21Container;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.database.SqlScript;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

@HeavyDatabaseTest
@Testcontainers
@ResolveTestContext
public class Issue1139Test extends BaseOracleWithConnectionTest {

  @Container private final JdbcDatabaseContainer<?> dbContainer = newOracle21Container();

  private final Consumer<Connection> customConnectionInitializer =
      connection -> {
        try {
          // Override Oracle plugin behavior, and show schema in DDL
          DatabaseUtility.executeSql(
              connection.createStatement(),
              "{call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'EMIT_SCHEMA', TRUE)}");
        } catch (final SQLException e) {
          fail(e);
        }
      };

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    final String urlx = "restrictGetTables=true;useFetchSizeWithLongColumn=true";
    createDataSource(dbContainer.getJdbcUrl(), "SYS AS SYSDBA", dbContainer.getPassword(), urlx);
  }

  @Test
  @DisplayName("Issue #1139 - data-source - override connection initializer")
  public void showSchemaInDDLWithDataSource() throws Exception {
    final DatabaseConnectionSource dataSource = getDataSource();
    dataSource.setFirstConnectionInitializer(customConnectionInitializer);

    showSchemaInDDL(dataSource);
  }

  @Test
  @DisplayName("Issue #1139 - multiple connection source - override connection initializer")
  public void showSchemaInDDLWithMultipleConnection() throws Exception {
    System.setProperty("SC_SINGLE_THREADED", Boolean.FALSE.toString());
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSources.newDatabaseConnectionSource(
            dbContainer.getJdbcUrl(),
            new MultiUseUserCredentials("SYS AS SYSDBA", dbContainer.getPassword()));
    dataSource.setFirstConnectionInitializer(customConnectionInitializer);

    showSchemaInDDL(dataSource);
  }

  @Test
  @DisplayName("Issue #1139 - single connection source - override connection initializer")
  public void showSchemaInDDLWithSingleConnection() throws Exception {
    System.setProperty("SC_SINGLE_THREADED", Boolean.TRUE.toString());
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSources.newDatabaseConnectionSource(
            dbContainer.getJdbcUrl(),
            new MultiUseUserCredentials("SYS AS SYSDBA", dbContainer.getPassword()));
    dataSource.setFirstConnectionInitializer(customConnectionInitializer);

    showSchemaInDDL(dataSource);
  }

  private void showSchemaInDDL(final DatabaseConnectionSource dataSource) throws Exception {

    final Connection connection = getConnection();
    SqlScript.executeScriptFromResource(";,/db/books/01_schemas_C.sql", connection);
    try (final Statement stmt = connection.createStatement()) {
      stmt.execute(
          "CREATE OR REPLACE FUNCTION CustomAdd(One IN INTEGER) \n"
              + "RETURN INTEGER \n"
              + "AS \n"
              + "BEGIN\n"
              + "  RETURN One + 1; \n"
              + "END;");
      // Auto-commited
    }

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("BOOKS"))
            .includeTables(new ExcludeAll())
            .includeRoutines(new IncludeAll());
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder.noInfo();
    final SchemaTextOptions textOptions = textOptionsBuilder.toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    // -- Schema output tests
    final String classpathResource = "showSchemaInDDL.txt";
    assertThat(
        outputOf(executableExecution(dataSource, executable)),
        hasSameContentAs(classpathResource(classpathResource)));
  }
}
