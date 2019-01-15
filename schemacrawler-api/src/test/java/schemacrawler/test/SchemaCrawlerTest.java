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
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.IsEmptyOptional.emptyOptional;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Constraint;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseProperty;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Property;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.BaseSchemaCrawlerTest;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.utility.NamedObjectSort;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class SchemaCrawlerTest
  extends BaseSchemaCrawlerTest
{

  @Test
  public void columnDataTypes(final TestContext testContext,
                              final Connection connection)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptions schemaRetrievalOptions = SchemaRetrievalOptionsBuilder
        .newSchemaRetrievalOptions(config);

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
        .includeAllRoutines();
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final Catalog catalog = getCatalog(connection,
                                         schemaRetrievalOptions,
                                         schemaCrawlerOptions);
      final Collection<ColumnDataType> columnDataTypes = catalog
        .getColumnDataTypes();
      assertThat("ColumnDataType count does not match",
                 columnDataTypes,
                 hasSize(30));
      for (final ColumnDataType columnDataType: columnDataTypes)
      {
        assertThat(columnDataType, notNullValue());
        out.println(printColumnDataType(columnDataType));
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(testContext
                 .currentMethodFullName())));
  }

  @Test
  public void columnLookup(final TestContext testContext,
                           final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
      .newSchemaCrawlerOptions();

    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
    assertThat(catalog, notNullValue());
    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat(schema, notNullValue());
    final Table table = catalog.lookupTable(schema, "AUTHORS").get();
    assertThat(table, notNullValue());
    assertThat(table.lookupColumn(null), is(emptyOptional()));
    assertThat(table.lookupColumn(""), is(emptyOptional()));
    assertThat(table.lookupColumn("NO_COLUMN"), is(emptyOptional()));
    assertThat(table.lookupColumn("ID"), is(not(emptyOptional())));
  }

  @Test
  public void columns(final TestContext testContext,
                      final Connection connection)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptions schemaRetrievalOptions = SchemaRetrievalOptionsBuilder
        .newSchemaRetrievalOptions(config);

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final Catalog catalog = getCatalog(connection,
                                         schemaRetrievalOptions,
                                         schemaCrawlerOptions);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema: schemas)
      {
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table: tables)
        {
          final Column[] columns = table.getColumns().toArray(new Column[0]);
          Arrays.sort(columns);
          for (final Column column: columns)
          {
            out.println(String.format("%s", column.getFullName()));

            out.println(String
              .format("  - %s=%s", "data-type", column.getColumnDataType()));
            out.println(String.format("  - %s=%s", "size", column.getSize()));
            out.println(String.format("  - %s=%s",
                                      "decimal digits",
                                      column.getDecimalDigits()));
            out.println(String.format("  - %s=%s", "width", column.getWidth()));
            out.println(String
              .format("  - %s=%s", "default value", column.getDefaultValue()));
            out.println(String.format("  - %s=%s",
                                      "auto-incremented",
                                      column.isAutoIncremented()));
            out.println(String
              .format("  - %s=%s", "nullable", column.isNullable()));
            out.println(String
              .format("  - %s=%s", "generated", column.isGenerated()));
            out.println(String.format("  - %s=%s",
                                      "part of primary key",
                                      column.isPartOfPrimaryKey()));
            out.println(String.format("  - %s=%s",
                                      "part of foreign key",
                                      column.isPartOfForeignKey()));
            out.println(String.format("  - %s=%s",
                                      "ordinal position",
                                      column.getOrdinalPosition()));
            out.println(String
              .format("  - %s=%s", "remarks", column.getRemarks()));

            out.println(String.format("  - %s=%s", "attibutes", ""));
            final SortedMap<String, Object> columnAttributes = new TreeMap<>(column
              .getAttributes());
            for (final Entry<String, Object> columnAttribute: columnAttributes
              .entrySet())
            {
              out.println(String.format("    ~ %s=%s",
                                        columnAttribute.getKey(),
                                        columnAttribute.getValue()));
            }
          }

          out.println();
        }
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(testContext
                 .currentMethodFullName())));
  }

  @Test
  public void counts(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptions schemaRetrievalOptions = SchemaRetrievalOptionsBuilder
        .newSchemaRetrievalOptions(config);

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final Catalog catalog = getCatalog(connection,
                                         schemaRetrievalOptions,
                                         schemaCrawlerOptions);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema: schemas)
      {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table: tables)
        {
          out.println("  table: " + table.getFullName());
          out.println("    # columns: " + table.getColumns().size());
          out.println("    # constraints: "
                      + table.getTableConstraints().size());
          out.println("    # indexes: " + table.getIndexes().size());
          out.println("    # foreign keys: " + table.getForeignKeys().size());
          out.println("    # imported foreign keys: "
                      + table.getExportedForeignKeys().size());
          out.println("    # exported: "
                      + table.getImportedForeignKeys().size());
          out.println("    # privileges: " + table.getPrivileges().size());
        }
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(testContext
                 .currentMethodFullName())));
  }

  @Test
  public void databaseInfo(final TestContext testContext,
                           final Connection connection)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptions schemaRetrievalOptions = SchemaRetrievalOptionsBuilder
        .newSchemaRetrievalOptions(config);

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
        .includeAllRoutines();
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final Catalog catalog = getCatalog(connection,
                                         schemaRetrievalOptions,
                                         schemaCrawlerOptions);
      final DatabaseInfo databaseInfo = catalog.getDatabaseInfo();
      final Collection<DatabaseProperty> dbProperties = databaseInfo
        .getProperties();
      assertThat("Database property count does not match",
                 dbProperties,
                 hasSize(158));
      final Collection<Property> serverInfo = databaseInfo.getServerInfo();
      assertThat("Server info property count does not match",
                 serverInfo,
                 is(empty()));
      out.println(String.format("username=%s", databaseInfo.getUserName()));
      out.println(String.format("product name=%s",
                                databaseInfo.getProductName()));
      out.println(String.format("product version=%s",
                                databaseInfo.getProductVersion()));
      out.println(String.format("catalog=%s", catalog.getName()));
      for (final Property serverInfoProperty: serverInfo)
      {
        assertThat(serverInfoProperty, notNullValue());
        out.println(serverInfoProperty);
      }
      for (final DatabaseProperty dbProperty: dbProperties)
      {
        assertThat(dbProperty, notNullValue());
        out.println(dbProperty);
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(testContext
                 .currentMethodFullName())));
  }

  private String printColumnDataType(final ColumnDataType columnDataType)
  {
    final StringBuffer buffer = new StringBuffer();

    final boolean isUserDefined = columnDataType.isUserDefined();
    final String typeName = columnDataType.getFullName();
    final String dataType = (isUserDefined? "user defined ": "")
                            + "column data-type";
    final String nullable = (columnDataType.isNullable()? "": "not ")
                            + "nullable";
    final String autoIncrementable = (columnDataType
      .isAutoIncrementable()? "": "not ") + "auto-incrementable";

    final String createParameters = columnDataType.getCreateParameters();
    final String definedWith = "defined with "
                               + (isBlank(createParameters)? "no parameters"
                                                           : createParameters);

    final String literalPrefix = columnDataType.getLiteralPrefix();
    final String literalPrefixText = isBlank(literalPrefix)? "no literal prefix"
                                                           : "literal prefix "
                                                             + literalPrefix;

    final String literalSuffix = columnDataType.getLiteralSuffix();
    final String literalSuffixText = isBlank(literalSuffix)? "no literal suffix"
                                                           : "literal suffix "
                                                             + literalSuffix;

    final String javaSqlType = "java.sql.Types: "
                               + columnDataType.getJavaSqlType().getName();

    final String precision = "precision " + columnDataType.getPrecision();
    final String minimumScale = "minimum scale "
                                + columnDataType.getMinimumScale();
    final String maximumScale = "maximum scale "
                                + columnDataType.getMaximumScale();

    buffer.append(typeName).append("\n").append("  ").append(dataType)
      .append("\n").append("  ").append(definedWith).append("\n").append("  ")
      .append(nullable).append("\n").append("  ").append(autoIncrementable)
      .append("\n").append("  ").append(literalPrefixText).append("\n")
      .append("  ").append(literalSuffixText).append("\n").append("  ")
      .append(columnDataType.getSearchable().toString()).append("\n")
      .append("  ").append(precision).append("\n").append("  ")
      .append(minimumScale).append("\n").append("  ").append(maximumScale)
      .append("\n").append("  ").append(javaSqlType);
    if (isUserDefined)
    {
      final String baseTypeName;
      final ColumnDataType baseColumnDataType = columnDataType.getBaseType();
      if (baseColumnDataType == null)
      {
        baseTypeName = "";
      }
      else
      {
        baseTypeName = baseColumnDataType.getFullName();
      }
      buffer.append("\n").append("  ").append("based on ").append(baseTypeName);
    }

    return buffer.toString();
  }

  @Test
  public void relatedTables(final TestContext testContext,
                            final Connection connection)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder()
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
      final Table[] tables = catalog.getTables().toArray(new Table[0]);
      assertThat("Table count does not match", tables, arrayWithSize(13));
      Arrays.sort(tables, NamedObjectSort.alphabetical);
      for (final Table table: tables)
      {
        out.println("  table: " + table.getFullName());
        out.println("    # columns: " + table.getColumns().size());
        out.println("    # child tables: "
                    + table.getRelatedTables(TableRelationshipType.child));
        out.println("    # parent tables: "
                    + table.getRelatedTables(TableRelationshipType.parent));
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(testContext
                 .currentMethodFullName())));
  }

  @Test
  public void relatedTablesWithTableRestriction(final TestContext testContext,
                                                final Connection connection)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder()
        .includeTables(new RegularExpressionInclusionRule(".*\\.AUTHORS"));
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
      final Table[] tables = catalog.getTables().toArray(new Table[0]);
      assertThat("Table count does not match", tables, arrayWithSize(1));
      Arrays.sort(tables, NamedObjectSort.alphabetical);
      for (final Table table: tables)
      {
        out.println("  table: " + table.getFullName());
        out.println("    # columns: " + table.getColumns().size());
        out.println("    # child tables: "
                    + table.getRelatedTables(TableRelationshipType.child));
        out.println("    # parent tables: "
                    + table.getRelatedTables(TableRelationshipType.parent));
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(testContext
                 .currentMethodFullName())));
  }

  @Test
  public void routineDefinitions(final TestContext testContext,
                                 final Connection connection)
    throws Exception
  {
    final Config config = loadHsqldbConfig();

    final SchemaRetrievalOptions schemaRetrievalOptions = SchemaRetrievalOptionsBuilder
      .newSchemaRetrievalOptions(config);

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeAllRoutines();
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final Catalog catalog = getCatalog(connection,
                                       schemaRetrievalOptions,
                                       schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final Routine[] routines = catalog.getRoutines(schema)
      .toArray(new Routine[0]);
    assertThat("Wrong number of routines", routines, arrayWithSize(4));
    for (final Routine routine: routines)
    {
      assertThat("Routine definition not found, for " + routine,
                 isBlank(routine.getDefinition()),
                 is(false));
    }
  }

  @Test
  public void schemaEquals(final TestContext testContext,
                           final Connection connection)
    throws Exception
  {

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.detailed())
      .includeAllRoutines();
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
    final Schema schema1 = new SchemaReference("PUBLIC", "BOOKS");
    assertThat("Could not find any tables",
               catalog.getTables(schema1),
               not(empty()));
    assertThat("Wrong number of routines",
               catalog.getRoutines(schema1),
               hasSize(4));

    final Schema schema2 = new SchemaReference("PUBLIC", "BOOKS");

    assertThat("Schema not not match", schema1, equalTo(schema2));
    assertThat("Tables do not match",
               catalog.getTables(schema1),
               equalTo(catalog.getTables(schema2)));
    assertThat("Routines do not match",
               catalog.getRoutines(schema1),
               equalTo(catalog.getRoutines(schema2)));

    // Try negative test
    final Table table1 = catalog.getTables(schema1).toArray(new Table[0])[0];
    final Table table2 = catalog.getTables(schema1).toArray(new Table[0])[1];
    assertThat("Tables should not be equal", table1, not(equalTo(table2)));

  }

  @Test
  public void sequences(final TestContext testContext,
                        final Connection connection)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptions schemaRetrievalOptions = SchemaRetrievalOptionsBuilder
        .newSchemaRetrievalOptions(config);

      final SchemaInfoLevel schemaInfoLevel = SchemaInfoLevelBuilder.builder()
        .withInfoLevel(InfoLevel.minimum).setRetrieveSequenceInformation(true)
        .toOptions();

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(schemaInfoLevel).includeAllSequences();
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final Catalog catalog = getCatalog(connection,
                                         schemaRetrievalOptions,
                                         schemaCrawlerOptions);
      final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
      assertThat("BOOKS Schema not found", schema, notNullValue());
      final Sequence[] sequences = catalog.getSequences(schema)
        .toArray(new Sequence[0]);
      assertThat("Sequence count does not match", sequences, arrayWithSize(1));
      for (final Sequence sequence: sequences)
      {
        assertThat(sequence, notNullValue());
        out.println("sequence: " + sequence.getName());
        out.println("  increment: " + sequence.getIncrement());
        out.println("  minimum value: " + sequence.getMinimumValue());
        out.println("  maximum value: " + sequence.getMaximumValue());
        out.println("  cycle?: " + sequence.isCycle());
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(testContext
                 .currentMethodFullName())));
  }

  @Test
  public void synonyms(final TestContext testContext,
                       final Connection connection)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptions schemaRetrievalOptions = SchemaRetrievalOptionsBuilder
        .newSchemaRetrievalOptions(config);

      final SchemaInfoLevel schemaInfoLevel = SchemaInfoLevelBuilder.builder()
        .withInfoLevel(InfoLevel.minimum).setRetrieveSynonymInformation(true)
        .toOptions();

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(schemaInfoLevel).includeAllSynonyms();
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final Catalog catalog = getCatalog(connection,
                                         schemaRetrievalOptions,
                                         schemaCrawlerOptions);
      final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
      assertThat("BOOKS Schema not found", schema, notNullValue());
      final Synonym[] synonyms = catalog.getSynonyms(schema)
        .toArray(new Synonym[0]);
      assertThat("Synonym count does not match", synonyms, arrayWithSize(1));
      for (final Synonym synonym: synonyms)
      {
        assertThat(synonym, notNullValue());
        out.println("synonym: " + synonym.getName());
        out.println("  class: "
                    + synonym.getReferencedObject().getClass().getSimpleName());
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(testContext
                 .currentMethodFullName())));
  }

  @Test
  public void tableConstraints(final TestContext testContext,
                               final Connection connection)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptions schemaRetrievalOptions = SchemaRetrievalOptionsBuilder
        .newSchemaRetrievalOptions(config);

      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsWithMaximumSchemaInfoLevel();

      final Catalog catalog = getCatalog(connection,
                                         schemaRetrievalOptions,
                                         schemaCrawlerOptions);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(6));
      for (final Schema schema: schemas)
      {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        for (final Table table: tables)
        {
          out.println("  table: " + table.getFullName());
          final Constraint[] tableConstraints = table.getTableConstraints()
            .toArray(new Constraint[0]);
          for (final Constraint tableConstraint: tableConstraints)
          {
            out.println("    constraint: " + tableConstraint.getName());
            out.println("      type: " + tableConstraint.getConstraintType());
            if (tableConstraint instanceof TableConstraint)
            {
              final TableConstraint dependentTableConstraint = (TableConstraint) tableConstraint;
              final List<TableConstraintColumn> columns = dependentTableConstraint
                .getColumns();
              for (final TableConstraintColumn tableConstraintColumn: columns)
              {
                out.println("      on column: "
                            + tableConstraintColumn.getName());
              }
            }
          }
        }
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(testContext
                 .currentMethodFullName())));
  }

  @Test
  public void tables(final TestContext testContext, final Connection connection)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Config config = loadHsqldbConfig();

      final SchemaRetrievalOptions schemaRetrievalOptions = SchemaRetrievalOptionsBuilder
        .newSchemaRetrievalOptions(config);

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
        .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
        .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
      final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
        .toOptions();

      final Catalog catalog = getCatalog(connection,
                                         schemaRetrievalOptions,
                                         schemaCrawlerOptions);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema: schemas)
      {
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table: tables)
        {
          out.println(String
            .format("o--> %s [%s]", table.getFullName(), table.getTableType()));
          final SortedMap<String, Object> tableAttributes = new TreeMap<>(table
            .getAttributes());
          for (final Entry<String, Object> tableAttribute: tableAttributes
            .entrySet())
          {
            out.println(String.format("      ~ %s=%s",
                                      tableAttribute.getKey(),
                                      tableAttribute.getValue()));
          }
        }
      }
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(testContext
                 .currentMethodFullName())));
  }

  @Test
  public void tablesSort(final TestContext testContext,
                         final Connection connection)
    throws Exception
  {

    final String[] tableNames = {
                                  "AUTHORS",
                                  "BOOKS",
                                  "COUPONS",
                                  "CUSTOMERDATA",
                                  "CUSTOMERS",
                                  "Global Counts",
                                  "PUBLISHERS",
                                  "BOOKAUTHORS",
                                  "ΒΙΒΛΊΑ",
                                  "AUTHORSLIST" };
    final Random rnd = new Random();

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder()
      .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
    final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
    assertThat("Schema count does not match", schemas, arrayWithSize(5));
    final Schema schema = schemas[0];

    final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
    for (int i = 0; i < 10; i++)
    {
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++)
      {
        final Table table = tables[tableIdx];
        assertThat("Table name does not match in iteration " + i,
                   tableNames[tableIdx],
                   is(table.getName()));
      }

      // Shuffle array, and sort it again
      for (int k = tables.length; k > 1; k--)
      {
        final int i1 = k - 1;
        final int i2 = rnd.nextInt(k);
        final Table tmp = tables[i1];
        tables[i1] = tables[i2];
        tables[i2] = tmp;
      }
      Arrays.sort(tables);
    }
  }

  @Test
  public void triggers(final TestContext testContext,
                       final Connection connection)
    throws Exception
  {
    final Config config = loadHsqldbConfig();

    final SchemaRetrievalOptions schemaRetrievalOptions = SchemaRetrievalOptionsBuilder
      .newSchemaRetrievalOptions(config);

    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsWithMaximumSchemaInfoLevel();

    final Catalog catalog = getCatalog(connection,
                                       schemaRetrievalOptions,
                                       schemaCrawlerOptions);
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
    boolean foundTrigger = false;
    for (final Table table: tables)
    {
      for (final Trigger trigger: table.getTriggers())
      {
        foundTrigger = true;
        assertThat("Triggers full name does not match",
                   trigger.getFullName(),
                   is("PUBLIC.BOOKS.AUTHORS.TRG_AUTHORS"));
        assertThat("Trigger EventManipulationType does not match",
                   trigger.getEventManipulationType(),
                   is(EventManipulationType.delete));
      }
    }
    assertThat("No triggers found", foundTrigger, is(true));
  }

  @Test
  public void viewDefinitions(final TestContext testContext,
                              final Connection connection)
    throws Exception
  {
    final Config config = loadHsqldbConfig();

    final SchemaRetrievalOptions schemaRetrievalOptions = SchemaRetrievalOptionsBuilder
      .newSchemaRetrievalOptions(config);

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder();
    schemaCrawlerOptionsBuilder.tableTypes("VIEW");
    schemaCrawlerOptionsBuilder
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

    final Catalog catalog = getCatalog(connection,
                                       schemaRetrievalOptions,
                                       schemaCrawlerOptionsBuilder.toOptions());
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final View view = (View) catalog.lookupTable(schema, "AUTHORSLIST").get();
    assertThat("View not found", view, notNullValue());
    assertThat("View definition not found",
               view.getDefinition(),
               notNullValue());
    assertThat("View definition not found",
               isBlank(view.getDefinition()),
               is(false));
  }

}
