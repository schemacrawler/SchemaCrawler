/*
 * SchemaCrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
import static org.junit.Assert.assertNull;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

public class SchemaCrawlerSystemTest
{

  final ApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml");

  @Test
  public void tablesAndCounts()
    throws Exception
  {
    String dataSourceName;
    Schema schema;

    dataSourceName = "MicrosoftSQLServer";
    schema = retrieveSchema(dataSourceName,
                            "schemacrawler",
                            "schemacrawler.dbo");
    tables(dataSourceName, schema);
    counts(dataSourceName, schema);

    dataSourceName = "MySQL";
    schema = retrieveSchema(dataSourceName, "schemacrawler", null);
    tables(dataSourceName, schema);
    counts(dataSourceName, schema);

    dataSourceName = "Oracle";
    schema = retrieveSchema(dataSourceName, null, "SCHEMACRAWLER");
    tables(dataSourceName, schema);
    counts(dataSourceName, schema);

    dataSourceName = "PostgreSQL";
    schema = retrieveSchema(dataSourceName, null, null);
    tables(dataSourceName, schema);
    counts(dataSourceName, schema);

  }

  @Test
  public void unknownCatalog()
    throws Exception
  {
    String dataSourceName;
    Schema schema;

    dataSourceName = "MicrosoftSQLServer";
    schema = retrieveSchema(dataSourceName, "unknown", "schemacrawler.dbo");
    assertNull(dataSourceName, schema);

    dataSourceName = "MySQL";
    schema = retrieveSchema(dataSourceName, "unknown", null);
    assertNull(schema);

    // Oracle does not support catalogs, so the catalog rule is ignored
    dataSourceName = "Oracle";
    schema = retrieveSchema(dataSourceName, "unknown", "SCHEMACRAWLER");
    assertNotNull(dataSourceName, schema);

    // PostgreSQL does not support catalogs, so the catalog rule is
    // ignored
    dataSourceName = "PostgreSQL";
    schema = retrieveSchema(dataSourceName, "unknown", null);
    assertNotNull(dataSourceName, schema);

  }

  @Test
  public void unknownSchema()
    throws Exception
  {
    String dataSourceName;
    Schema schema;

    dataSourceName = "MicrosoftSQLServer";
    schema = retrieveSchema(dataSourceName, "schemacrawler", "unknown");
    assertNull(dataSourceName, schema);

    dataSourceName = "MySQL";
    schema = retrieveSchema(dataSourceName, "schemacrawler", "unknown");
    assertNull(dataSourceName, schema);

    dataSourceName = "Oracle";
    schema = retrieveSchema(dataSourceName, null, "unknown");
    assertNull(dataSourceName, schema);

    dataSourceName = "PostgreSQL";
    schema = retrieveSchema(dataSourceName, null, "unknown");
    assertNull(dataSourceName, schema);

  }

  private void counts(String dataSourceName, final Schema schema)
    throws Exception
  {

    final int[] tableColumnCounts = {
        5, 3, 3, 5, 3, 2
    };
    final int[] checkConstraints = {
        0, 0, 0, 0, 0, 0
    };
    // final int[] indexCounts = {
    // 0, 0, 2, 4, 0, 2
    // };
    final int[] fkCounts = {
        1, 0, 2, 2, 1, 0
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
      assertEquals(String
        .format("%s table %s check constraints count does not match",
                dataSourceName,
                table.getFullName()), checkConstraints[tableIdx], table
        .getCheckConstraints().length);
      // assertEquals(String.format("%s table %s index count does not match",dataSourceName,
      // table
      // .getFullName()), indexCounts[tableIdx],
      // table.getIndices().length);
      assertEquals(String
        .format("%s table %s foreign key count does not match",
                dataSourceName,
                table.getFullName()), fkCounts[tableIdx], table
        .getForeignKeys().length);
    }
  }

  private Schema retrieveSchema(final String dataSourceName,
                                final String catalogInclusion,
                                final String schemaInclusion)
    throws Exception
  {
    final DataSource dataSource = (DataSource) appContext
      .getBean(dataSourceName);
    final Connection connection = dataSource.getConnection();

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
    if (catalogInclusion != null)
    {
      schemaCrawlerOptions
        .setCatalogInclusionRule(new InclusionRule(catalogInclusion,
                                                   InclusionRule.NONE));
    }
    if (schemaInclusion != null)
    {
      schemaCrawlerOptions
        .setSchemaInclusionRule(new InclusionRule(schemaInclusion,
                                                  InclusionRule.NONE));
    }

    try
    {
      final Database database = SchemaCrawlerUtility
        .getDatabase(connection, schemaCrawlerOptions);

      final Schema schema;
      final Catalog[] catalogs = database.getCatalogs();
      if (catalogs != null && catalogs.length > 0)
      {
        final Catalog catalog = catalogs[0];
        if (catalog == null)
        {
          throw new NullPointerException("No catalog found for "
                                         + dataSourceName);
        }

        final Schema[] schemas = catalog.getSchemas();
        schema = schemas[0];
      }
      else
      {
        schema = null;
      }
      return schema;
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException(dataSourceName, e);
    }

  }

  private void tables(final String dataSourceName, final Schema schema)
    throws Exception
  {
    final String[] tableNames = {
        "CUSTOMER", "CUSTOMERLIST", "INVOICE", "ITEM", "PRODUCT", "SUPPLIER"
    };
    final String[] tableTypes = {
        "TABLE", "VIEW", "TABLE", "TABLE", "TABLE", "TABLE"
    };

    final Table[] tables = schema.getTables();
    assertEquals(dataSourceName + " table count does not match",
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

}
