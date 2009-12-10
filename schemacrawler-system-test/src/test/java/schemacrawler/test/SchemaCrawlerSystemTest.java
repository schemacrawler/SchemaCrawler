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
    Schema schema;

    schema = retrieveSchema("oracle", "", "SCHEMACRAWLER");
    tables(schema);
    // counts(schema);

    schema = retrieveSchema("sql-server",
                            "schemacrawler",
                            "schemacrawler.schemacrawler");
    tables(schema);
    // counts(schema);
  }

  private void counts(final Schema schema)
    throws Exception
  {

    final int tableCount = 6;
    final int[] tableColumnCounts = {
        5, 3, 3, 5, 3, 2
    };
    final int[] checkConstraints = {
        0, 0, 0, 0, 0, 0
    };
    final int[] indexCounts = {
        0, 0, 2, 4, 0, 2
    };
    final int[] fkCounts = {
        1, 0, 2, 2, 1, 0
    };

    final Table[] tables = schema.getTables();
    assertEquals("Table count does not match", tableCount, tables.length);
    for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
    {
      final Table table = tables[tableIdx];
      assertEquals(String.format("Table %s columns count does not match", table
        .getFullName()), tableColumnCounts[tableIdx], table.getColumns().length);
      assertEquals(String
        .format("Table %s check constraints count does not match", table
          .getFullName()), checkConstraints[tableIdx], table
        .getCheckConstraints().length);
      assertEquals(String.format("Table %s index count does not match", table
        .getFullName()), indexCounts[tableIdx], table.getIndices().length);
      assertEquals(String.format("Table %s foreign key count does not match",
                                 table.getFullName()),
                   fkCounts[tableIdx],
                   table.getForeignKeys().length);
    }
  }

  private void tables(final Schema schema)
    throws Exception
  {
    final String[] tableNames = {
        "CUSTOMER", "CUSTOMERLIST", "INVOICE", "ITEM", "PRODUCT", "SUPPLIER"
    };
    final String[] tableTypes = {
        "TABLE", "VIEW", "TABLE", "TABLE", "TABLE", "TABLE"
    };

    final Table[] tables = schema.getTables();
    for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
    {
      final Table table = tables[tableIdx];
      assertEquals("Table name does not match", tableNames[tableIdx], table
        .getName().toUpperCase());
      assertEquals("Table type does not match", tableTypes[tableIdx], table
        .getType().toString().toUpperCase());
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
    schemaCrawlerOptions
      .setCatalogInclusionRule(new InclusionRule(catalogInclusion,
                                                 InclusionRule.NONE));
    schemaCrawlerOptions
      .setSchemaInclusionRule(new InclusionRule(schemaInclusion,
                                                InclusionRule.NONE));

    try
    {
      final Database database = SchemaCrawlerUtility
        .getDatabase(connection, schemaCrawlerOptions);

      final Catalog catalog = database.getCatalogs()[0];
      if (catalog == null)
      {
        throw new NullPointerException("No catalog found for " + dataSourceName);
      }

      final Schema schema = catalog.getSchemas()[0];
      if (schema == null)
      {
        throw new NullPointerException("No schema found for " + dataSourceName);
      }

      return schema;
    }
    catch (Exception e)
    {
      throw new SchemaCrawlerException(dataSourceName, e);
    }

  }
}
