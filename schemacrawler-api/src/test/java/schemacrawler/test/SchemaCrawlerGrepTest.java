/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineColumn;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;

public class SchemaCrawlerGrepTest
  extends BaseDatabaseTest
{

  @Test
  public void grepColumns(final TestInfo testInfo)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
        .builder()
        .includeGreppedColumns(new RegularExpressionInclusionRule(".*\\..*\\.BOOKID"))
        .toOptions();

      final Catalog catalog = getCatalog(schemaCrawlerOptions);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas.length, is(6));
      for (final Schema schema: schemas)
      {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        for (final Table table: tables)
        {
          out.println("  table: " + table.getFullName());
          final Column[] columns = table.getColumns().toArray(new Column[0]);
          Arrays.sort(columns);
          for (final Column column: columns)
          {
            out.println("    column: " + column.getFullName());
          }
        }
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(currentMethodFullName(testInfo))));
  }

  @Test
  public void grepColumnsAndIncludeChildTables()
    throws Exception
  {

    SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
      .builder()
      .includeGreppedColumns(new RegularExpressionInclusionRule(".*\\.BOOKAUTHORS\\..*"))
      .toOptions();

    Catalog catalog;
    Schema schema;
    Table table;

    catalog = getCatalog(schemaCrawlerOptions);
    schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat("Schema PUBLIC.BOOKS not found", schema, notNullValue());
    assertThat(catalog.getTables(schema), hasSize(1));
    table = catalog.lookupTable(schema, "BOOKAUTHORS").get();
    assertThat("Table BOOKAUTHORS not found", table, notNullValue());

    schemaCrawlerOptions = SchemaCrawlerOptionsBuilder.builder()
      .fromOptions(schemaCrawlerOptions).parentTableFilterDepth(1).toOptions();
    catalog = getCatalog(schemaCrawlerOptions);
    schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat("Schema PUBLIC.BOOKS not found", schema, notNullValue());
    assertThat(catalog.getTables(schema).size(), is(3));
    table = catalog.lookupTable(schema, "BOOKAUTHORS").get();
    assertThat("Table BOOKAUTHORS not found", table, notNullValue());
    table = catalog.lookupTable(schema, "BOOKS").get();
    assertThat("Table BOOKS not found", table, notNullValue());
    table = catalog.lookupTable(schema, "AUTHORS").get();
    assertThat("Table AUTHORS not found", table, notNullValue());

  }

  @Test
  public void grepCombined(final TestInfo testInfo)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
        .builder()
        .includeGreppedColumns(new RegularExpressionInclusionRule(".*\\..*\\.BOOKID"))
        .includeGreppedDefinitions(new RegularExpressionInclusionRule(".*book author.*"))
        .toOptions();

      final Catalog catalog = getCatalog(schemaCrawlerOptions);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas.length, is(6));
      for (final Schema schema: schemas)
      {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        for (final Table table: tables)
        {
          out.println("  table: " + table.getFullName());
          final Column[] columns = table.getColumns().toArray(new Column[0]);
          Arrays.sort(columns);
          for (final Column column: columns)
          {
            out.println("    column: " + column.getFullName());
          }
        }
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(currentMethodFullName(testInfo))));
  }

  @Test
  public void grepDefinitions(final TestInfo testInfo)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
        .builder()
        .includeGreppedDefinitions(new RegularExpressionInclusionRule(".*book author.*"))
        .toOptions();

      final Catalog catalog = getCatalog(schemaCrawlerOptions);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas.length, is(6));
      for (final Schema schema: schemas)
      {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        for (final Table table: tables)
        {
          out.println("  table: " + table.getFullName());
          final Column[] columns = table.getColumns().toArray(new Column[0]);
          Arrays.sort(columns);
          for (final Column column: columns)
          {
            out.println("    column: " + column.getFullName());
          }
        }
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(currentMethodFullName(testInfo))));
  }

  @Test
  public void grepProcedures(final TestInfo testInfo)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().includeAllRoutines()
        .includeGreppedRoutineColumns(new RegularExpressionInclusionRule(".*\\.B_COUNT"));
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final Catalog catalog = getCatalog(schemaCrawlerOptions);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas.length, is(6));
      for (final Schema schema: schemas)
      {
        out.println("schema: " + schema.getFullName());
        final Routine[] routines = catalog.getRoutines(schema)
          .toArray(new Routine[0]);
        for (final Routine routine: routines)
        {
          out.println("  routine: " + routine.getFullName());
          final RoutineColumn[] columns = routine.getColumns()
            .toArray(new RoutineColumn[0]);
          for (final RoutineColumn column: columns)
          {
            out.println("    parameter: " + column.getFullName());
          }
        }
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(currentMethodFullName(testInfo))));

  }

}
