/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import us.fatehi.test.utility.DataSourceTestUtility;
import us.fatehi.test.utility.TestDatabaseDriver;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.UtilityLogger;
import us.fatehi.utility.database.DatabaseUtility;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class DatabaseUtilityTest {

  private Connection connection;
  @Captor private ArgumentCaptor<String> loggerMessageCaptor;

  @Test
  public void checkConnection() throws SQLException {

    assertThat(DatabaseUtility.checkConnection(connection), is(connection));

    final Connection mockConnection = TestObjectUtility.mockConnection();
    when(mockConnection.isClosed()).thenReturn(true);

    final SQLException exception1 =
        assertThrows(
            SQLException.class,
            () -> assertThat(DatabaseUtility.checkConnection(null), is(nullValue())));
    assertThat(exception1.getMessage(), endsWith("No database connection provided"));

    final SQLException exception2 =
        assertThrows(
            SQLException.class,
            () -> assertThat(DatabaseUtility.checkConnection(mockConnection), is(nullValue())));
    assertThat(exception2.getMessage(), endsWith("Connection is closed"));
  }

  @Test
  public void checkResultSet() throws SQLException {

    final ResultSet results = mock(ResultSet.class);

    assertThat(DatabaseUtility.checkResultSet(results), is(results));

    when(results.isClosed()).thenReturn(true);

    final SQLException exception1 =
        assertThrows(
            SQLException.class,
            () -> assertThat(DatabaseUtility.checkResultSet(null), is(nullValue())));
    assertThat(exception1.getMessage(), endsWith("No result-set provided"));

    final SQLException exception2 =
        assertThrows(
            SQLException.class,
            () -> assertThat(DatabaseUtility.checkResultSet(results), is(nullValue())));
    assertThat(exception2.getMessage(), endsWith("Result-set is closed"));
  }

  @Test
  public void getAvailableJdbcDrivers() throws SQLException {
    final Collection<Driver> drivers = DatabaseUtility.getAvailableJdbcDrivers();
    assertThat(drivers.size(), is(15));
    assertThat(
        drivers,
        everyItem(is(anyOf(instanceOf(Driver.class), instanceOf(TestDatabaseDriver.class)))));
  }

  @BeforeAll
  public void createDatabase() throws Exception {

    final DataSource db = DataSourceTestUtility.newEmbeddedDatabase("/testdb.sql");
    connection = db.getConnection();
  }

  @BeforeAll
  public void disableLogging() throws Exception {
    // Turn off logging
    new LoggingConfig();
  }

  @Test
  public void executeSql() throws SQLException {

    final Statement statement = connection.createStatement();

    assertThat(DatabaseUtility.executeSql(null, "<some query>"), is(nullValue()));
    assertThat(DatabaseUtility.executeSql(statement, null), is(nullValue()));

    assertThat(
        DatabaseUtility.executeSql(statement, "SELECT COL1 FROM TABLE1 WHERE ENTITY_ID = 1"),
        is(not(nullValue())));
    assertThat(
        DatabaseUtility.executeSql(statement, "UPDATE TABLE1 SET COL2 = 'GHI' WHERE ENTITY_ID = 1"),
        is(nullValue()));
  }

  @Test
  public void executeSql_throw() throws SQLException {

    final Statement statement = mock(Statement.class);
    when(statement.execute(anyString()))
        .thenThrow(new SQLException("Exception executing SQL statement"));

    final SQLException exception =
        assertThrows(
            SQLException.class,
            () ->
                assertThat(DatabaseUtility.executeSql(statement, "<some query>"), is(nullValue())));
    assertThat(exception.getMessage(), is("Exception executing SQL statement"));
  }

  @Test
  public void executeSqlForLong() throws SQLException {

    // Happy path
    assertThat(
        DatabaseUtility.executeSqlForLong(
            connection, "SELECT COL3 FROM TABLE1 WHERE ENTITY_ID = 1"),
        is(2L));

    // Unhappy paths
    Exception exception;
    // NULL in database
    exception =
        assertThrows(
            SQLException.class,
            () ->
                DatabaseUtility.executeSqlForLong(
                    connection, "SELECT COL3 FROM TABLE1 WHERE ENTITY_ID = 2"));
    assertThat(exception.getMessage(), startsWith("Cannot get a long value"));
    // No rows of data
    exception =
        assertThrows(
            SQLException.class,
            () ->
                DatabaseUtility.executeSqlForLong(
                    connection, "SELECT COL3 FROM TABLE1 WHERE ENTITY_ID = 3"));
    assertThat(exception.getMessage(), startsWith("Cannot get a long value"));
    // Not a number
    exception =
        assertThrows(
            SQLException.class,
            () ->
                DatabaseUtility.executeSqlForLong(
                    connection, "SELECT COL1 FROM TABLE1 WHERE ENTITY_ID = 1"));
    assertThat(exception.getMessage(), startsWith("Cannot get a long value"));
  }

  @Test
  public void executeSqlForScalar() throws SQLException {

    // Happy path
    assertThat(
        DatabaseUtility.executeSqlForScalar(
            connection, "SELECT COL3 FROM TABLE1 WHERE COL1 = 'ABC'"),
        is(new BigDecimal(2)));
    // Happy path - NULL in database
    assertThat(
        DatabaseUtility.executeSqlForScalar(
            connection, "SELECT COL3 FROM TABLE1 WHERE COL1 = 'XYZ'"),
        is(nullValue()));
    // Happy path - no rows of data
    assertThat(
        DatabaseUtility.executeSqlForScalar(
            connection, "SELECT COL3 FROM TABLE1 WHERE COL1 = 'ZZZ'"),
        is(nullValue()));

    // Unhappy paths
    Exception exception;
    // Too many rows
    exception =
        assertThrows(
            SQLException.class,
            () -> DatabaseUtility.executeSqlForScalar(connection, "SELECT COL3 FROM TABLE1"));
    assertThat(exception.getMessage(), startsWith("Too many rows"));
    // Too many columns
    exception =
        assertThrows(
            SQLException.class,
            () -> DatabaseUtility.executeSqlForScalar(connection, "SELECT COL2, COL3 FROM TABLE1"));
    assertThat(exception.getMessage(), startsWith("Too many columns"));
    // Empty result set
    final Connection mockConnection = TestObjectUtility.mockConnection();

    final Object scalar =
        DatabaseUtility.executeSqlForScalar(mockConnection, "SELECT COL3 FROM TABLE1");
    assertThat(scalar, is(nullValue()));
  }

  @Test
  public void logSQLWarningsResultSet() throws Exception {
    final Logger logger = mock(Logger.class);

    // Test with null argument
    when(logger.isLoggable(Level.INFO)).thenReturn(true);
    new UtilityLogger(logger).logSQLWarnings((ResultSet) null);
    verify(logger, never()).log(any(Level.class), anyString(), any(SQLWarning.class));

    final ResultSet results = mock(ResultSet.class);

    // Test with INFO log level off
    when(logger.isLoggable(Level.INFO)).thenReturn(false);
    new UtilityLogger(logger).logSQLWarnings(results);
    verify(logger, never()).log(any(Level.class), anyString(), any(SQLWarning.class));

    // Test error getting SQL warning
    when(results.getWarnings()).thenThrow(SQLException.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);
    new UtilityLogger(logger).logSQLWarnings(results);
    verify(logger, times(1)).log(any(Level.class), loggerMessageCaptor.capture());
    assertThat(
        loggerMessageCaptor.getValue(), startsWith("Could not log SQL warnings for result set"));

    // Test logs are created with a SQL warning
    final String errorMessage = "TEST SQL warning";
    reset(results);
    when(results.getWarnings()).thenReturn(new SQLWarning(errorMessage));
    when(logger.isLoggable(Level.INFO)).thenReturn(true);
    new UtilityLogger(logger).logSQLWarnings(results);
    verify(logger, times(1))
        .log(any(Level.class), loggerMessageCaptor.capture(), any(SQLWarning.class));
    assertThat(loggerMessageCaptor.getValue(), startsWith(errorMessage));
  }

  @Test
  public void logSQLWarningsStatement() throws Exception {
    final Logger logger = mock(Logger.class);

    // Test with null argument
    when(logger.isLoggable(Level.INFO)).thenReturn(true);
    new UtilityLogger(logger).logSQLWarnings((Statement) null);
    verify(logger, never()).log(any(Level.class), anyString(), any(SQLWarning.class));

    final Statement statement = mock(Statement.class);

    // Test with INFO log level off
    when(logger.isLoggable(Level.INFO)).thenReturn(false);
    new UtilityLogger(logger).logSQLWarnings(statement);
    verify(logger, never()).log(any(Level.class), anyString(), any(SQLWarning.class));

    // Test error getting SQL warning
    when(statement.getWarnings()).thenThrow(SQLException.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);
    new UtilityLogger(logger).logSQLWarnings(statement);
    verify(logger, times(1)).log(any(Level.class), loggerMessageCaptor.capture());
    assertThat(
        loggerMessageCaptor.getValue(), startsWith("Could not log SQL warnings for statement"));

    // Test logs are created with a SQL warning
    final String errorMessage = "TEST SQL warning";
    reset(statement);
    when(statement.getWarnings()).thenReturn(new SQLWarning(errorMessage));
    when(logger.isLoggable(Level.INFO)).thenReturn(true);
    new UtilityLogger(logger).logSQLWarnings(statement);
    verify(logger, times(1))
        .log(any(Level.class), loggerMessageCaptor.capture(), any(SQLWarning.class));
    assertThat(loggerMessageCaptor.getValue(), startsWith(errorMessage));
  }

  @Test
  public void readResultsVector() throws SQLException {
    final Statement statement = connection.createStatement();

    // Null resultset
    assertThat(DatabaseUtility.readResultsVector(null), is(emptyCollectionOf(String.class)));
    // Read no values
    assertThat(
        DatabaseUtility.readResultsVector(
            statement.executeQuery("SELECT COL1 FROM TABLE1 WHERE ENTITY_ID = 3")),
        is(emptyCollectionOf(String.class)));
    // Read one value
    assertThat(
        DatabaseUtility.readResultsVector(
            statement.executeQuery("SELECT COL1 FROM TABLE1 WHERE ENTITY_ID = 1")),
        containsInAnyOrder("ABC"));
    // Read more than value, including nulls (ignoring nulls)
    assertThat(
        DatabaseUtility.readResultsVector(statement.executeQuery("SELECT COL1 FROM TABLE1")),
        containsInAnyOrder("ABC", "XYZ"));
    // Read other data types as strings, including nulls (ignoring nulls)
    assertThat(
        DatabaseUtility.readResultsVector(statement.executeQuery("SELECT COL3 FROM TABLE1")),
        containsInAnyOrder("2"));
    // Read other column
    assertThat(
        DatabaseUtility.readResultsVector(
            statement.executeQuery("SELECT COL3, COL1, COL2 FROM TABLE1"), 2),
        containsInAnyOrder("ABC", "XYZ"));
    // Read other column with bad column number
    assertThat(
        DatabaseUtility.readResultsVector(
            statement.executeQuery("SELECT COL3, COL1, COL2 FROM TABLE1"), 0),
        is(emptyCollectionOf(String.class)));
  }
}
