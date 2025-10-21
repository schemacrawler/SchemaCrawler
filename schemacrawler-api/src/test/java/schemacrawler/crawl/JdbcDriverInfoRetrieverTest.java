/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.utility.datasource.DatabaseServerType;
import us.fatehi.utility.property.Property;

@WithTestDatabase
@ResolveTestContext
public class JdbcDriverInfoRetrieverTest {

  private MutableCatalog catalog;

  @Test
  public void jdbcDriverInfo(final Connection connection) throws Exception {
    new DatabaseServerType("hsqldb-1", "HyperSQL");
    verifyJdbcDriverInfoRetrieval();
  }

  @BeforeEach
  public void loadBaseCatalog(final Connection connection) throws SQLException {
    final ConnectionInfoBuilder connectionInfoBuilder = ConnectionInfoBuilder.builder(connection);
    final MutableDatabaseInfo databaseInfo =
        (MutableDatabaseInfo) connectionInfoBuilder.buildDatabaseInfo();
    final MutableJdbcDriverInfo jdbcDriverInfo =
        (MutableJdbcDriverInfo) connectionInfoBuilder.buildJdbcDriverInfo();

    catalog = new MutableCatalog("database_info_test", databaseInfo, jdbcDriverInfo);
    assertThat(catalog.getColumnDataTypes(), is(empty()));
    assertThat(catalog.getSchemas(), is(empty()));
    assertThat(catalog.getJdbcDriverInfo().getDriverClassName(), is("org.hsqldb.jdbc.JDBCDriver"));
  }

  private void verifyJdbcDriverInfoRetrieval() throws SQLException {
    assertThat(catalog.getJdbcDriverInfo().getDriverClassName(), is("org.hsqldb.jdbc.JDBCDriver"));

    final JdbcDriverInfo jdbcDriverInfo = catalog.getJdbcDriverInfo();
    assertThat(jdbcDriverInfo.getProductName(), is("HSQL Database Engine Driver"));
    assertThat(jdbcDriverInfo.getProductVersion(), is("2.7.4"));

    final List<Property> driverProperties = new ArrayList<>(jdbcDriverInfo.getDriverProperties());
    assertThat(driverProperties, hasSize(0));
  }
}
