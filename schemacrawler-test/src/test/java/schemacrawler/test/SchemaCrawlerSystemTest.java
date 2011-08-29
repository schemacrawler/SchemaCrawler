/*
 * SchemaCrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import sf.util.ObjectToString;

public class SchemaCrawlerSystemTest
  extends AbstractSchemaCrawlerSystemTest
{

  @Test
  public void schemaCounts()
    throws Exception
  {
    final int[] schemaCounts = {
        65, 19, 16, 6, 5, 12, 1,
    };

    final SchemaCrawlerOptions schemaCrawlerOptions = createOptions(".*");
    final SchemaInfoLevel infoLevel = SchemaInfoLevel.minimum();
    infoLevel.setRetrieveTables(false);
    infoLevel.setRetrieveProcedures(false);
    schemaCrawlerOptions.setSchemaInfoLevel(infoLevel);

    for (int i = 0; i < dataSources.length; i++)
    {
      final String dataSource = dataSources[i];
      final Database database = retrieveDatabase(dataSource,
                                                 schemaCrawlerOptions);
      final Schema[] schemas = database.getSchemas();
      assertEquals("Incorrect number of schemas for " + dataSource + ": "
                   + Arrays.toString(schemas), schemaCounts[i], schemas.length);
    }
  }

  @Test
  public void tablesAndCounts()
    throws Exception
  {
    final List<String> messages = new ArrayList<String>();
    String message;
    message = tablesAndCounts("MicrosoftSQLServer", "Books.dbo", "\"");
    if (message != null)
    {
      messages.add(message);
    }

    message = tablesAndCounts("Oracle", "BOOKS", "\"");
    if (message != null)
    {
      messages.add(message);
    }

    message = tablesAndCounts("IBM_DB2", "BOOKS", "\"");
    if (message != null)
    {
      messages.add(message);
    }

    message = tablesAndCounts("MySQL", null, "`");
    if (message != null)
    {
      messages.add(message);
    }

    message = tablesAndCounts("PostgreSQL", "books", "\"");
    if (message != null)
    {
      messages.add(message);
    }

    message = tablesAndCounts("Derby", "BOOKS", "\"");
    if (message != null)
    {
      messages.add(message);
    }

    // message = tablesAndCounts("SQLite", null, "\"");
    // if (message != null)
    // {
    // messages.add(message);
    // }

    if (!messages.isEmpty())
    {
      final String error = ObjectToString.toString(messages);
      System.out.println(error);
      fail(error);
    }
  }

  @Test
  public void unknownSchema()
    throws Exception
  {
    String dataSourceName;
    Schema schema;

    dataSourceName = "MicrosoftSQLServer";
    schema = retrieveSchema(dataSourceName, "unknown");
    assertNull(dataSourceName, schema);

    dataSourceName = "MySQL";
    schema = retrieveSchema(dataSourceName, "unknown");
    assertNull(dataSourceName, schema);

    dataSourceName = "Oracle";
    schema = retrieveSchema(dataSourceName, "unknown");
    assertNull(dataSourceName, schema);

    dataSourceName = "PostgreSQL";
    schema = retrieveSchema(dataSourceName, "unknown");
    assertNull(dataSourceName, schema);

    // // SQLite does not support catalogs or schemas, so rules are
    // ignored
    // dataSourceName = "SQLite";
    // schema = retrieveSchema(dataSourceName, "unknown");
    // assertNotNull(dataSourceName, schema);
  }

  private void counts(final String dataSourceName, final Schema schema)
    throws Exception
  {

    final int[] tableColumnCounts = {
        9, 3, 3, 6, 1, 2
    };
    final int[] checkConstraints = {
        0, 0, 0, 0, 0, 0
    };
    /*
     * final int[] indexCounts = { 1, 0, 2, 4, 0, 2 };
     */
    final int[] fkCounts = {
        1, 0, 2, 1, 0, 0
    };

    final Table[] tables = schema.getTables();
    assertEquals(dataSourceName + " table count does not match",
                 tableColumnCounts.length,
                 tables.length);
    for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
    {
      final Table table = tables[tableIdx];
      assertEquals(String.format("%s table %s columns count does not match",
                                 dataSourceName,
                                 table.getFullName()),
                   tableColumnCounts[tableIdx],
                   table.getColumns().length);
      assertEquals(String.format("%s table %s check constraints count does not match",
                                 dataSourceName,
                                 table.getFullName()),
                   checkConstraints[tableIdx],
                   table.getCheckConstraints().length);
      /*
       * assertEquals(String.format("%s table %s index count does not match"
       * , dataSourceName, table.getFullName()), indexCounts[tableIdx],
       * table.getIndices().length);
       */
      assertEquals(String.format("%s table %s foreign key count does not match",
                                 dataSourceName,
                                 table.getFullName()),
                   fkCounts[tableIdx],
                   table.getForeignKeys().length);
    }
  }

  private void tables(final String dataSourceName,
                      final Schema schema,
                      final String quote)
    throws Exception
  {
    if (schema == null)
    {
      throw new SchemaCrawlerException("No schema found");
    }

    final String[] tableNames = {
        "AUTHORS",
        "AUTHORSLIST",
        "BOOKAUTHORS",
        "BOOKS",
        quote + "GLOBAL COUNTS" + quote,
        "PUBLISHERS",
    };
    final String[] tableTypes = {
        "TABLE", "VIEW", "TABLE", "TABLE", "TABLE", "TABLE"
    };

    final Table[] tables = schema.getTables();
    assertEquals(dataSourceName + " table count does not match - "
                     + ObjectToString.toString(tables),
                 tableNames.length,
                 tables.length);
    for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
    {
      final Table table = tables[tableIdx];
      assertEquals(dataSourceName + " table name does not match",
                   tableNames[tableIdx],
                   table.getName().toUpperCase());
      assertEquals(dataSourceName + " table type does not match",
                   tableTypes[tableIdx],
                   table.getType().toString().toUpperCase());
    }
  }

  private String tablesAndCounts(final String dataSourceName,
                                 final String schemaInclusion,
                                 final String quote)
  {
    try
    {
      Schema schema;
      schema = retrieveSchema(dataSourceName, schemaInclusion);
      tables(dataSourceName, schema, quote);
      counts(dataSourceName, schema);
      return null;
    }
    catch (final Exception e)
    {
      e.printStackTrace();
      return e.getMessage();
    }
  }

}
