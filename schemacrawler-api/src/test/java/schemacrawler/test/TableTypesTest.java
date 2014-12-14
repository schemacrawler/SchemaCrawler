/*
 * SchemaCrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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
import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.createTempFile;
import static sf.util.Utility.UTF8;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.utility.NamedObjectSort;

public class TableTypesTest
  extends BaseDatabaseTest
{

  private static final String TABLE_TYPES_OUTPUT = "table_types/";

  @Test
  public void all()
    throws Exception
  {
    test("all.txt", null);
  }

  @Test
  public void bad()
    throws Exception
  {
    test("bad.txt", "BAD TABLE TYPE");
  }

  @Test
  public void defaultTableTypes()
    throws Exception
  {
    test("default.txt", "default");
  }

  @Test
  public void global_temporary()
    throws Exception
  {
    test("global_temporary.txt", "GLOBAL TEMPORARY");
  }

  @Test
  public void mixed()
    throws Exception
  {
    test("mixed.txt", " global temporary, view ");
  }

  @Test
  public void none()
    throws Exception
  {
    test("none.txt", "");
  }

  @Test
  public void system()
    throws Exception
  {
    test("system.txt", "SYSTEM TABLE");
  }

  @Test
  public void tables()
    throws Exception
  {
    test("tables.txt", "TABLE");
  }

  @Test
  public void views()
    throws Exception
  {
    test("views.txt", "VIEW");
  }

  private void test(final String referenceFile, final String tableTypes)
    throws Exception
  {

    final Path testOutputFile = createTempFile(referenceFile, "text");

    try (final PrintWriter writer = new PrintWriter(testOutputFile.toFile(),
                                                    UTF8.name());)
    {

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.standard());
      schemaCrawlerOptions
        .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      if (!"default".equals(tableTypes))
      {
        schemaCrawlerOptions.setTableTypesFromString(tableTypes);
      }

      final Catalog catalog = getCatalog(schemaCrawlerOptions);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertEquals("Schema count does not match", 5, schemas.length);
      for (final Schema schema: schemas)
      {
        writer.println(String.format("%s", schema.getFullName()));
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table: tables)
        {
          writer.println(String.format("  %s [%s]",
                                       table.getName(),
                                       table.getTableType()));
          final Column[] columns = table.getColumns().toArray(new Column[0]);
          Arrays.sort(columns);
          for (final Column column: columns)
          {
            writer.println(String.format("    %s [%s]",
                                         column.getName(),
                                         column.getColumnDataType()));
          }
        }
      }
    }

    final List<String> failures = compareOutput(TABLE_TYPES_OUTPUT
                                                + referenceFile, testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
