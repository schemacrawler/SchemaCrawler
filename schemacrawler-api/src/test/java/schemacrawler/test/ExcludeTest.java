/*
 * SchemaCrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
import schemacrawler.test.utility.TestDatabase;

public class ExcludeTest
{

  private static final Logger LOGGER = Logger.getLogger(ExcludeTest.class
    .getName());

  private static TestDatabase testDatabase = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testDatabase.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testDatabase.startMemoryDatabase();
  }

  @Test
  public void excludeColumns()
    throws Exception
  {
    final String[] schemaNames = {
        "BOOKS",
        "INFORMATION_SCHEMA",
        "PUBLIC",
        "\"PUBLISHER SALES\"",
        "SYSTEM_LOBS"
    };
    final int[] tableCounts = {
        6, 0, 0, 2, 0,
    };
    final String[][][] columnNames = {
        {
            {
                // "AUTHORS.ID",
                "AUTHORS.FIRSTNAME",
                "AUTHORS.LASTNAME",
                "AUTHORS.ADDRESS1",
                "AUTHORS.ADDRESS2",
                "AUTHORS.CITY",
                "AUTHORS.STATE",
                "AUTHORS.POSTALCODE",
                "AUTHORS.COUNTRY",
            },
            {
                // "AUTHORSLIST.ID",
                "AUTHORSLIST.FIRSTNAME",
                "AUTHORSLIST.LASTNAME",
            },
            {
                "BOOKAUTHORS.BOOKID",
                "BOOKAUTHORS.AUTHORID",
                "BOOKAUTHORS.\"UPDATE\"",
            },
            {
                // "BOOKS.ID",
                "BOOKS.TITLE",
                "BOOKS.DESCRIPTION",
                "BOOKS.PUBLISHERID",
                "BOOKS.PUBLICATIONDATE",
                "BOOKS.PRICE",
            },
            {
              "\"Global Counts\".\"Global Count\"",
            },
            {
              // "PUBLISHERS.ID",
              "PUBLISHERS.PUBLISHER",
            },
        },
        {},
        {},
        {
            {
                "REGIONS.CITY",
                "REGIONS.STATE",
                "REGIONS.POSTALCODE",
                "REGIONS.COUNTRY",
            },
            {
                "SALES.POSTALCODE",
                "SALES.COUNTRY",
                "SALES.BOOKID",
                "SALES.PERIODENDDATE",
                "SALES.TOTALAMOUNT",
            },
        },
        {},
    };
    final String[][][] columnDataTypes = {
        {

            {
                // "INTEGER",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
                "VARCHAR",
            }, {
                // "INTEGER",
                "VARCHAR",
                "VARCHAR",
            }, {
                "INTEGER", "INTEGER", "CLOB",
            }, {
                // "INTEGER",
                "VARCHAR",
                "VARCHAR",
                "INTEGER",
                "DATE",
                "DOUBLE"
            }, {
              "INTEGER",
            }, {
              // "INTEGER",
              "VARCHAR",
            },
        }, {}, {}, {
            {
                "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR"
            }, {
                "VARCHAR", "VARCHAR", "INTEGER", "DATE", "DOUBLE",
            },
        }, {},
    };

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setSchemaInclusionRule(new InclusionRule(InclusionRule.ALL,
                                                ".*\\.FOR_LINT"));
    schemaCrawlerOptions
      .setColumnInclusionRule(new InclusionRule(InclusionRule.ALL,
                                                ".*\\..*\\.ID"));

    final Database database = testDatabase.getDatabase(schemaCrawlerOptions);
    final Schema[] schemas = database.getSchemas();
    assertEquals("Schema count does not match",
                 schemaNames.length,
                 schemas.length);
    for (int schemaIdx = 0; schemaIdx < schemas.length; schemaIdx++)
    {
      final Schema schema = schemas[schemaIdx];
      assertEquals("Schema name does not match",
                   "PUBLIC." + schemaNames[schemaIdx],
                   schema.getName());
      final Table[] tables = schema.getTables();
      assertEquals("Table count does not match, for schema " + schema,
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
          assertEquals("Column full name does not match for column " + column,
                       "PUBLIC." + schemaNames[schemaIdx] + "."
                           + columnsNamesForTable[columnIdx],
                       column.getFullName());
          assertEquals("Column type does not match for column " + column,
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
