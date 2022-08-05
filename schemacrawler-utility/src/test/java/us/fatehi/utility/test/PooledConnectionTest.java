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
package us.fatehi.utility.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.PooledConnectionUtility;

public class PooledConnectionTest {

  private Connection connection;

  @BeforeEach
  public void createDatabase() throws Exception {

    final EmbeddedDatabase db =
        new EmbeddedDatabaseBuilder()
            .generateUniqueName(true)
            .setScriptEncoding("UTF-8")
            .ignoreFailedDrops(true)
            .addScript("testdb.sql")
            .build();

    connection = db.getConnection();
  }

  @Test
  public void testClosedPooledConnection() throws SQLException {
    final DatabaseConnectionSource databaseConnectionSource = mock(DatabaseConnectionSource.class);
    final Connection pooledConnection =
        PooledConnectionUtility.newPooledConnection(connection, databaseConnectionSource);
    pooledConnection.close();

    final Method[] declaredMethods = Connection.class.getDeclaredMethods();
    for (final Method method : declaredMethods) {
      if (method.getParameterCount() == 0) {
        if (Arrays.asList("close", "setSavepoint").contains(method.getName())) {
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
    final DatabaseConnectionSource databaseConnectionSource = mock(DatabaseConnectionSource.class);
    final Connection pooledConnection =
        PooledConnectionUtility.newPooledConnection(connection, databaseConnectionSource);

    final Method[] declaredMethods = Connection.class.getDeclaredMethods();
    for (final Method method : declaredMethods) {
      if (method.getParameterCount() == 0) {
        if (Arrays.asList("close", "setSavepoint").contains(method.getName())) {
          continue;
        }
        // Assert nothing is thrown from method call
        method.invoke(pooledConnection);
      }
    }
  }
}
