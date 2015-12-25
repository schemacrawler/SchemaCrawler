/*
 * SchemaCrawler
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Rule;
import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestName;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.utility.NamedObjectSort;

public class ExcludeTest
  extends BaseDatabaseTest
{

  private static final Logger LOGGER = Logger
    .getLogger(ExcludeTest.class.getName());

  @Rule
  public TestName testName = new TestName();

  @Test
  public void excludeColumns()
    throws Exception
  {
    try (final TestWriter out = new TestWriter("text");)
    {
      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      schemaCrawlerOptions
        .setColumnInclusionRule(new RegularExpressionExclusionRule(".*\\..*\\.ID"));

      final Catalog catalog = getCatalog(schemaCrawlerOptions);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertEquals("Schema count does not match", 5, schemas.length);
      for (final Schema schema: schemas)
      {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table: tables)
        {
          out.println("  table: " + table.getFullName());
          final Column[] columns = table.getColumns().toArray(new Column[0]);
          Arrays.sort(columns);
          for (final Column column: columns)
          {
            LOGGER.log(Level.FINE, column.toString());
            out.println("    column: " + column.getFullName());
            out.println("      database type: " + column.getColumnDataType()
              .getDatabaseSpecificTypeName());
            out.println("      type: " + column.getColumnDataType()
              .getJavaSqlType().getJavaSqlTypeName());
          }
        }
      }

      out.assertEquals(testName.currentMethodFullName());
    }
  }

}
