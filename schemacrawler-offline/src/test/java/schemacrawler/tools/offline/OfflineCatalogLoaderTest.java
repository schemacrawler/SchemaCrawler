/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.offline;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.catalogloader.CatalogLoader;
import us.fatehi.test.utility.TestDatabaseDriver;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSourceUtility;

public class OfflineCatalogLoaderTest {

  @Test
  public void connection() throws SQLException {
    final CatalogLoader catalogLoader = new OfflineCatalogLoader();

    assertThat(catalogLoader.getDataSource(), is(nullValue()));

    final Connection connection = new TestDatabaseDriver().connect("jdbc:test-db:test", null);
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSourceUtility.newTestDatabaseConnectionSource(connection);
    catalogLoader.setDataSource(dataSource);

    assertThat(catalogLoader.getDataSource(), is(not(nullValue())));
  }
}
