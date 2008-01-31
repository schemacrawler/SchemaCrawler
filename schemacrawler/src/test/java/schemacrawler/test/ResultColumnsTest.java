/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;
import dbconnector.test.TestUtility;

public class ResultColumnsTest
{

  private static final Logger LOGGER = Logger.getLogger(ResultColumnsTest.class
    .getName());

  private static TestUtility testUtility = new TestUtility();

  @AfterClass
  public static void afterAllTests()
    throws ClassNotFoundException
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws ClassNotFoundException
  {
    testUtility.setApplicationLogLevel();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void columns()
    throws SQLException
  {

    final String[] columnNames = {
        "CUSTOMER.FIRSTNAME", "CUSTOMER.LASTNAME", "ADDRESS", "",
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
    Connection connection = testUtility.getDataSource().getConnection();
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);

    final ResultsColumns resultColumns = SchemaCrawler
      .getResultColumns(resultSet);
    connection.close();

    assertNotNull("Could not obtain result columns", resultColumns);
    ResultsColumn[] columns = resultColumns.getColumns();
    assertEquals("Column count does not match", 4, columns.length);
    for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
    {
      ResultsColumn column = columns[columnIdx];
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

  }
}
