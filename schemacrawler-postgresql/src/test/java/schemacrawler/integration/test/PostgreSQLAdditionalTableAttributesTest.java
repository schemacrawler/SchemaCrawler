/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
import static us.fatehi.test.integration.utility.PostgreSQLTestUtility.newPostgreSQL11Container;
import static us.fatehi.utility.database.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.server.postgresql.PostgreSQLDatabaseConnector;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;

@DisableLogging
@HeavyDatabaseTest("postgresql")
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("Test for issue #258 on GitHub")
@Disabled("Only for PostgreSQL 11")
public class PostgreSQLAdditionalTableAttributesTest extends BaseAdditionalDatabaseTest {

  @Container private final JdbcDatabaseContainer<?> dbContainer = newPostgreSQL11Container();

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    createDataSource(
        dbContainer.getJdbcUrl(), dbContainer.getUsername(), dbContainer.getPassword());
  }

  @Test
  @DisplayName("Test additional table attributes")
  public void testAdditionalTableAttibutes() throws Exception {
    try (final Connection connection = getConnection();
        Statement stmt = connection.createStatement(); ) {
      stmt.execute("CREATE TABLE AIRCRAFT (NAME VARCHAR(100)) WITH OIDS");
      stmt.execute("INSERT INTO AIRCRAFT VALUES ('Boeing 747')");
      // Auto-commited
    }

    final SchemaCrawlerOptions options = schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final Connection connection = checkConnection(getConnection());
    final DatabaseConnector postgreSQLDatabaseConnector = new PostgreSQLDatabaseConnector();

    final SchemaRetrievalOptions schemaRetrievalOptions =
        postgreSQLDatabaseConnector.getSchemaRetrievalOptionsBuilder(connection).toOptions();

    final SchemaCrawler schemaCrawler =
        new SchemaCrawler(getDataSource(), schemaRetrievalOptions, options);
    final Catalog catalog = schemaCrawler.crawl();

    final Collection<Table> tables = catalog.getTables();
    assertThat("Did not retrieve all tables", tables.size(), equalTo(1));

    final Table table = tables.stream().findFirst().get();
    assertThat("Got wrong table", table.getName(), equalToIgnoringCase("AIRCRAFT"));
    assertThat("Got no oim attributes", table.getAttributes().get("RELHASOIDS"), is(true));

    final List<Column> columns = table.getColumns();
    assertThat("Did not retrieve all table columns", columns.size(), equalTo(1));

    final Column column = columns.stream().findFirst().get();
    assertThat("Got wrong column", column.getName(), equalToIgnoringCase("NAME"));
    assertThat(
        "Got wrong column data type",
        column.getColumnDataType().getDatabaseSpecificTypeName(),
        equalToIgnoringCase("VARCHAR"));
  }
}
