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

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.integration.test.utility.OracleTestUtility.newOracleContainer;
import static schemacrawler.schemacrawler.QueryUtility.executeForScalar;
import static schemacrawler.test.utility.TestUtility.javaVersion;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.test.utility.HeavyDatabaseTest;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

@HeavyDatabaseTest("oracle")
@Testcontainers
public class OracleSpecialUsersTest extends BaseOracleWithConnectionTest {

  @Container private final JdbcDatabaseContainer<?> dbContainer = newOracleContainer();

  private DataSource schemaOwnerUserDataSource;
  private DataSource selectUserDataSource;
  private DataSource catalogUserDataSource;
  private DataSource noAccessUserDataSource;

  @BeforeEach
  public void createDatabase() {

    final String urlx = "restrictGetTables=true;useFetchSizeWithLongColumn=true";
    createDataSource(dbContainer.getJdbcUrl(), "SYS AS SYSDBA", dbContainer.getPassword(), urlx);

    createDatabase("/oracle.scripts.txt");

    schemaOwnerUserDataSource =
        createDataSourceObject(dbContainer.getJdbcUrl(), "BOOKS", "BOOKS", urlx);
    selectUserDataSource =
        createDataSourceObject(dbContainer.getJdbcUrl(), "SELUSER", "SELUSER", urlx);
    catalogUserDataSource =
        createDataSourceObject(dbContainer.getJdbcUrl(), "CATUSER", "CATUSER", urlx);
    noAccessUserDataSource =
        createDataSourceObject(dbContainer.getJdbcUrl(), "NOTUSER", "NOTUSER", urlx);
  }

  @Test
  @DisplayName("Oracle test for user CATUSER with just SELECT_CATALOG_ROLE")
  /** CATUSER can get metadata, but cannot run data queries. */
  public void testOracleSelectCatalogRoleUser() throws Exception {

    final Connection connection = catalogUserDataSource.getConnection();
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSources.fromDataSource(catalogUserDataSource);
    final String expectedResource =
        String.format("testOracleSelectCatalogRoleUser.%s.txt", javaVersion());
    testOracleWithConnection(dataSource, expectedResource, 33, true);

    final DatabaseAccessException sqlException =
        assertThrows(
            DatabaseAccessException.class,
            () -> testSelectQuery(dataSource, "testOracleWithConnectionQuery.txt"));
    assertThat(
        sqlException.getMessage(),
        matchesPattern(
            Pattern.compile(".*ORA-00942: table or view .* does not exist.*", Pattern.DOTALL)));

    assertCatalogScope(connection, true, true);
  }

  @Test
  @DisplayName("Oracle test for system user")
  /** CATUSER can get metadata, but cannot run data queries. */
  public void testOracleSystemUser() throws Exception {

    final Connection connection = getConnection();

    assertCatalogScope(connection, true, true);
  }

  @Test
  @DisplayName("Oracle test for user NOTUSER with no access")
  /** NOTUSER cannot get metadata, nor run data queries. */
  public void testOracleWithNoAccessUser() throws Exception {

    final Connection connection = noAccessUserDataSource.getConnection();
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSources.fromDataSource(noAccessUserDataSource);
    final String expectedResource =
        String.format("testOracleWithNoAccessUser.%s.txt", javaVersion());
    testOracleWithConnection(dataSource, expectedResource, 33, false);

    final DatabaseAccessException sqlException =
        assertThrows(
            DatabaseAccessException.class,
            () -> testSelectQuery(dataSource, "testOracleWithConnectionQuery.txt"));
    assertThat(
        sqlException.getMessage(),
        matchesPattern(
            Pattern.compile(".*ORA-00942: table or view .* does not exist.*", Pattern.DOTALL)));

    assertCatalogScope(connection, false, true);
  }

  @Test
  @DisplayName("Oracle test for user BOOKS who is the schema owner")
  /** BOOKS user can get metadata, and can run data queries. */
  public void testOracleWithSchemaOwnerUser() throws Exception {

    final Connection connection = schemaOwnerUserDataSource.getConnection();
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSources.fromDataSource(schemaOwnerUserDataSource);
    final String expectedResource =
        String.format("testOracleWithSchemaOwnerUser.%s.txt", javaVersion());
    testOracleWithConnection(dataSource, expectedResource, 33, true);

    testSelectQuery(dataSource, "testOracleWithConnectionQuery.txt");

    assertCatalogScope(connection, false, true);
  }

  @Test
  @DisplayName("Oracle test for user SELUSER with just GRANT SELECT")
  /** SELUSER cannot get metadata, but can run data queries. */
  public void testOracleWithSelectGrantUser() throws Exception {

    final Connection connection = selectUserDataSource.getConnection();
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSources.fromDataSource(selectUserDataSource);

    final String expectedResource =
        String.format("testOracleWithSelectGrantUser.%s.txt", javaVersion());
    testOracleWithConnection(dataSource, expectedResource, 33, true);

    testSelectQuery(dataSource, "testOracleWithConnectionQuery.txt");

    assertCatalogScope(connection, false, true);
  }

  private void assertCatalogScope(
      final Connection connection, final boolean dbaAccess, final boolean allAccess) {

    assertDataDictionaryAccess(
        new Query(
            "Select from DBA data dictionary tables",
            "SELECT TABLE_NAME FROM DBA_TABLES WHERE ROWNUM = 1"),
        connection,
        dbaAccess);

    assertDataDictionaryAccess(
        new Query(
            "Select from ALL data dictionary tables",
            "SELECT TABLE_NAME FROM ALL_TABLES WHERE ROWNUM = 1"),
        connection,
        allAccess);
  }

  private void assertDataDictionaryAccess(
      final Query query, final Connection connection, final boolean accessAllowed) {
    try {
      final Object scalar = executeForScalar(query, connection);
      if (scalar == null) {
        fail(query.getName() + " is allowed, but not expected to be allowed");
      }
      if (!accessAllowed) {
        fail(query.getName() + " is allowed, but not expected to be allowed");
      }
    } catch (final SQLException e) {
      if (accessAllowed) {
        fail(query.getName() + "  is not allowed, but is expected to be allowed");
      }
    }
  }
}
