/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schema.Property;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class JdbcDriverInfoRetrieverTest {

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve JDBC driver info")
  public void jdbcDriverInfo(final Connection connection) throws Exception {
    final DatabaseServerType databaseServerType = new DatabaseServerType("hsqldb-1", "HyperSQL");
    verifyJdbcDriverInfoRetrieval(connection, databaseServerType);

    assertThat(catalog.getJdbcDriverInfo().getDriverClassName(), is("org.hsqldb.jdbc.JDBCDriver"));
  }

  @Test
  @DisplayName("Retrieve JDBC driver info with overridden driver class")
  public void jdbcDriverInfoOverridden(final TestContext testContext, final Connection connection)
      throws Exception {
    final DatabaseServerType databaseServerType =
        new DatabaseServerType(
            "hsqldb-2", "HyperSQL with extended driver", "org.hsqldb.jdbcDriver");
    verifyJdbcDriverInfoRetrieval(connection, databaseServerType);

    assertThat(catalog.getJdbcDriverInfo().getDriverClassName(), is("org.hsqldb.jdbcDriver"));
  }

  @Test
  @DisplayName("Retrieve JDBC driver info with an unknown driver class name")
  public void jdbcDriverInfoOverriddenBad(final Connection connection) throws Exception {
    final DatabaseServerType databaseServerType =
        new DatabaseServerType("hsqldb-3", "HyperSQL", "com.example.BadDriverClass");
    verifyJdbcDriverInfoRetrieval(connection, databaseServerType);

    assertThat(catalog.getJdbcDriverInfo().getDriverClassName(), is("org.hsqldb.jdbc.JDBCDriver"));
  }

  @BeforeEach
  public void loadBaseCatalog(final Connection connection) throws SQLException {
    catalog = new MutableCatalog("database_info_test");
    assertThat(catalog.getColumnDataTypes(), is(empty()));
    assertThat(catalog.getSchemas(), is(empty()));
    assertThat(catalog.getJdbcDriverInfo().getDriverClassName(), is(""));
  }

  private void verifyJdbcDriverInfoRetrieval(
      final Connection connection, final DatabaseServerType databaseServerType)
      throws SQLException {
    assertThat(catalog.getJdbcDriverInfo().getDriverClassName(), is(""));

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder().withDatabaseServerType(databaseServerType);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final DatabaseInfoRetriever databaseInfoRetriever =
        new DatabaseInfoRetriever(retrieverConnection, catalog, options);
    databaseInfoRetriever.retrieveJdbcDriverInfo();

    final MutableJdbcDriverInfo jdbcDriverInfo = catalog.getJdbcDriverInfo();
    assertThat(jdbcDriverInfo.getProductName(), is("HSQL Database Engine Driver"));
    assertThat(jdbcDriverInfo.getProductVersion(), is("2.5.1"));

    final List<Property> driverProperties = new ArrayList<>(jdbcDriverInfo.getDriverProperties());
    assertThat(driverProperties, hasSize(0));
  }
}
