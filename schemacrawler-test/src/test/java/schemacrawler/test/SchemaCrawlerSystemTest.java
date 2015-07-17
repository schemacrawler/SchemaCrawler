/*
 * SchemaCrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
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

    for (int i = 0; i < dataSources.length; i++)
    {
      final String dataSource = dataSources[i];

      final SchemaCrawlerOptions schemaCrawlerOptions = createOptions(dataSource,
                                                                      ".*");
      final SchemaInfoLevel infoLevel = SchemaInfoLevelBuilder.minimum();
      infoLevel.setRetrieveTables(false);
      infoLevel.setRetrieveRoutines(false);
      schemaCrawlerOptions.setSchemaInfoLevel(infoLevel);

      final Catalog catalog = retrieveDatabase(dataSource, schemaCrawlerOptions);
      final Schema[] schemas = (Schema[]) catalog.getSchemas().toArray();
      assertEquals("Incorrect number of schemas for " + dataSource + ": "
                   + Arrays.toString(schemas), schemaCounts[i], schemas.length);
    }
  }

  @Test
  public void tablesAndCounts()
    throws Exception
  {
    final List<String> messages = new ArrayList<>();
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

  }

  private void counts(final String dataSourceName,
                      final Schema schema,
                      final Catalog catalog)
    throws Exception
  {

    final int[] tableColumnCounts = {
        9, 3, 3, 6, 1, 2
    };

    final int[] fkCounts = {
        1, 0, 2, 1, 0, 0
    };

    final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
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
                   table.getColumns().size());
      assertEquals(String.format("%s table %s foreign key count does not match",
                                 dataSourceName,
                                 table.getFullName()),
                   fkCounts[tableIdx],
                   table.getForeignKeys().size());
    }
  }

  private Catalog retrieveDatabase(final String dataSourceName,
                                   final String schemaInclusion)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = createOptions(dataSourceName,
                                                                    schemaInclusion);
    final Catalog catalog = retrieveDatabase(dataSourceName,
                                             schemaCrawlerOptions);
    return catalog;
  }

  private Schema retrieveSchema(final String schemaInclusion,
                                final Catalog catalog)
    throws Exception
  {

    final Schema[] schemas = (Schema[]) catalog.getSchemas().toArray();
    final Schema schema;
    if (schemas == null || schemas.length == 0)
    {
      schema = null;
    }
    else if (schemas.length == 1)
    {
      schema = schemas[0];
    }
    else
    {
      final Pattern schemaPattern = Pattern.compile(".*books",
                                                    Pattern.CASE_INSENSITIVE);
      Schema scSchema = null;
      for (final Schema currSchema: schemas)
      {
        if (schemaPattern.matcher(currSchema.getFullName()).matches())
        {
          scSchema = currSchema;
          break;
        }
      }
      schema = scSchema;
    }
    return schema;
  }

  private Schema retrieveSchema(final String dataSourceName,
                                final String schemaInclusion)
    throws Exception
  {
    final Catalog catalog = retrieveDatabase(dataSourceName, schemaInclusion);
    return retrieveSchema(schemaInclusion, catalog);
  }

  private void tables(final String dataSourceName,
                      final Schema schema,
                      final String quote,
                      final Catalog catalog)
    throws Exception
  {
    requireNonNull(schema, "No schema found");

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

    final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
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
                   table.getTableType());
    }
  }

  private String tablesAndCounts(final String dataSourceName,
                                 final String schemaInclusion,
                                 final String quote)
  {
    try
    {
      final Catalog catalog = retrieveDatabase(dataSourceName, schemaInclusion);
      final Schema schema = retrieveSchema(schemaInclusion, catalog);
      tables(dataSourceName, schema, quote, catalog);
      counts(dataSourceName, schema, catalog);
      return null;
    }
    catch (final Exception e)
    {
      e.printStackTrace();
      return e.getMessage();
    }
  }

}
