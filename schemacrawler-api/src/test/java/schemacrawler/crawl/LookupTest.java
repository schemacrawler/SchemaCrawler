/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;

import java.sql.Connection;
import java.util.Collection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DataTypeType;
import schemacrawler.schema.Function;
import schemacrawler.schema.FunctionParameter;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureParameter;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableTypes;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.WithTestDatabase;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LookupTest {

  private static final String NOTHING_AT_ALL = "NOTHING_AT_ALL";
  private Catalog catalog;

  @BeforeAll
  public void loadCatalog(final Connection connection) throws Exception {

    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseTestUtility.newSchemaRetrievalOptions();

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
  public void lookupCatalog() throws Exception {
    final SchemaReference schema = new SchemaReference("PUBLIC", "BOOKS");

    assertThat(catalog.lookupSchema(null), isEmpty());
    assertThat(catalog.lookupSchema(NOTHING_AT_ALL), isEmpty());
    assertThat(catalog.lookupSchema("PUBLIC.BOOKS"), isPresentAndIs(schema));

    final Sequence sequence = new MutableSequence(schema, "PUBLISHER_ID_SEQ");
    assertThat(catalog.lookupSequence(schema, null), isEmpty());
    assertThat(catalog.lookupSequence(schema, NOTHING_AT_ALL), isEmpty());
    assertThat(catalog.lookupSequence(schema, "PUBLISHER_ID_SEQ"), isPresentAndIs(sequence));

    final Synonym synonym = new MutableSynonym(schema, "PUBLICATIONS");
    assertThat(catalog.lookupSynonym(schema, null), isEmpty());
    assertThat(catalog.lookupSynonym(schema, NOTHING_AT_ALL), isEmpty());
    assertThat(catalog.lookupSynonym(schema, "PUBLICATIONS"), isPresentAndIs(synonym));

    final ColumnDataType systemColumnDataType =
        new MutableColumnDataType(new SchemaReference(), "VARCHAR", DataTypeType.system);
    assertThat(catalog.lookupSystemColumnDataType(null), isEmpty());
    assertThat(catalog.lookupSystemColumnDataType(NOTHING_AT_ALL), isEmpty());
    assertThat(catalog.lookupSystemColumnDataType("VARCHAR"), isPresentAndIs(systemColumnDataType));

    final Table table = new TablePartial(schema, "AUTHORS");
    assertThat(catalog.lookupTable(schema, null), isEmpty());
    assertThat(catalog.lookupTable(schema, NOTHING_AT_ALL), isEmpty());
    assertThat(catalog.lookupTable(schema, "AUTHORS"), isPresentAndIs(table));
  }

  @Test
  public void lookupColumn() throws Exception {
    final SchemaReference schema = new SchemaReference("PUBLIC", "BOOKS");

    final Table table = catalog.lookupTable(schema, "AUTHORS").get();
    final Column column = table.lookupColumn("FIRSTNAME").get();

    assertThat(column.lookupAttribute(null), isEmpty());
    assertThat(column.lookupAttribute(NOTHING_AT_ALL), isEmpty());
    assertThat(column.lookupAttribute("CHAR_OCTET_LENGTH"), isPresentAndIs(20));

    final Privilege<Column> privilege = new MutablePrivilege<>(new ColumnPointer(column), "DELETE");
    assertThat(column.lookupPrivilege(null), isEmpty());
    assertThat(column.lookupPrivilege(NOTHING_AT_ALL), isEmpty());
    assertThat(column.lookupPrivilege("DELETE"), isPresentAndIs(privilege));
  }

  @Test
  public void lookupFunction() throws Exception {
    final SchemaReference schema = new SchemaReference("PUBLIC", "BOOKS");

    final Collection<Routine> functions = catalog.getRoutines(schema, "CUSTOMADD");
    assertThat(functions, hasSize(2));
    final Function function = (Function) functions.iterator().next();

    assertThat(function.lookupAttribute(null), isEmpty());
    assertThat(function.lookupAttribute(NOTHING_AT_ALL), isEmpty());
    assertThat(function.lookupAttribute("DECLARED_NUMERIC_SCALE"), isPresentAndIs(0L));

    assertThat(function.lookupParameter(null), isEmpty());
    assertThat(function.lookupParameter(NOTHING_AT_ALL), isEmpty());
    assertThat(function.lookupParameter("ONE"), isPresent());
    final FunctionParameter parameter = function.lookupParameter("ONE").get();
    assertThat(parameter.getName(), is("ONE"));
  }

  @Test
  public void lookupIndex() throws Exception {
    final SchemaReference schema = new SchemaReference("PUBLIC", "BOOKS");

    final Table table = catalog.lookupTable(schema, "AUTHORS").get();
    final Index index = table.lookupIndex("IDX_B_AUTHORS").get();

    assertThat(index.lookupAttribute(null), isEmpty());
    assertThat(index.lookupAttribute(NOTHING_AT_ALL), isEmpty());
    assertThat(index.lookupAttribute("INDEX_QUALIFIER"), isPresentAndIs("PUBLIC"));

    final IndexColumn column = new MutableIndexColumn(index, new ColumnPartial(table, "FIRSTNAME"));
    assertThat(index.lookupColumn(null), isEmpty());
    assertThat(index.lookupColumn(NOTHING_AT_ALL), isEmpty());
    assertThat(index.lookupColumn("FIRSTNAME"), isPresentAndIs(column));
  }

  @Test
  public void lookupProcedure() throws Exception {
    final SchemaReference schema = new SchemaReference("PUBLIC", "BOOKS");

    final Collection<Routine> procedures = catalog.getRoutines(schema, "NEW_PUBLISHER");
    assertThat(procedures, hasSize(2));
    final Procedure procedure = (Procedure) procedures.iterator().next();

    assertThat(procedure.lookupAttribute(null), isEmpty());
    assertThat(procedure.lookupAttribute(NOTHING_AT_ALL), isEmpty());
    assertThat(procedure.lookupAttribute("SCHEMA_LEVEL_ROUTINE"), isPresentAndIs("YES"));

    assertThat(procedure.lookupParameter(null), isEmpty());
    assertThat(procedure.lookupParameter(NOTHING_AT_ALL), isEmpty());
    assertThat(procedure.lookupParameter("PUBLISHER"), isPresent());
    final ProcedureParameter parameter = procedure.lookupParameter("PUBLISHER").get();
    assertThat(parameter.getName(), is("PUBLISHER"));
  }

  @Test
  public void lookupTable() throws Exception {
    final SchemaReference schema = new SchemaReference("PUBLIC", "BOOKS");

    final Table table = catalog.lookupTable(schema, "AUTHORS").get();

    assertThat(table.lookupAttribute(null), isEmpty());
    assertThat(table.lookupAttribute(NOTHING_AT_ALL), isEmpty());
    assertThat(
        table.lookupAttribute("REMARKS"), isPresentAndIs("Contact details for book authors"));

    final Column column = new ColumnPartial(table, "FIRSTNAME");
    assertThat(table.lookupColumn(null), isEmpty());
    assertThat(table.lookupColumn(NOTHING_AT_ALL), isEmpty());
    assertThat(table.lookupColumn("FIRSTNAME"), isPresentAndIs(column));

    final Index index = new MutableIndex(table, "IDX_B_AUTHORS");
    assertThat(table.lookupIndex(null), isEmpty());
    assertThat(table.lookupIndex(NOTHING_AT_ALL), isEmpty());
    assertThat(table.lookupIndex("IDX_B_AUTHORS"), isPresentAndIs(index));

    final Privilege<Table> privilege = new MutablePrivilege<>(new TablePointer(table), "DELETE");
    assertThat(table.lookupPrivilege(null), isEmpty());
    assertThat(table.lookupPrivilege(NOTHING_AT_ALL), isEmpty());
    assertThat(table.lookupPrivilege("DELETE"), isPresentAndIs(privilege));

    final Trigger trigger = new MutableTrigger(table, "TRG_AUTHORS");
    assertThat(table.lookupTrigger(null), isEmpty());
    assertThat(table.lookupTrigger(NOTHING_AT_ALL), isEmpty());
    assertThat(table.lookupTrigger("TRG_AUTHORS"), isPresentAndIs(trigger));

    assertThat(table.lookupForeignKey(null), isEmpty());
    assertThat(table.lookupForeignKey(NOTHING_AT_ALL), isEmpty());
    assertThat(
        catalog.lookupTable(schema, "BOOKAUTHORS").get().lookupForeignKey("Z_FK_AUTHOR"),
        isPresent());

    assertThat(table.lookupTableConstraint(null), isEmpty());
    assertThat(table.lookupTableConstraint(NOTHING_AT_ALL), isEmpty());
    assertThat(
        catalog.lookupTable(schema, "BOOKAUTHORS").get().lookupTableConstraint("Z_FK_AUTHOR"),
        isPresent());
  }

  @Test
  public void lookupTableTypes(final Connection connection) throws Exception {

    final TableTypes tableTypes = TableTypes.from(connection);

    assertThat(tableTypes.lookupTableType(null), isEmpty());
    assertThat(tableTypes.lookupTableType(NOTHING_AT_ALL), isEmpty());
    assertThat(tableTypes.lookupTableType("VIEW"), isPresent());
  }
}
