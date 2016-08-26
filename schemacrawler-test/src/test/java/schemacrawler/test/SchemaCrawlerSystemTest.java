/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test;


import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import sf.util.ObjectToString;

@Ignore
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
