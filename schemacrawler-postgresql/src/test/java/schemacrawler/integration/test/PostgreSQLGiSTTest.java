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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.integration.test.utility.PostgreSQLTestUtility.newPostgreSQL15Container;
import static schemacrawler.schemacrawler.IdentifierQuotingStrategy.quote_all;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static schemacrawler.utility.MetaDataUtility.getColumnsListAsString;
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
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.HeavyDatabaseTest;

@HeavyDatabaseTest("postgresql")
@Testcontainers
@DisplayName("Test for issue #458 - daterange index in Postgres results in NotLoadedException")
public class PostgreSQLGiSTTest extends BaseAdditionalDatabaseTest {

  private static final Identifiers identifiers =
      IdentifiersBuilder.builder()
          .withIdentifierQuotingStrategy(quote_all)
          .withIdentifierQuoteString("\"")
          .toOptions();
  @Container private final JdbcDatabaseContainer<?> dbContainer = newPostgreSQL15Container();

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
          "CREATE TABLE prices ("
              + "  start_date date NOT NULL,"
              + "  end_date date NOT NULL,"
              + "  CONSTRAINT exclude_dates EXCLUDE using gist (daterange(start_date,end_date,'[]') WITH &&)"
              + ")");
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
