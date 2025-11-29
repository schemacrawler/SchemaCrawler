/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import us.fatehi.test.utility.DataSourceTestUtility;
import us.fatehi.utility.database.SqlScript;

@TestInstance(Lifecycle.PER_CLASS)
public class SqlScriptTest {

  private Connection connection;

  @BeforeAll
  public void createDatabase() throws Exception {
    final DataSource db = DataSourceTestUtility.newEmbeddedDatabase("/testdb.sql");
    connection = db.getConnection();
  }

  @Test
  public void executeScriptEdgeCases() throws SQLException {

    final String tableName = "TABLE3";

    // Pre-condition - table does not exist
    assertThat(doesTableExist(tableName), is(false));
    // Test
    SqlScript.executeScriptFromResource("/sql-resource-table3.sql", connection);
    // Post-condition - table exists
    assertThat(doesTableExist(tableName), is(true));
  }

  @Test
  public void executeScriptFromResource() throws SQLException {

    final String tableName = "TABLE2";

    // 1. No SQL resource found
    // Pre-condition - table does not exist
    assertThat(doesTableExist(tableName), is(false));
    // Test
    assertThrows(
        RuntimeException.class,
        () -> SqlScript.executeScriptFromResource("no-resource.sql", connection));
    // Post-condition - table exists
    assertThat(doesTableExist(tableName), is(false));

    // 2. Unhappy path - bad SQL
    // Pre-condition - table does not exist
    assertThat(doesTableExist(tableName), is(false));
    // Test
    assertThrows(
        RuntimeException.class,
        () -> SqlScript.executeScriptFromResource("/bad-resource-1.sql", connection));
    // Post-condition - table exists
    assertThat(doesTableExist(tableName), is(false));

    // 3. Happy path
    // Pre-condition - table does not exist
    assertThat(doesTableExist(tableName), is(false));
    // Test
    SqlScript.executeScriptFromResource("/sql-resource-table2.sql", connection);
    // Post-condition - table exists
    assertThat(doesTableExist(tableName), is(true));
  }

  @Test
  public void executeScriptFromResourceNullCheck() throws SQLException {

    final String tableName = "TABLE2";

    // 1. Null SQL
    // Pre-condition - table does not exist
    assertThat(doesTableExist(tableName), is(false));
    // Test
    assertThrows(
        RuntimeException.class, () -> SqlScript.executeScriptFromResource(null, connection));
    // Post-condition - table exists
    assertThat(doesTableExist(tableName), is(false));

    // 2. Null connection
    // Pre-condition - table does not exist
    assertThat(doesTableExist(tableName), is(false));
    // Test
    assertThrows(
        RuntimeException.class,
        () -> SqlScript.executeScriptFromResource("/sql-resource-table2.sql", null));
    // Post-condition - table exists
    assertThat(doesTableExist(tableName), is(false));

    // 3. Empty SQL resource
    // Pre-condition - table does not exist
    assertThat(doesTableExist(tableName), is(false));
    // Test
    SqlScript.executeScriptFromResource("/empty-resource.sql", connection);
    // Post-condition - table exists
    assertThat(doesTableExist(tableName), is(false));
  }

  private boolean doesTableExist(final String tableName) throws SQLException {
    final String catalog = connection.getCatalog();
    final DatabaseMetaData dbMetaData = connection.getMetaData();
    final ResultSet results =
        dbMetaData.getTables(catalog, null, tableName, new String[] {"TABLE"});
    return results.next();
  }
}
