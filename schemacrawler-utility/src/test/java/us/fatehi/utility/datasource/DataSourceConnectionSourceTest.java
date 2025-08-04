/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.utility.SQLRuntimeException;

class DataSourceConnectionSourceTest {

  @Test
  void close() throws Exception {
    // Arrange
    abstract class CloseableDataSource implements DataSource, AutoCloseable {}
    final CloseableDataSource dataSource = mock(CloseableDataSource.class);

    final DataSourceConnectionSource dataSourceConnectionSource =
        new DataSourceConnectionSource(dataSource);

    // Act
    dataSourceConnectionSource.close();

    // Assert
    verify(dataSource, times(1)).close();
  }

  @Test
  void closeWithoutShutdown() throws Exception {
    // Arrange
    final DataSource dataSource = mock(DataSource.class);

    final DataSourceConnectionSource dataSourceConnectionSource =
        new DataSourceConnectionSource(dataSource);

    // Act
    dataSourceConnectionSource.close();

    // Assert
    verifyNoInteractions(dataSource);
  }

  @Test
  void closeWithShutdown() throws Exception {
    // Arrange
    abstract class ShutdownableDataSource implements DataSource {
      abstract void shutdown();
    }
    final ShutdownableDataSource dataSource = mock(ShutdownableDataSource.class);

    final DataSourceConnectionSource dataSourceConnectionSource =
        new DataSourceConnectionSource(dataSource);

    // Act
    dataSourceConnectionSource.close();

    // Assert
    verify(dataSource, times(1)).shutdown();
  }

  @Test
  void get() throws Exception {
    // Arrange
    final DataSource dataSource = mock(DataSource.class);
    final Connection connection = TestObjectUtility.mockConnection();
    when(dataSource.getConnection()).thenReturn(connection);

    final DataSourceConnectionSource dataSourceConnectionSource =
        new DataSourceConnectionSource(dataSource);

    // Act
    final Connection result = dataSourceConnectionSource.get();

    // Assert
    assertThat(result, is(connection));
  }

  @Test
  void getWithException() throws Exception {
    // Arrange
    final DataSource dataSource = mock(DataSource.class);
    final Connection connection = TestObjectUtility.mockConnection();
    when(dataSource.getConnection()).thenThrow(new SQLException("Forced exception"));

    final DataSourceConnectionSource dataSourceConnectionSource =
        new DataSourceConnectionSource(dataSource);

    // Act and assert
    assertThrows(SQLRuntimeException.class, () -> dataSourceConnectionSource.get());
  }

  @Test
  void releaseConnection() throws Exception {
    // Arrange
    final DataSource dataSource = mock(DataSource.class);
    final Connection connection = TestObjectUtility.mockConnection();
    final DataSourceConnectionSource dataSourceConnectionSource =
        new DataSourceConnectionSource(dataSource);

    // Act
    final boolean result = dataSourceConnectionSource.releaseConnection(connection);

    // Assert
    assertThat(result, is(true));
    verify(connection, times(1)).close();
  }

  @Test
  void releaseConnectionWithException() throws Exception {
    // Arrange
    final DataSource dataSource = mock(DataSource.class);
    final Connection connection = TestObjectUtility.mockConnection();
    doThrow(new SQLException("Forced exception")).when(connection).close();

    final DataSourceConnectionSource dataSourceConnectionSource =
        new DataSourceConnectionSource(dataSource);

    // Act
    final boolean result = dataSourceConnectionSource.releaseConnection(connection);

    // Assert
    assertThat(result, is(false));
    verify(connection, times(1)).close();
  }
}
