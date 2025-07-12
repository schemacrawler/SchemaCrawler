/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import us.fatehi.test.utility.DataSourceTestUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.PooledConnectionUtility;

@TestInstance(Lifecycle.PER_CLASS)
public class PooledConnectionTest {

  private Connection connection;
  private Map<String, Method> methodsMap;
  private final DatabaseConnectionSource databaseConnectionSource =
      mock(DatabaseConnectionSource.class);

  @BeforeEach
  public void createDatabase() throws Exception {
    final DataSource db = DataSourceTestUtility.newEmbeddedDatabase("/testdb.sql");
    connection = db.getConnection();
  }

  @BeforeAll
  public void methodsMap() throws Exception {
    methodsMap = new HashMap<>();
    for (final Method method : Connection.class.getMethods()) {
      if (method.getParameterCount() > 0) {
        continue;
      }
      methodsMap.put(method.getName(), method);
    }
  }

  @Test
  public void setSavepoint() throws SQLException {
    final Connection pooledConnection =
        PooledConnectionUtility.newPooledConnection(connection, databaseConnectionSource);

    final Method method = methodsMap.get("setSavepoint");
    assertThrows(
        InvocationTargetException.class,
        () -> method.invoke(pooledConnection),
        method.toGenericString());

    assertThrows(SQLException.class, () -> pooledConnection.setSavepoint());
  }

  @Test
  public void testClosedPooledConnection() throws SQLException {
    final Connection pooledConnection =
        PooledConnectionUtility.newPooledConnection(connection, databaseConnectionSource);
    pooledConnection.close();

    for (final Method method : methodsMap.values()) {
      if (method.getParameterCount() == 0) {
        if (Arrays.asList("close", "setSavepoint", "isClosed").contains(method.getName())) {
          continue;
        }
        assertThrows(
            InvocationTargetException.class,
            () -> method.invoke(pooledConnection),
            method.toGenericString());
      }
    }
  }

  @Test
  public void testPooledConnection() throws Exception {
    final Connection pooledConnection =
        PooledConnectionUtility.newPooledConnection(connection, databaseConnectionSource);

    for (final Method method : methodsMap.values()) {
      if (method.getParameterCount() == 0) {
        if (Arrays.asList("close", "setSavepoint").contains(method.getName())) {
          continue;
        }
        // Assert nothing is thrown from method call
        method.invoke(pooledConnection);
      }
    }
  }

  @Test
  public void toStringTest() throws Exception {
    final Connection pooledConnection =
        PooledConnectionUtility.newPooledConnection(connection, databaseConnectionSource);

    final Method method = Object.class.getMethod("toString");
    final String returnValue = (String) method.invoke(pooledConnection);
    assertThat(returnValue, startsWith("Pooled connection"));
  }

  @Test
  public void wrapper() throws Exception, InvocationTargetException {
    final Connection pooledConnection =
        PooledConnectionUtility.newPooledConnection(connection, databaseConnectionSource);

    boolean testedIsWrapperFor = false;
    boolean testedUnrwap = false;
    for (final Method method : Connection.class.getMethods()) {
      if ("isWrapperFor".equals(method.getName())) {
        testedIsWrapperFor = true;
        boolean returnValue;

        returnValue = (Boolean) method.invoke(pooledConnection, Connection.class);
        assertThat(returnValue, is(true));

        returnValue = (Boolean) method.invoke(pooledConnection, Boolean.class);
        assertThat(returnValue, is(false));
      }

      if ("unwrap".equals(method.getName())) {
        testedUnrwap = true;
        Connection returnValue;

        returnValue = (Connection) method.invoke(pooledConnection, Connection.class);
        assertThat(returnValue, is(not(nullValue())));
        assertThat(returnValue == pooledConnection, is(false));
      }
    }
    assertThat(testedIsWrapperFor, is(true));
    assertThat(testedUnrwap, is(true));
  }
}
