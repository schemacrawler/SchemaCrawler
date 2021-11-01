/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static schemacrawler.test.utility.TestUtility.javaVersion;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import schemacrawler.schemacrawler.SchemaCrawlerException;

@Testcontainers(disabledWithoutDocker = true)
@EnabledIfSystemProperty(named = "heavydb", matches = "^((?!(false|no)).)*$")
public class OracleSpecialUsersTest extends BaseOracleWithConnectionTest {

  @Container
  private final JdbcDatabaseContainer<?> dbContainer =
      new OracleContainer(DockerImageName.parse("gvenzl/oracle-xe").withTag("11")).usingSid();

  private DataSource booksUserDataSource;
  private DataSource catalogUserDataSource;

  @BeforeEach
  public void createDatabase() throws SQLException, SchemaCrawlerException {

    final String urlx = "restrictGetTables=true;useFetchSizeWithLongColumn=true";
    createDataSource(dbContainer.getJdbcUrl(), "SYS AS SYSDBA", dbContainer.getPassword(), urlx);

    createDatabase("/oracle-11g.scripts.txt");

    booksUserDataSource =
        createDataSourceObject(dbContainer.getJdbcUrl(), "BOOKSUSER", "booksuser", urlx);
    catalogUserDataSource =
        createDataSourceObject(dbContainer.getJdbcUrl(), "CATUSER", "catuser", urlx);
  }

  @Test
  @DisplayName("Oracle test for user with just Schema Object Access role")
  /**
   * Test user cannot get metadata, but can run data queries. The BOOKSUSER does not have access
   * either to DBA_ nor ALL_ data dictionary tables.
   */
  public void testOracleWithConnectionSchemaObjectAccessUser() throws Exception {
    final Connection connection = booksUserDataSource.getConnection();
    final String expectedResource =
        String.format("testOracleWithConnectionSchemaObjectAccessUser.%s.txt", javaVersion());
    testOracleWithConnection(connection, expectedResource, 13);
  }

  @Test
  @DisplayName("Oracle test for user with just Select Catalog role")
  /**
   * Test user can get metadata, but cannot run data queries. The CATUSER does not have access
   * either to DBA_ data dictionary tables, but only to the ALL_ dictionary tables.
   */
  public void testOracleWithConnectionSelectCatalogUser() throws Exception {
    final Connection connection = catalogUserDataSource.getConnection();
    final String expectedResource =
        String.format("testOracleWithConnectionSelectCatalogUser.%s.txt", javaVersion());
    testOracleWithConnection(connection, expectedResource, 13);
  }
}
