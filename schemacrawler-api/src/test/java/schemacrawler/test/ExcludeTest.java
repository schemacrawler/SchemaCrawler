/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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
