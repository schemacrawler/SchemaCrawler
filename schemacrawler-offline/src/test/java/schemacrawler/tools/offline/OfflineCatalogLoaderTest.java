/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.offline;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.test.utility.TestDatabaseDriver;
import us.fatehi.utility.datasource.ConnectionDatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class OfflineCatalogLoaderTest {

  @Test
  public void connection() throws SQLException {
    final OfflineCatalogLoader catalogLoader =
        (OfflineCatalogLoader)
            new OfflineCatalogLoaderProvider()
                .newCommand("offlineloader", ConfigUtility.newConfig());

    assertThat(catalogLoader.getConnectionSource(), is(nullValue()));

    final Connection connection = new TestDatabaseDriver().connect("jdbc:test-db:test", null);
    final DatabaseConnectionSource connectionSource =
        new ConnectionDatabaseConnectionSource(connection);
    catalogLoader.setConnectionSource(connectionSource);

    assertThat(catalogLoader.getConnectionSource(), is(not(nullValue())));
  }
}
