/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestSuite;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.test.util.TestBase;

public class SchemaCrawlerTest
  extends TestBase
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerTest.class
      .getName());

  public static Test suite()
  {
    return new TestSuite(SchemaCrawlerTest.class);
  }

  public SchemaCrawlerTest(final String name)
  {
    super(name);
  }

  public void testTableCount()
  {
    LOGGER.log(Level.FINE, dataSource.toString());
    LOGGER.log(Level.FINE, "schemapattern="
        + dataSource.getSourceProperties().getProperty("schemapattern"));
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    LOGGER.log(Level.FINE, schemaCrawlerOptions.toString());
    final Schema schema = SchemaCrawler.getSchema(dataSource, null,
        SchemaInfoLevel.MINIMUM, schemaCrawlerOptions);
    final Table[] tables = schema.getTables();
    final int numTables = tables.length;
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Table count does not match", 5, numTables);

  }

  public void testTableNames()
  {

    final String[] tableNames =
    { "CUSTOMER", "CUSTOMERLIST", "INVOICE", "ITEM", "PRODUCT" };
    final String[] tableTypes =
    { "TABLE", "VIEW", "TABLE", "TABLE", "TABLE" };
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    final Schema schema = SchemaCrawler.getSchema(dataSource, null,
        SchemaInfoLevel.MINIMUM, schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 5, tables.length);
    for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
    {
      final Table table = tables[tableIdx];
      assertEquals("Table name does not match", tableNames[tableIdx], table
          .getName());
      assertEquals("Table type does not match", tableTypes[tableIdx], table
          .getType().toString());
    }

  }

  public void testColumns()
  {

    final String[][] columnNames =
    {
     { "CUSTOMER.ID", "CUSTOMER.FIRSTNAME", "CUSTOMER.LASTNAME",
      "CUSTOMER.STREET", "CUSTOMER.CITY" },
     { "CUSTOMERLIST.ID", "CUSTOMERLIST.FIRSTNAME", "CUSTOMERLIST.LASTNAME", },
     { "INVOICE.ID", "INVOICE.CUSTOMERID", "INVOICE.TOTAL" },
     { "ITEM.INVOICEID", "ITEM.ITEM", "ITEM.PRODUCTID", "ITEM.QUANTITY",
      "ITEM.COST" },
     { "PRODUCT.ID", "PRODUCT.NAME", "PRODUCT.PRICE" } };

    final String[][] columnDataTypes =
    {
    { "INTEGER", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR" },
    { "INTEGER", "VARCHAR", "VARCHAR" },
    { "INTEGER", "INTEGER", "DECIMAL" },
    { "INTEGER", "INTEGER", "INTEGER", "INTEGER", "DECIMAL" },
    { "INTEGER", "VARCHAR", "DECIMAL" } };

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    final Schema schema = SchemaCrawler.getSchema(dataSource, null,
        SchemaInfoLevel.BASIC, schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", 5, tables.length);
    for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
    {
      final Table table = tables[tableIdx];
      final Column[] columns = table.getColumns();
      final String[] columnsNamesForTable = columnNames[tableIdx];
      for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
      {
        final Column column = columns[columnIdx];
        LOGGER.log(Level.FINE, column.toString());
        assertEquals("Column name does not match",
            columnsNamesForTable[columnIdx], column.toString());
        assertEquals("Column type does not match",
            columnDataTypes[tableIdx][columnIdx], column.getType()
                .getDatabaseSpecificTypeName());
        assertEquals("Column JDBC type does not match",
            columnDataTypes[tableIdx][columnIdx], column.getType()
                .getTypeName());
      }
    }

  }

}
