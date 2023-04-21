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

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.integration.test.utility.MySQLTestUtility.newMySQL8Container;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseAdditionalDatabaseTest;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.HeavyDatabaseTest;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;

@HeavyDatabaseTest
@Testcontainers
@DisplayName("Test for support of enum values")
public class MySQLEnumColumnTest extends BaseAdditionalDatabaseTest {

  @Container
  private final JdbcDatabaseContainer<?> dbContainer =
      newMySQL8Container().withUsername("schemacrawler");

  @Test
  public void columnWithEnum() throws Exception {
    try (final Connection connection = getConnection();
        final Statement stmt = connection.createStatement(); ) {
      stmt.execute("CREATE TABLE shirts (name VARCHAR(40), size ENUM('small', 'medium', 'large'))");
      // Auto-commited
    }

    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);

    assertThat(
        outputOf(executableExecution(getDataSource(), executable)),
        hasSameContentAs(classpathResource("testColumnWithEnum.txt")));

    // Additional programmatic test
    final Catalog catalog = executable.getCatalog();
    final Schema schema = catalog.lookupSchema("test").orElse(null);
    assertThat(schema, notNullValue());
    final Table table = catalog.lookupTable(schema, "shirts").orElse(null);
    assertThat(table, notNullValue());

    final Column nameColumn = table.lookupColumn("name").orElse(null);
    assertThat(nameColumn, notNullValue());
    final List<String> nameEnumValues = nameColumn.getColumnDataType().getEnumValues();
    assertThat(nameEnumValues, is(empty()));

    final Column sizeColumn = table.lookupColumn("size").orElse(null);
    assertThat(sizeColumn, notNullValue());
    final List<String> enumValues = sizeColumn.getColumnDataType().getEnumValues();
    assertThat(enumValues, containsInAnyOrder("small", "medium", "large"));
  }

  @BeforeEach
  public void createDatabase() {

    if (!dbContainer.isRunning()) {
      fail("Testcontainer for database is not available");
    }

    createDataSource(
        dbContainer.getJdbcUrl(), dbContainer.getUsername(), dbContainer.getPassword());
  }
}
