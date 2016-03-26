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


import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;
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
    try (final TestWriter out = new TestWriter("text");)
    {
      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = new SchemaCrawlerOptionsBuilder();
      schemaCrawlerOptionsBuilder
        .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
      schemaCrawlerOptionsBuilder
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      if (!"default".equals(tableTypes))
      {
        schemaCrawlerOptionsBuilder.tableTypes(tableTypes);
      }

      final Catalog catalog = getCatalog(schemaCrawlerOptionsBuilder
        .toOptions());
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertEquals("Schema count does not match", 5, schemas.length);
      for (final Schema schema: schemas)
      {
        out.println(String.format("%s", schema.getFullName()));
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table: tables)
        {
          out.println(String.format("  %s [%s]",
                                    table.getName(),
                                    table.getTableType()));
          final Column[] columns = table.getColumns().toArray(new Column[0]);
          Arrays.sort(columns);
          for (final Column column: columns)
          {
            out.println(String.format("    %s [%s]",
                                      column.getName(),
                                      column.getColumnDataType()));
          }
        }
      }

      out.assertEquals(TABLE_TYPES_OUTPUT + referenceFile);
    }
  }

}
