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
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.schema.IdentifierQuotingStrategy.quote_all;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static schemacrawler.utility.MetaDataUtility.getColumnsListAsString;
import static us.fatehi.test.integration.utility.PostgreSQLTestUtility.newPostgreSQLContainer;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Index;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.Identifiers;
import schemacrawler.schema.IdentifiersBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DisableLogging;
import us.fatehi.test.utility.extensions.HeavyDatabaseTest;

@DisableLogging
@HeavyDatabaseTest("postgresql")
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("Test for issue #458 - daterange index in Postgres results in NotLoadedException")
public class PostgreSQLGiSTTest extends BaseAdditionalDatabaseTest {

  private static final Identifiers identifiers =
      IdentifiersBuilder.builder()
          .withIdentifierQuotingStrategy(quote_all)
          .withIdentifierQuoteString("\"")
          .toOptions();
  @Container private final JdbcDatabaseContainer<?> dbContainer = newPostgreSQLContainer();

  @BeforeEach
  public void createDatabase() throws Exception {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    createDataSource(
        dbContainer.getJdbcUrl(), dbContainer.getUsername(), dbContainer.getPassword());

    try (final Connection connection = getConnection();
        final Statement stmt = connection.createStatement(); ) {
      stmt.execute(
          "CREATE TABLE prices (start_date date NOT NULL, end_date date NOT NULL, CONSTRAINT"
              + " exclude_dates EXCLUDE using gist (daterange(start_date,end_date,'[]') WITH &&))");
      // Auto-commited
    }
  }

  @Test
  public void tableWithGiSTConstraint() throws Exception {

    final SchemaCrawlerOptions schemaCrawlerOptions =
        schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final Catalog catalog = getCatalog(getDataSource(), schemaCrawlerOptions);
    final Schema schema = catalog.lookupSchema("public").orElse(null);
    assertThat(schema, notNullValue());
    final Table table = catalog.lookupTable(schema, "prices").orElse(null);
    assertThat(table, notNullValue());

    final List<Column> columns = table.getColumns();
    assertThat(columns.size(), is(2));
    for (final Column column : columns) {
      assertThat(column, notNullValue());
      assertThat(column.getColumnDataType().getName(), is("date"));
    }

    final Collection<Index> indexes = table.getIndexes();
    assertThat(indexes.size(), is(1));
    for (final Index index : indexes) {
      assertThat(index, notNullValue());
      final String columnsListAsString = getColumnsListAsString(index, identifiers);
      assertThat(columnsListAsString, is(""));
    }
  }
}
