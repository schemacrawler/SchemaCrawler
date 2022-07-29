/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
import org.junit.jupiter.api.Test;

import schemacrawler.schema.ConnectionInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Property;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.WithTestDatabase;

@WithTestDatabase
@ResolveTestContext
public class JdbcDriverInfoRetrieverTest {

  private MutableCatalog catalog;

  @Test
  public void jdbcDriverInfo(final Connection connection) throws Exception {
    final DatabaseServerType databaseServerType = new DatabaseServerType("hsqldb-1", "HyperSQL");
    verifyJdbcDriverInfoRetrieval(connection, databaseServerType);
  }

  @BeforeEach
  public void loadBaseCatalog(final Connection connection) throws SQLException {
    final ConnectionInfo connectionInfo = ConnectionInfoBuilder.builder(connection).build();
    catalog = new MutableCatalog("database_info_test", connectionInfo);
    assertThat(catalog.getColumnDataTypes(), is(empty()));
    assertThat(catalog.getSchemas(), is(empty()));
    assertThat(catalog.getJdbcDriverInfo().getDriverClassName(), is(""));
  }

  private void verifyJdbcDriverInfoRetrieval(
      final Connection connection, final DatabaseServerType databaseServerType)
      throws SQLException {
    assertThat(catalog.getJdbcDriverInfo().getDriverClassName(), is(""));

    final JdbcDriverInfo jdbcDriverInfo = catalog.getJdbcDriverInfo();
    assertThat(jdbcDriverInfo.getProductName(), is("HSQL Database Engine Driver"));
    assertThat(jdbcDriverInfo.getProductVersion(), is("2.7.0"));

    final List<Property> driverProperties = new ArrayList<>(jdbcDriverInfo.getDriverProperties());
    assertThat(driverProperties, hasSize(0));
  }
}
