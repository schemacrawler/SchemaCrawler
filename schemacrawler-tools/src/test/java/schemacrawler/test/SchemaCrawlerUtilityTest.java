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

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.test.utility.CaptureLogs;
import schemacrawler.test.utility.CapturedLogs;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSourceUtility;

@WithTestDatabase
public class SchemaCrawlerUtilityTest {

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  @CaptureLogs
  public void getCatalog(final DatabaseConnectionSource dataSource, final CapturedLogs logs)
      throws Exception {
    final Catalog catalog = SchemaCrawlerUtility.getCatalog(dataSource, newSchemaCrawlerOptions());
    assertThat(catalog, is(not(nullValue())));
    final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
    assertThat("Schema count does not match", schemas, arrayWithSize(6));
  }

  @Test
  public void getCatalogClosedConnection(final Connection connection) throws Exception {
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSourceUtility.newTestDatabaseConnectionSource(connection);
    connection.close();
    assertThrows(
        DatabaseAccessException.class,
        () -> SchemaCrawlerUtility.getCatalog(dataSource, newSchemaCrawlerOptions()));
  }

  @Test
  public void getCatalogMissingPlugin(final DatabaseConnectionSource dataSource) throws Exception {
    final InternalRuntimeException exception =
        assertThrows(
            InternalRuntimeException.class,
            () -> SchemaCrawlerUtility.getCatalog(dataSource, newSchemaCrawlerOptions()));
    assertThat(exception.getMessage(), containsString("hsqldb"));
  }

  @Test
  public void getResultsColumns() throws Exception {
    final ResultSet results = mock(ResultSet.class);
    when(results.getMetaData()).thenThrow(SQLException.class);

    assertThrows(
        DatabaseAccessException.class, () -> SchemaCrawlerUtility.getResultsColumns(results));
  }

  @Test
  public void getResultsColumns(final DatabaseConnectionSource dataSource) throws Exception {
    try (final Connection connection = dataSource.get();
        final ResultSet results = connection.getMetaData().getCatalogs()) {
      final ResultsColumns resultsColumns = SchemaCrawlerUtility.getResultsColumns(results);
      assertThat(resultsColumns, is(not(nullValue())));
      final String columnsListAsString = resultsColumns.getColumnsListAsString();
      assertThat(
          columnsListAsString,
          is("PUBLIC.INFORMATION_SCHEMA.INFORMATION_SCHEMA_CATALOG_NAME.CATALOG_NAME"));
    }
  }

  @Test
  public void getResultsColumnsClosedResults(final DatabaseConnectionSource dataSource)
      throws Exception {
    try (final Connection connection = dataSource.get(); ) {
      final ResultSet results = connection.getMetaData().getCatalogs();
      results.close();

      assertThrows(
          DatabaseAccessException.class, () -> SchemaCrawlerUtility.getResultsColumns(results));
    }
  }
}
