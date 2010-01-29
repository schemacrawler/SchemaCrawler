/*
 * SchemaCrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.utility.TestDatabase;

public class ResultColumnsTest
{

  private static final Logger LOGGER = Logger.getLogger(ResultColumnsTest.class
    .getName());

  private static TestDatabase testUtility = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void columns()
    throws Exception
  {

    final String[] columnNames = {
        "PUBLIC.CUSTOMER.FIRSTNAME", "PUBLIC.CUSTOMER.LASTNAME", "ADDRESS", "",
    };
    final String[] columnDataTypes = {
        "VARCHAR", "VARCHAR", "VARCHAR", "DOUBLE",
    };

    final String sql = "SELECT "
                       + "  CUSTOMER.FIRSTNAME, "
                       + "  CUSTOMER.LASTNAME, "
                       + "  CUSTOMER.STREET + ', ' + CUSTOMER.CITY AS ADDRESS, "
                       + "  SUM(INVOICE.TOTAL) " + "FROM " + "  CUSTOMER "
                       + "  INNER JOIN INVOICE "
                       + "  ON INVOICE.CUSTOMERID = CUSTOMER.ID " + "GROUP BY "
                       + "  CUSTOMER.FIRSTNAME, " + "  CUSTOMER.LASTNAME, "
                       + "  CUSTOMER.STREET, " + "  CUSTOMER.CITY "
                       + "ORDER BY " + "  SUM(INVOICE.TOTAL) DESC";
    final Connection connection = testUtility.getConnection();
    final Statement statement = connection.createStatement();
    final ResultSet resultSet = statement.executeQuery(sql);

    final ResultsColumns resultColumns = SchemaCrawler
      .getResultColumns(resultSet);
    connection.close();

    assertNotNull("Could not obtain result columns", resultColumns);
    final ResultsColumn[] columns = resultColumns.getColumns();
    assertEquals("Column count does not match", 4, columns.length);
    for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
    {
      final ResultsColumn column = columns[columnIdx];
      LOGGER.log(Level.FINE, column.toString());
      assertEquals("Column full name does not match",
                   columnNames[columnIdx],
                   column.getFullName());
      assertEquals("Column type does not match",
                   columnDataTypes[columnIdx],
                   column.getType().getDatabaseSpecificTypeName());
      assertEquals("Column JDBC type does not match",
                   columnDataTypes[columnIdx],
                   column.getType().getTypeName());
    }

    resultSet.close();
    statement.close();

  }
}
