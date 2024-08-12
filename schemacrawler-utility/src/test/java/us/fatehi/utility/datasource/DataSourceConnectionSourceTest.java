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

package us.fatehi.utility.datasource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DataSourceConnectionSourceTest {

  @Test
  void get() throws Exception {
    // Arrange
    final DataSource dataSource = Mockito.mock(DataSource.class);
    final Connection connection = Mockito.mock(Connection.class);
    when(dataSource.getConnection()).thenReturn(connection);

    final DataSourceConnectionSource dataSourceConnectionSource =
        new DataSourceConnectionSource(dataSource);

    // Act
    final Connection result = dataSourceConnectionSource.get();

    // Assert
    assertThat(result, is(connection));

  }

  @Test
  void close() throws Exception {
    // Arrange
    abstract class CloseableDataSource implements DataSource, AutoCloseable {}
    final CloseableDataSource dataSource = Mockito.mock(CloseableDataSource.class);

    final DataSourceConnectionSource dataSourceConnectionSource =
        new DataSourceConnectionSource(dataSource);

    // Act
    dataSourceConnectionSource.close();

    // Assert
    verify(dataSource, times(1)).close();
  }

  @Test
  void releaseConnection() throws Exception {
    // Arrange
    final DataSource dataSource = Mockito.mock(DataSource.class);
    final Connection connection = Mockito.mock(Connection.class);
    final DataSourceConnectionSource dataSourceConnectionSource =
        new DataSourceConnectionSource(dataSource);

    // Act
    final boolean result = dataSourceConnectionSource.releaseConnection(connection);

    // Assert
    assertThat(result, is(true));
    verify(connection, times(1)).close();

  }
}
