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

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.utility.NamedObjectSort;

public class TableTypesTest
extends BaseDatabaseTest
{

  private static final String TABLE_TYPES_OUTPUT = "table_types/";

  @Test
  public void all()
      throws Exception
  {

    final String referenceFile = "all.txt";
    final File testOutputFile = File.createTempFile("schemacrawler."
        + referenceFile + ".",
        ".test");
    testOutputFile.delete();

    try (final PrintWriter writer = new PrintWriter(testOutputFile, "UTF-8");)
    {

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.standard());
      schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      schemaCrawlerOptions.setTableTypesFromString(null);

      final Database database = getDatabase(schemaCrawlerOptions);
      writeTables(database, writer);
    }

    final List<String> failures = TestUtility
        .compareOutput(TABLE_TYPES_OUTPUT + referenceFile, testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void bad()
      throws Exception
  {

    final String referenceFile = "bad.txt";
    final File testOutputFile = File.createTempFile("schemacrawler."
        + referenceFile + ".",
        ".test");
    testOutputFile.delete();

    try (final PrintWriter writer = new PrintWriter(testOutputFile, "UTF-8");)
    {

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.standard());
      schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      schemaCrawlerOptions.setTableTypesFromString("BAD TABLE TYPE");

      final Database database = getDatabase(schemaCrawlerOptions);
      writeTables(database, writer);
    }

    final List<String> failures = TestUtility
        .compareOutput(TABLE_TYPES_OUTPUT + referenceFile, testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void defaultTableTypes()
      throws Exception
  {

    final String referenceFile = "default.txt";
    final File testOutputFile = File.createTempFile("schemacrawler."
        + referenceFile + ".",
        ".test");
    testOutputFile.delete();

    try (final PrintWriter writer = new PrintWriter(testOutputFile, "UTF-8");)
    {

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.standard());
      schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      // schemaCrawlerOptions.setTableTypesFromString();

      final Database database = getDatabase(schemaCrawlerOptions);
      writeTables(database, writer);
    }

    final List<String> failures = TestUtility
        .compareOutput(TABLE_TYPES_OUTPUT + referenceFile, testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void global_temporary()
      throws Exception
  {

    final String referenceFile = "global_temporary.txt";
    final File testOutputFile = File.createTempFile("schemacrawler."
        + referenceFile + ".",
        ".test");
    testOutputFile.delete();

    try (final PrintWriter writer = new PrintWriter(testOutputFile, "UTF-8");)
    {

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.standard());
      schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      schemaCrawlerOptions.setTableTypesFromString("GLOBAL TEMPORARY");

      final Database database = getDatabase(schemaCrawlerOptions);
      writeTables(database, writer);
    }

    final List<String> failures = TestUtility
        .compareOutput(TABLE_TYPES_OUTPUT + referenceFile, testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void none()
      throws Exception
  {

    final String referenceFile = "none.txt";
    final File testOutputFile = File.createTempFile("schemacrawler."
        + referenceFile + ".",
        ".test");
    testOutputFile.delete();

    try (final PrintWriter writer = new PrintWriter(testOutputFile, "UTF-8");)
    {

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.standard());
      schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      schemaCrawlerOptions.setTableTypesFromString("");

      final Database database = getDatabase(schemaCrawlerOptions);
      writeTables(database, writer);
    }

    final List<String> failures = TestUtility
        .compareOutput(TABLE_TYPES_OUTPUT + referenceFile, testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void system()
      throws Exception
  {

    final String referenceFile = "system.txt";
    final File testOutputFile = File.createTempFile("schemacrawler."
        + referenceFile + ".",
        ".test");
    testOutputFile.delete();

    try (final PrintWriter writer = new PrintWriter(testOutputFile, "UTF-8");)
    {

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.standard());
      schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      schemaCrawlerOptions.setTableTypesFromString("SYSTEM TABLE");

      final Database database = getDatabase(schemaCrawlerOptions);
      writeTables(database, writer);
    }

    final List<String> failures = TestUtility
        .compareOutput(TABLE_TYPES_OUTPUT + referenceFile, testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void tables()
      throws Exception
  {

    final String referenceFile = "tables.txt";
    final File testOutputFile = File.createTempFile("schemacrawler."
        + referenceFile + ".",
        ".test");
    testOutputFile.delete();

    try (final PrintWriter writer = new PrintWriter(testOutputFile, "UTF-8");)
    {

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.standard());
      schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      schemaCrawlerOptions.setTableTypesFromString("TABLE");

      final Database database = getDatabase(schemaCrawlerOptions);
      writeTables(database, writer);
    }

    final List<String> failures = TestUtility
        .compareOutput(TABLE_TYPES_OUTPUT + referenceFile, testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void views()
      throws Exception
  {

    final String referenceFile = "views.txt";
    final File testOutputFile = File.createTempFile("schemacrawler."
        + referenceFile + ".",
        ".test");
    testOutputFile.delete();

    try (final PrintWriter writer = new PrintWriter(testOutputFile, "UTF-8");)
    {

      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.standard());
      schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      schemaCrawlerOptions.setTableTypesFromString("VIEW");

      final Database database = getDatabase(schemaCrawlerOptions);
      writeTables(database, writer);
    }

    final List<String> failures = TestUtility
        .compareOutput(TABLE_TYPES_OUTPUT + referenceFile, testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  private void writeTables(final Database database, final PrintWriter writer)
  {
    final Schema[] schemas = database.getSchemas().toArray(new Schema[0]);
    assertEquals("Schema count does not match", 5, schemas.length);
    for (final Schema schema: schemas)
    {
      writer.println(String.format("%s", schema.getFullName()));
      final Table[] tables = database.getTables(schema).toArray(new Table[0]);
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

}
