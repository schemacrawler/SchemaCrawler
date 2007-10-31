/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.crawl;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import dbconnector.datasource.PropertiesDataSourceException;
import dbconnector.test.TestUtility;

public class SchemaCrawlerDeepTest
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerDeepTest.class.getName());

  private static TestUtility testUtility = new TestUtility();

  @AfterClass
  public static void afterAllTests()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws PropertiesDataSourceException, ClassNotFoundException
  {
    testUtility.setApplicationLogLevel();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void tableEquals()
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setShowStoredProcedures(true);

    final Schema schema = SchemaCrawler.getSchema(testUtility.getDataSource(),
                                                  SchemaInfoLevel.basic,
                                                  schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    assertTrue("Could not find any tables", schema.getTables().length > 0);
    assertTrue("Could not find any procedures",
               schema.getProcedures().length > 0);

    // Try negative test
    Table table0 = schema.getTables()[0];
    assertTrue("Could not find any columns", table0.getColumns().length > 0);

    MutableTable table1 = new MutableTable(table0.getCatalogName(), table0
      .getSchemaName(), "Test Table 1");
    MutableTable table2 = new MutableTable(table0.getCatalogName(), table0
      .getSchemaName(), "Test Table 2");
    PrimaryKey primaryKey = table0.getPrimaryKey();
    table1.setPrimaryKey(primaryKey);
    table2.setPrimaryKey(primaryKey);
    for (Column column: table0.getColumns())
    {
      table1.addColumn((MutableColumn) column);
      table2.addColumn((MutableColumn) column);
    }
    for (Index index: table0.getIndices())
    {
      table1.addIndex((MutableIndex) index);
      table2.addIndex((MutableIndex) index);
    }
    for (ForeignKey fk: table0.getForeignKeys())
    {
      table1.addForeignKey((MutableForeignKey) fk);
      table2.addForeignKey((MutableForeignKey) fk);
    }
    for (Trigger trigger: table0.getTriggers())
    {
      table1.addTrigger((MutableTrigger) trigger);
      table2.addTrigger((MutableTrigger) trigger);
    }
    for (Privilege privilege: table0.getPrivileges())
    {
      table1.addPrivilege((MutablePrivilege) privilege);
      table2.addPrivilege((MutablePrivilege) privilege);
    }
    for (CheckConstraint checkConstraint: table0.getCheckConstraints())
    {
      table1.addCheckConstraint((MutableCheckConstraint) checkConstraint);
      table2.addCheckConstraint((MutableCheckConstraint) checkConstraint);
    }

    assertFalse("Tables should not be equal", table1.equals(table2));

  }
}
