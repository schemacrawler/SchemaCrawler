/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.utility.SchemaCrawlerUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class SchemaCrawlerUtilityTest
{

  @Test
  public void getCatalog(final Connection connection)
    throws Exception
  {
    final Catalog catalog =
      SchemaCrawlerUtility.getCatalog(connection, newSchemaCrawlerOptions());
    assertThat(catalog, is(not(nullValue())));
    final Schema[] schemas = catalog
      .getSchemas()
      .toArray(new Schema[0]);
    assertThat("Schema count does not match", schemas, arrayWithSize(6));
  }

  @Test
  public void getCatalogClosedConnection(final Connection connection)
    throws Exception
  {
    connection.close();
    assertThrows(SchemaCrawlerException.class,
                 () -> SchemaCrawlerUtility.getCatalog(connection,
                                                       newSchemaCrawlerOptions()));
  }

  @Test
  public void getResultsColumns(final Connection connection)
    throws Exception
  {
    try (
      final ResultSet results = connection
        .getMetaData()
        .getCatalogs()
    )
    {
      final ResultsColumns resultsColumns =
        SchemaCrawlerUtility.getResultsColumns(results);
      assertThat(resultsColumns, is(not(nullValue())));
      final String columnsListAsString =
        resultsColumns.getColumnsListAsString();
      assertThat(columnsListAsString,
                 is(
                   "PUBLIC.INFORMATION_SCHEMA.INFORMATION_SCHEMA_CATALOG_NAME.CATALOG_NAME"));
    }
  }

  @Test
  public void getResultsColumnsClosedResults(final Connection connection)
    throws Exception
  {
    final ResultSet results = connection
      .getMetaData()
      .getCatalogs();
    results.close();

    assertThrows(SchemaCrawlerException.class,
                 () -> SchemaCrawlerUtility.getResultsColumns(results));
  }

}
