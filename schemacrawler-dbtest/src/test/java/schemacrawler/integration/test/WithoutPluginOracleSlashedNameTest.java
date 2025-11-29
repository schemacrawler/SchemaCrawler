/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.test.integration.utility.OracleTestUtility.newOracleContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.WithSystemProperty;

@DisableLogging
@HeavyDatabaseTest("oracle")
@Testcontainers(disabledWithoutDocker = true)
@ResolveTestContext
public class WithoutPluginOracleSlashedNameTest extends BaseAdditionalDatabaseTest {

  @Container private final JdbcDatabaseContainer<?> dbContainer = newOracleContainer();

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    final Map<String, String> urlx = new HashMap<>();
    urlx.put("restrictGetTables", "true");
    urlx.put("useFetchSizeWithLongColumn", "true");

    createDataSource(dbContainer.getJdbcUrl(), "SYS AS SYSDBA", dbContainer.getPassword(), urlx);

    createDatabase("/oracle.scripts.txt");
  }

  @Test
  @DisplayName("Issue #628 - retrieve table and columns names with a slash or dot")
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "oracle")
  public void slashedName() throws Exception {

    final Connection connection = getConnection();
    try (final Statement stmt = connection.createStatement()) {
      stmt.execute(
          """
          CREATE TABLE "A/B" (I INT)
          """);
      stmt.execute(
          """
          CREATE TABLE CD ("E/F" INT)
          """);
      stmt.execute(
          """
          CREATE TABLE "G.H" (J INT)
          """);
      stmt.execute(
          """
          CREATE TABLE KL ("M.N" INT)
          """);
      // Auto-commited
    }

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("BOOKS"))
            .tableTypes("TABLE");
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final RuntimeException executionRuntimeException =
        assertThrows(
            RuntimeException.class, () -> getCatalog(getDataSource(), schemaCrawlerOptions));
    final SQLException cause = (SQLException) executionRuntimeException.getCause().getCause();
    // ORA-01424: missing or illegal character following the escape character
    assertThat(cause.getSQLState(), is("22025"));
  }
}
