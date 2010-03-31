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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.TestDatabase;

public class SchemaCrawlerGrepTest
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerGrepTest.class.getName());

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
  public void grep()
    throws Exception
  {
    final String[] schemaNames = {
        "INFORMATION_SCHEMA", "PUBLIC", "SALES"
    };
    final int[] tableCounts = {
        0, 1, 1
    };
    final String[][][] columnNames = {
        {},
        {
          {
              "BOOKAUTHORS.BOOKID", "BOOKAUTHORS.AUTHORID",
          },
        },
        {
          {
              "SALES.POSTALCODE",
              "SALES.COUNTRY",
              "SALES.BOOKID",
              "SALES.PERIODENDDATE",
              "SALES.TOTALAMOUNT",
          },
        }
    };
    final String[][][] columnDataTypes = {
        {}, {
          {
              "INTEGER", "INTEGER",
          },
        }, {
          {
              "VARCHAR", "VARCHAR", "INTEGER", "DATE", "FLOAT",
          },
        }
    };

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();

    schemaCrawlerOptions
      .setGrepColumnInclusionRule(new InclusionRule(".*\\..*\\.BOOKID", ""));

    final Database database = testUtility.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match", 3, schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      assertEquals("Schema name does not match", schemaNames[schemaIdx], schema
        .getName());
      final Table[] tables = schema.getTables();
      assertEquals("Table count does not match",
                   tableCounts[schemaIdx],
                   tables.length);
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        final Column[] columns = table.getColumns();
        final String[] columnsNamesForTable = columnNames[schemaIdx][tableIdx];
        for (int columnIdx = 0; columnIdx < columns.length; columnIdx++)
        {
          final Column column = columns[columnIdx];
          LOGGER.log(Level.FINE, column.toString());
          assertEquals("Column full name does not match",
                       schemaNames[schemaIdx] + "."
                           + columnsNamesForTable[columnIdx],
                       column.getFullName());
          assertEquals("Column type does not match",
                       columnDataTypes[schemaIdx][tableIdx][columnIdx],
                       column.getType().getDatabaseSpecificTypeName());
          assertEquals("Column JDBC type does not match",
                       columnDataTypes[schemaIdx][tableIdx][columnIdx],
                       column.getType().getTypeName());
        }
      }
    }
  }
}
