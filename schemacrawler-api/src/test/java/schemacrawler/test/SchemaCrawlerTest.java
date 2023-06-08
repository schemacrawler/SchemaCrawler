/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.crawl.ForeignKeyRetrieverTest.verifyRetrieveForeignKeys;
import static schemacrawler.crawl.IndexRetrieverTest.verifyRetrieveIndexes;
import static schemacrawler.crawl.PrimaryKeyRetrieverTest.verifyRetrievePrimaryKeys;
import static schemacrawler.crawl.TableColumnRetrieverTest.verifyRetrieveTableColumns;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.javaVersion;
import static us.fatehi.utility.Utility.isBlank;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.crawl.AlternateKeyBuilder;
import schemacrawler.crawl.AlternateKeyBuilder.AlternateKeyDefinition;
import schemacrawler.crawl.WeakAssociationBuilder;
import schemacrawler.crawl.WeakAssociationBuilder.WeakAssociationColumn;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.DataTypeType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseProperty;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Grant;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.JdbcDriverProperty;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Property;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineParameter;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schema.WeakAssociation;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.utility.NamedObjectSort;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SchemaCrawlerTest {

  private static String printColumnDataType(final ColumnDataType columnDataType) {
    final StringBuilder buffer = new StringBuilder();

    final boolean isUserDefined = columnDataType.getType() == DataTypeType.user_defined;
    final String typeName = columnDataType.getFullName();
    final String dataType = (isUserDefined ? "user defined " : "") + "column data-type";
    final String nullable = (columnDataType.isNullable() ? "" : "not ") + "nullable";
    final String autoIncrementable =
        (columnDataType.isAutoIncrementable() ? "" : "not ") + "auto-incrementable";

    final String createParameters = columnDataType.getCreateParameters();
    final String definedWith =
        "defined with " + (isBlank(createParameters) ? "no parameters" : createParameters);

    final String literalPrefix = columnDataType.getLiteralPrefix();
    final String literalPrefixText =
        isBlank(literalPrefix) ? "no literal prefix" : "literal prefix " + literalPrefix;

    final String literalSuffix = columnDataType.getLiteralSuffix();
    final String literalSuffixText =
        isBlank(literalSuffix) ? "no literal suffix" : "literal suffix " + literalSuffix;

    final String javaSqlType = "java.sql.Types: " + columnDataType.getJavaSqlType().getName();

    final String precision = "precision " + columnDataType.getPrecision();
    final String minimumScale = "minimum scale " + columnDataType.getMinimumScale();
    final String maximumScale = "maximum scale " + columnDataType.getMaximumScale();

    buffer
        .append(typeName)
        .append("\n")
        .append("  ")
        .append(dataType)
        .append("\n")
        .append("  ")
        .append(definedWith)
        .append("\n")
        .append("  ")
        .append(nullable)
        .append("\n")
        .append("  ")
        .append(autoIncrementable)
        .append("\n")
        .append("  ")
        .append(literalPrefixText)
        .append("\n")
        .append("  ")
        .append(literalSuffixText)
        .append("\n")
        .append("  ")
        .append(columnDataType.getSearchable().toString())
        .append("\n")
        .append("  ")
        .append(precision)
        .append("\n")
        .append("  ")
        .append(minimumScale)
        .append("\n")
        .append("  ")
        .append(maximumScale)
        .append("\n")
        .append("  ")
        .append(javaSqlType);
    if (isUserDefined) {
      final String baseTypeName;
      final ColumnDataType baseColumnDataType = columnDataType.getBaseType();
      if (baseColumnDataType == null) {
        baseTypeName = "";
      } else {
        baseTypeName = baseColumnDataType.getFullName();
      }
      buffer.append("\n").append("  ").append("based on ").append(baseTypeName);
    }

    return buffer.toString();
  }

  private Catalog catalog;

  @Test
  public void alternateKeys(final TestContext testContext) throws Exception {

    final Schema alternateKeySchema = new SchemaReference("PUBLIC", "BOOKS");

    final AlternateKeyBuilder builder = AlternateKeyBuilder.builder(catalog);
    // 1. Happy path - good alternate key
    builder.addAlternateKey(
        new AlternateKeyDefinition(
            alternateKeySchema, "AUTHORS", "1_alternate_key", Arrays.asList("ID")));
    // 2. External table - not built
    builder.addAlternateKey(
        new AlternateKeyDefinition(
            alternateKeySchema, "OTHERTABLE", "2_alternate_key", Arrays.asList("ID")));
    // 3. External column - not built
    builder.addAlternateKey(
        new AlternateKeyDefinition(
            alternateKeySchema, "AUTHORS", "3_alternate_key", Arrays.asList("OTHERCOLUMN")));

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        for (final Table table : tables) {
          out.println("  table: " + table.getFullName());
          final Collection<PrimaryKey> alternateKeys = table.getAlternateKeys();
          for (final PrimaryKey alternateKey : alternateKeys) {
            out.println("    altermate key: " + alternateKey.getName());
            out.println("      columns: ");
            final List<TableConstraintColumn> constrainedColumns =
                alternateKey.getConstrainedColumns();
            for (final TableConstraintColumn tableConstraintColumn : constrainedColumns) {
              out.println("        ordinal: " + tableConstraintColumn.getOrdinalPosition());
              out.println("          " + tableConstraintColumn);
            }
          }
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void columnDataTypes(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Collection<ColumnDataType> columnDataTypes = catalog.getColumnDataTypes();
      assertThat("ColumnDataType count does not match", columnDataTypes, hasSize(32));
      for (final ColumnDataType columnDataType : columnDataTypes) {
        assertThat(columnDataType, notNullValue());
        out.println(printColumnDataType(columnDataType));
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void columnLookup() {
    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat(schema, notNullValue());
    final Table table = catalog.lookupTable(schema, "AUTHORS").get();
    assertThat(table, notNullValue());
    assertThat(table.lookupColumn(null), isEmpty());
    assertThat(table.lookupColumn(""), isEmpty());
    assertThat(table.lookupColumn("NO_COLUMN"), isEmpty());
    assertThat(table.lookupColumn("ID"), not(isEmpty()));
  }

  @Test
  public void columnPrivileges(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      final Table table = catalog.lookupTable(schemas[0], "AUTHORS").get();
      out.println(table.getFullName());
      for (final Column column : table.getColumns()) {
        out.println("  " + column.getName());
        final Collection<Privilege<Column>> privileges = column.getPrivileges();
        for (final Privilege<Column> privilege : privileges) {
          out.println(String.format("    privilege: %s", privilege.getName()));
          final Collection<Grant<Column>> grants = privilege.getGrants();
          for (final Grant<Column> grant : grants) {
            out.println("      " + grant);
          }
        }
      }
    }

    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void counts(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          out.println("  table: " + table.getFullName());
          out.println("    # columns: " + table.getColumns().size());
          out.println("    # constraints: " + table.getTableConstraints().size());
          out.println("    # indexes: " + table.getIndexes().size());
          out.println("    # foreign keys: " + table.getForeignKeys().size());
          out.println("    # imported foreign keys: " + table.getExportedForeignKeys().size());
          out.println("    # exported: " + table.getImportedForeignKeys().size());
          out.println("    # privileges: " + table.getPrivileges().size());
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void databaseInfo(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final DatabaseInfo databaseInfo = catalog.getDatabaseInfo();
      final Collection<DatabaseProperty> dbProperties = databaseInfo.getProperties();
      final Collection<Property> serverInfo = databaseInfo.getServerInfo();
      assertThat("Server info property count does not match", serverInfo, is(empty()));
      out.println(String.format("username=%s", databaseInfo.getUserName()));
      out.println(String.format("product name=%s", databaseInfo.getProductName()));
      out.println(String.format("product version=%s", databaseInfo.getProductVersion()));
      out.println(String.format("catalog=%s", catalog.getName()));
      for (final Property serverInfoProperty : serverInfo) {
        assertThat(serverInfoProperty, notNullValue());
        out.println(serverInfoProperty);
      }
      for (final DatabaseProperty dbProperty : dbProperties) {
        assertThat(dbProperty, notNullValue());
        out.println(dbProperty);
      }

      final JdbcDriverInfo jdbcDriverInfo = catalog.getJdbcDriverInfo();
      out.println(String.format("connection url=%s", jdbcDriverInfo.getConnectionUrl()));
      out.println(String.format("driver class=%s", jdbcDriverInfo.getDriverClassName()));
      final Collection<JdbcDriverProperty> driverProperties = jdbcDriverInfo.getDriverProperties();
      for (final JdbcDriverProperty driverProperty : driverProperties) {
        assertThat(driverProperty, notNullValue());
        out.println(driverProperty);
      }
    }
    final String expectedResultsResource =
        String.format("%s.%s", testContext.testMethodFullName(), javaVersion());
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResultsResource)));
  }

  @Test
  public void foreignKeys(final TestContext testContext) throws Exception {
    verifyRetrieveForeignKeys(catalog);
  }

  @Test
  public void indexes(final TestContext testContext) throws Exception {
    verifyRetrieveIndexes(catalog);
  }

  @BeforeAll
  public void loadCatalog(final Connection connection) throws Exception {

    final SchemaRetrievalOptions schemaRetrievalOptions = TestUtility.newSchemaRetrievalOptions();

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"))
            .includeAllSynonyms()
            .includeAllSequences()
            .includeAllRoutines();
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    catalog = getCatalog(connection, schemaRetrievalOptions, schemaCrawlerOptions);
  }

  @Test
  public void primaryKeys(final TestContext testContext) throws Exception {
    verifyRetrievePrimaryKeys(catalog);
  }

  @Test
  public void relatedTables(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Table[] tables = catalog.getTables().toArray(new Table[0]);
      assertThat("Table count does not match", tables, arrayWithSize(14));
      Arrays.sort(tables, NamedObjectSort.alphabetical);
      for (final Table table : tables) {
        out.println("  table: " + table.getFullName());
        out.println("    # columns: " + table.getColumns().size());
        out.println("    # child tables: " + table.getRelatedTables(TableRelationshipType.child));
        out.println("    # parent tables: " + table.getRelatedTables(TableRelationshipType.parent));
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void routineParameters(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
      final Routine[] routines = catalog.getRoutines(schema).toArray(new Routine[0]);
      assertThat("Routine count does not match", routines, arrayWithSize(4));
      for (final Routine routine : routines) {
        assertThat(routine, notNullValue());
        out.println("routine: " + routine.getName());
        final List<RoutineParameter<? extends Routine>> parameters = routine.getParameters();
        for (final RoutineParameter<? extends Routine> parameter : parameters) {
          out.println("  parameter: " + parameter.getName());
          out.println(String.format("  - %s=%s", "data-type", parameter.getColumnDataType()));
          out.println(String.format("  - %s=%s", "size", parameter.getSize()));
          out.println(String.format("  - %s=%s", "decimal digits", parameter.getDecimalDigits()));
          out.println(String.format("  - %s=%s", "width", parameter.getWidth()));
          out.println(String.format("  - %s=%s", "nullable", parameter.isNullable()));
          out.println(
              String.format("  - %s=%s", "ordinal position", parameter.getOrdinalPosition()));
          out.println(String.format("  - %s=%s", "remarks", parameter.getRemarks()));

          out.println(String.format("  - %s=%s", "attibutes", ""));
        }
      }
      out.println();
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void routines(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
      final Routine[] routines = catalog.getRoutines(schema).toArray(new Routine[0]);
      assertThat("Routine count does not match", routines, arrayWithSize(4));
      for (final Routine routine : routines) {
        assertThat(routine, notNullValue());
        out.println("routine: " + routine.getName());
        out.println("  specific name: " + routine.getSpecificName());
        out.println("  return type: " + routine.getReturnType());
        out.println("  body type: " + routine.getRoutineBodyType());
        out.println("  definition:\n" + routine.getDefinition());
        out.println();
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void schemaEquals() {

    final Schema schema1 = new SchemaReference("PUBLIC", "BOOKS");
    assertThat("Could not find any tables", catalog.getTables(schema1), not(empty()));
    assertThat("Wrong number of routines", catalog.getRoutines(schema1), hasSize(4));

    final Schema schema2 = new SchemaReference("PUBLIC", "BOOKS");

    assertThat("Schema not not match", schema1, equalTo(schema2));
    assertThat(
        "Tables do not match", catalog.getTables(schema1), equalTo(catalog.getTables(schema2)));
    assertThat(
        "Routines do not match",
        catalog.getRoutines(schema1),
        equalTo(catalog.getRoutines(schema2)));

    // Try negative test
    final Table table1 = catalog.getTables(schema1).toArray(new Table[0])[0];
    final Table table2 = catalog.getTables(schema1).toArray(new Table[0])[1];
    assertThat("Tables should not be equal", table1, not(equalTo(table2)));
  }

  @Test
  public void sequences(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
      assertThat("BOOKS Schema not found", schema, notNullValue());
      final Sequence[] sequences = catalog.getSequences(schema).toArray(new Sequence[0]);
      assertThat("Sequence count does not match", sequences, arrayWithSize(1));
      for (final Sequence sequence : sequences) {
        assertThat(sequence, notNullValue());
        out.println("sequence: " + sequence.getName());
        out.println("  increment: " + sequence.getIncrement());
        out.println("  start value: " + sequence.getStartValue());
        out.println("  minimum value: " + sequence.getMinimumValue());
        out.println("  maximum value: " + sequence.getMaximumValue());
        out.println("  cycle?: " + sequence.isCycle());
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void synonyms(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
      assertThat("BOOKS Schema not found", schema, notNullValue());
      final Synonym[] synonyms = catalog.getSynonyms(schema).toArray(new Synonym[0]);
      assertThat("Synonym count does not match", synonyms, arrayWithSize(1));
      for (final Synonym synonym : synonyms) {
        assertThat(synonym, notNullValue());
        out.println("synonym: " + synonym.getName());
        out.println("  class: " + synonym.getReferencedObject().getClass().getSimpleName());
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void tableColumns(final TestContext testContext) throws Exception {
    verifyRetrieveTableColumns(catalog);
  }

  @Test
  public void tableConstraints(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        for (final Table table : tables) {
          out.println("  table: " + table.getFullName());
          final TableConstraint[] tableConstraints =
              table.getTableConstraints().toArray(new TableConstraint[0]);
          for (final TableConstraint tableConstraint : tableConstraints) {
            out.println("    constraint: " + tableConstraint.getName());
            out.println("      type: " + tableConstraint.getType());
            if (tableConstraint instanceof TableConstraint) {
              final TableConstraint dependentTableConstraint = tableConstraint;
              final List<TableConstraintColumn> columns =
                  dependentTableConstraint.getConstrainedColumns();
              for (final TableConstraintColumn tableConstraintColumn : columns) {
                out.println("      on column: " + tableConstraintColumn.getName());
              }
            }
          }
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void tablePrivileges(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      final Table table = catalog.lookupTable(schemas[0], "AUTHORS").get();

      out.println(table.getFullName());
      final Collection<Privilege<Table>> privileges = table.getPrivileges();
      for (final Privilege<Table> privilege : privileges) {
        out.println(String.format("  privilege: %s", privilege.getName()));
        final Collection<Grant<Table>> grants = privilege.getGrants();
        for (final Grant<Table> grant : grants) {
          if (!privilege.getName().equals("SELECT")) {
            assertThat(grant.getGrantor(), is("_SYSTEM"));
            assertThat(grant.getGrantee(), is("SA"));
            assertThat(grant.isGrantable(), is(true));
          }
          out.println("    " + grant);
        }
      }
    }

    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void tables(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          out.println(String.format("%s [%s]", table.getFullName(), table.getTableType()));

          final SortedMap<String, Object> tableAttributes = new TreeMap<>(table.getAttributes());
          for (final Entry<String, Object> tableAttribute : tableAttributes.entrySet()) {
            out.println(
                String.format("  ~ %s=%s", tableAttribute.getKey(), tableAttribute.getValue()));
          }
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void tablesSort() {

    final String[] tableNames = {
      "AUTHORS",
      "BOOKS",
      "Celebrities",
      "COUPONS",
      "CUSTOMERDATA",
      "CUSTOMERS",
      "PUBLISHERS",
      "BOOKAUTHORS",
      "Celebrity Updates",
      "ΒΙΒΛΊΑ",
      "AUTHORSLIST"
    };
    final Random rnd = new Random();

    final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
    assertThat("Schema count does not match", schemas, arrayWithSize(5));
    final Schema schema = schemas[0];

    final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
    for (int i = 0; i < 10; i++) {
      for (int tableIdx = 0; tableIdx < tables.length; tableIdx++) {
        final Table table = tables[tableIdx];
        assertThat(
            "Table name does not match in iteration " + i,
            tableNames[tableIdx],
            is(table.getName()));
      }

      // Shuffle array, and sort it again
      for (int k = tables.length; k > 1; k--) {
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
  public void triggers(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
      final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
      for (final Table table : tables) {
        for (final Trigger trigger : table.getTriggers()) {
          out.println(String.format("  trigger: %s", trigger.getFullName()));
          out.println(String.format("    action condition: %s", trigger.getActionCondition()));
          out.println(String.format("    condition timing: %s", trigger.getConditionTiming()));
          out.println(String.format("    action order: %s", trigger.getActionOrder()));
          out.println(String.format("    action orientation: %s", trigger.getActionOrientation()));
          out.println(String.format("    action statement: %s", trigger.getActionStatement()));
          out.println(
              String.format("    event manipulation type: %s", trigger.getEventManipulationType()));
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void views(final TestContext testContext) throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          if (!(table instanceof View)) {
            continue;
          }
          final View view = (View) table;
          out.println(String.format("%s [%s]", view.getFullName(), view.getTableType()));
          out.println(String.format("  - check option: %s", view.getCheckOption()));
          out.println(String.format("  - updatable?: %b", view.isUpdatable()));
          out.println(String.format("  - definition: %s", view.getDefinition()));
          out.println("  - table usage");
          for (final Table usedTable : view.getTableUsage()) {
            out.println(String.format("    - table: %s", usedTable));
          }
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  /** Keep in sync with {@link WeakAssociationsAttributesTest#weakAssociations() LabelName} */
  @Test
  public void weakAssociations(final TestContext testContext) throws Exception {

    final Column pkColumn =
        catalog
            .lookupTable(new SchemaReference("PUBLIC", "BOOKS"), "AUTHORS")
            .get()
            .lookupColumn("ID")
            .get();
    final Column fkColumn =
        catalog
            .lookupTable(new SchemaReference("PUBLIC", "BOOKS"), "BOOKS")
            .get()
            .lookupColumn("ID")
            .get();

    final WeakAssociationBuilder builder = WeakAssociationBuilder.builder(catalog);
    // 1. Happy path - good weak association
    builder.clear();
    builder.addColumnReference(
        new WeakAssociationColumn(fkColumn), new WeakAssociationColumn(pkColumn));
    builder.findOrCreate("1_weak");
    // 2. Partial foreign key
    builder.clear();
    builder.addColumnReference(
        new WeakAssociationColumn(
            new SchemaReference("PRIVATE", "LIBRARY"), "BOOKAUTHORS", "AUTHORID"),
        new WeakAssociationColumn(pkColumn));
    builder.findOrCreate("2_weak_partial_fk");
    // 3. Partial primary key
    builder.clear();
    builder.addColumnReference(
        new WeakAssociationColumn(fkColumn),
        new WeakAssociationColumn(new SchemaReference("PRIVATE", "LIBRARY"), "BOOKS", "ID"));
    builder.findOrCreate("3_weak_partial_pk");
    // 4. Partial both (not built)
    builder.clear();
    builder.addColumnReference(
        new WeakAssociationColumn(
            new SchemaReference("PRIVATE", "LIBRARY"), "BOOKAUTHORS", "AUTHORID"),
        new WeakAssociationColumn(new SchemaReference("PRIVATE", "LIBRARY"), "AUTHORS", "ID"));
    builder.findOrCreate("4_weak_partial_both");
    // 5. No column references (not built)
    builder.clear();
    builder.findOrCreate("5_weak_no_references");
    // 6. Multiple tables in play (not built)
    builder.clear();
    builder.addColumnReference(
        new WeakAssociationColumn(
            new SchemaReference("PRIVATE", "LIBRARY"), "BOOKAUTHORS", "AUTHORID"),
        new WeakAssociationColumn(pkColumn));
    builder.addColumnReference(
        new WeakAssociationColumn(fkColumn),
        new WeakAssociationColumn(new SchemaReference("PRIVATE", "LIBRARY"), "AUTHORS", "ID"));
    builder.findOrCreate("6_weak_conflicting");
    // 7. Duplicate column references (only one column reference built)
    builder.clear();
    builder.addColumnReference(
        new WeakAssociationColumn(
            new SchemaReference("PRIVATE", "LIBRARY"), "MAGAZINEARTICLES", "AUTHORID"),
        new WeakAssociationColumn(pkColumn));
    builder.addColumnReference(
        new WeakAssociationColumn(
            new SchemaReference("PRIVATE", "LIBRARY"), "MAGAZINEARTICLES", "AUTHORID"),
        new WeakAssociationColumn(pkColumn));
    builder.findOrCreate("7_weak_duplicate");
    // 8. Two column references
    builder.clear();
    builder.addColumnReference(
        new WeakAssociationColumn(
            new SchemaReference("PRIVATE", "ALLSALES"), "REGIONS", "POSTALCODE"),
        new WeakAssociationColumn(
            new SchemaReference("PUBLIC", "PUBLISHER SALES"), "SALES", "POSTALCODE"));
    builder.addColumnReference(
        new WeakAssociationColumn(new SchemaReference("PRIVATE", "ALLSALES"), "REGIONS", "COUNTRY"),
        new WeakAssociationColumn(
            new SchemaReference("PUBLIC", "PUBLISHER SALES"), "SALES", "COUNTRY"));
    builder.findOrCreate("8_weak_two_references");
    // 9. Self-reference
    builder.clear();
    builder.addColumnReference(
        new WeakAssociationColumn(
            new SchemaReference("PUBLIC", "BOOKS"), "BOOKS", "OTHEREDITIONID"),
        new WeakAssociationColumn(new SchemaReference("PUBLIC", "BOOKS"), "BOOKS", "ID"));
    builder.findOrCreate("9_weak_self_reference");
    // 10. Self-reference in partial table (not built)
    builder.clear();
    builder.addColumnReference(
        new WeakAssociationColumn(
            new SchemaReference("PRIVATE", "LIBRARY"), "BOOKS", "PREVIOUSEDITIONID"),
        new WeakAssociationColumn(new SchemaReference("PRIVATE", "LIBRARY"), "BOOKS", "ID"));
    builder.findOrCreate("10_weak_partial_self_reference");
    // 11. Duplicate weak association (not built)
    builder.clear();
    builder.addColumnReference(
        new WeakAssociationColumn(fkColumn), new WeakAssociationColumn(pkColumn));
    builder.findOrCreate("1_weak_duplicate");
    // 12. Same as foreign key
    builder.clear();
    builder.addColumnReference(
        new WeakAssociationColumn(
            new SchemaReference("PUBLIC", "BOOKS"), "BOOKAUTHORS", "AUTHORID"),
        new WeakAssociationColumn(pkColumn));
    final Optional<TableReference> optionalTableRef = builder.findOrCreate("12_same_as_fk");
    assertThat(optionalTableRef, isPresent());
    assertThat(optionalTableRef.get(), instanceOf(ForeignKey.class));
    assertThat(optionalTableRef.get().getName(), is("Z_FK_AUTHOR"));

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas) {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        for (final Table table : tables) {
          out.println("  table: " + table.getFullName());
          for (final WeakAssociation foreignKey : table.getWeakAssociations()) {
            out.println("    weak association: " + foreignKey.getName());
            out.println("      column references: ");
            final List<ColumnReference> columnReferences = foreignKey.getColumnReferences();
            for (int i = 0; i < columnReferences.size(); i++) {
              final ColumnReference columnReference = columnReferences.get(i);
              out.println("        key sequence: " + (i + 1));
              out.println("          " + columnReference);
            }
          }
        }
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
