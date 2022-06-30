/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.crawl;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;

import java.sql.Connection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DataTypeType;
import schemacrawler.schema.Index;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.WithTestDatabase;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LookupTest {

  private static final String NOTHING_AT_ALL = "NOTHING_AT_ALL";
  private Catalog catalog;

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
}
