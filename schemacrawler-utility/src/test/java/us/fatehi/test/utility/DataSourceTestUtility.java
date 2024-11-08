/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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
package us.fatehi.test.utility;

import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hsqldb.jdbc.JDBCDataSource;

public final class DataSourceTestUtility {

  private DataSourceTestUtility() {
    // Prevent instantiation
  }

  public static DataSource newEmbeddedDatabase(final String script) {
    try {
      // Create data source
      final String randomDatabaseName = RandomStringUtils.randomAlphabetic(7);
      final JDBCDataSource hsqlDataSource = new JDBCDataSource();
      hsqlDataSource.setDatabase("jdbc:hsqldb:mem:" + randomDatabaseName);
      // Read script
      final String sql = IOUtils.resourceToString(script, StandardCharsets.UTF_8);
      String[] statements = sql.split(";");
      // Create a QueryRunner to execute the SQL statements
      final QueryRunner runner = new QueryRunner(hsqlDataSource);
      for (String statement : statements) {
        statement = statement.trim();
        if (!statement.isEmpty()) {
          runner.update(statement);
        }
      }
      return hsqlDataSource;
    } catch (IOException | SQLException e) {
      return fail("Could not create a data source", e);
    }
  }
}
