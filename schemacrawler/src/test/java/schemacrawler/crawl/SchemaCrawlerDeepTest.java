/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

package schemacrawler.crawl;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import schemacrawler.utility.test.TestUtility;

public class SchemaCrawlerDeepTest
{

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
    TestUtility.setApplicationLogLevel();
    testUtility.createMemoryDatabase();
  }

  @Test
  public void tableEquals()
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setShowStoredProcedures(true);

    final Schema schema = testUtility.getSchema(schemaCrawlerOptions);
    assertNotNull("Could not obtain schema", schema);
    assertTrue("Could not find any tables", schema.getTables().length > 0);
    assertTrue("Could not find any procedures",
               schema.getProcedures().length > 0);

    // Try negative test
    final Table table0 = schema.getTables()[0];
    assertTrue("Could not find any columns", table0.getColumns().length > 0);

    final MutableTable table1 = new MutableTable(table0.getCatalogName(),
                                                 table0.getSchemaName(),
                                                 "Test Table 1");
    final MutableTable table2 = new MutableTable(table0.getCatalogName(),
                                                 table0.getSchemaName(),
                                                 "Test Table 2");
    final PrimaryKey primaryKey = table0.getPrimaryKey();
    table1.setPrimaryKey(primaryKey);
    table2.setPrimaryKey(primaryKey);
    for (final Column column: table0.getColumns())
    {
      table1.addColumn((MutableColumn) column);
      table2.addColumn((MutableColumn) column);
    }
    for (final Index index: table0.getIndices())
    {
      table1.addIndex((MutableIndex) index);
      table2.addIndex((MutableIndex) index);
    }
    for (final ForeignKey fk: table0.getForeignKeys())
    {
      table1.addForeignKey((MutableForeignKey) fk);
      table2.addForeignKey((MutableForeignKey) fk);
    }
    for (final Trigger trigger: table0.getTriggers())
    {
      table1.addTrigger((MutableTrigger) trigger);
      table2.addTrigger((MutableTrigger) trigger);
    }
    for (final Privilege privilege: table0.getPrivileges())
    {
      table1.addPrivilege((MutablePrivilege) privilege);
      table2.addPrivilege((MutablePrivilege) privilege);
    }
    for (final CheckConstraint checkConstraint: table0.getCheckConstraints())
    {
      table1.addCheckConstraint((MutableCheckConstraint) checkConstraint);
      table2.addCheckConstraint((MutableCheckConstraint) checkConstraint);
    }

    assertFalse("Tables should not be equal", table1.equals(table2));

  }
}
