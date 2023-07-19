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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.schemacrawler.IdentifierQuotingStrategy.quote_all;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.utility.MetaDataUtility;
import schemacrawler.utility.MetaDataUtility.ForeignKeyCardinality;

@WithTestDatabase
@TestInstance(Lifecycle.PER_CLASS)
public class MetadataUtilityTest {

  private static final Identifiers identifiers =
      IdentifiersBuilder.builder()
          .withIdentifierQuotingStrategy(quote_all)
          .withIdentifierQuoteString("'")
          .toOptions();
  private Catalog catalog;

  @Test
  public void columnsListAsStringConstraint() throws Exception {

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat("BOOKS Schema not found", schema, notNullValue());

    final Table table = catalog.lookupTable(schema, "BOOKS").get();
    assertThat("BOOKS Table not found", table, notNullValue());

    final PrimaryKey pk = table.getPrimaryKey();
    assertThat("Index not found", pk, notNullValue());

    final String columnsListAsStringChild = MetaDataUtility.getColumnsListAsString(pk, identifiers);
    assertThat(columnsListAsStringChild, is("'ID'"));
  }

  @Test
  public void columnsListAsStringFk() throws Exception {

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat("BOOKS Schema not found", schema, notNullValue());

    final Table table = catalog.lookupTable(schema, "BOOKS").get();
    assertThat("BOOKS Table not found", table, notNullValue());

    final ForeignKey fk = table.getForeignKeys().toArray(new ForeignKey[0])[0];
    assertThat("Foreign key not found", fk, notNullValue());

    final String columnsListAsStringChild =
        MetaDataUtility.getColumnsListAsString(fk, TableRelationshipType.child, identifiers);
    assertThat(columnsListAsStringChild, is("'PREVIOUSEDITIONID'"));

    final String columnsListAsStringParent =
        MetaDataUtility.getColumnsListAsString(fk, TableRelationshipType.parent, identifiers);
    assertThat(columnsListAsStringParent, is("'ID'"));

    final String columnsListAsStringNone =
        MetaDataUtility.getColumnsListAsString(fk, TableRelationshipType.none, identifiers);
    assertThat(columnsListAsStringNone, is(""));
  }

  @Test
  public void columnsListAsStringIndex() throws Exception {

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat("BOOKS Schema not found", schema, notNullValue());

    final Table table = catalog.lookupTable(schema, "BOOKS").get();
    assertThat("BOOKS Table not found", table, notNullValue());

    final Index index = table.getIndexes().toArray(new Index[0])[0];
    assertThat("Index not found", index, notNullValue());

    final String columnsListAsStringChild =
        MetaDataUtility.getColumnsListAsString(index, identifiers);
    assertThat(columnsListAsStringChild, is("'ID'"));
  }

  @Test
  public void columnsListAsStringTable() throws Exception {

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat("BOOKS Schema not found", schema, notNullValue());

    final Table table = catalog.lookupTable(schema, "BOOKS").get();
    assertThat("BOOKS Table not found", table, notNullValue());

    final String columnsListAsStringChild =
        MetaDataUtility.getColumnsListAsString(table, identifiers);
    assertThat(
        columnsListAsStringChild,
        is(
            "'ID', 'TITLE', 'DESCRIPTION', 'PUBLISHERID', 'PUBLICATIONDATE', 'PRICE', 'PREVIOUSEDITIONID'"));
  }

  @Test
  public void fkUtilities() throws Exception {

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat("BOOKS Schema not found", schema, notNullValue());

    final Table table = catalog.lookupTable(schema, "BOOKS").get();
    assertThat("BOOKS Table not found", table, notNullValue());

    final ForeignKey fk = table.getForeignKeys().toArray(new ForeignKey[0])[0];
    assertThat("Foreign key not found", fk, notNullValue());

    final ColumnReference columnReference =
        fk.getColumnReferences().toArray(new ColumnReference[0])[0];
    assertThat("Column reference not found", columnReference, notNullValue());

    assertThat(MetaDataUtility.findForeignKeyCardinality(fk), is(ForeignKeyCardinality.zero_one));

    assertThat(
        MetaDataUtility.foreignKeyColumnNames(fk),
        containsInAnyOrder("PUBLIC.BOOKS.BOOKS.PREVIOUSEDITIONID"));
  }

  @BeforeAll
  public void loadCatalog(final Connection connection) {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
    try {
      catalog = getCatalog(connection, schemaCrawlerOptions);
    } catch (final Exception e) {
      fail("Catalog not loaded", e);
    }
  }

  @Test
  public void reduceCatalog() throws Exception {

    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    limitOptionsBuilder.includeTables(tableName -> !tableName.matches(".*\\.BOOKS"));

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    // Reduce catalog
    MetaDataUtility.reduceCatalog(catalog, schemaCrawlerOptions);

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat("BOOKS Schema not found", schema, notNullValue());

    assertThat("BOOKS Table not found", !catalog.lookupTable(schema, "BOOKS").isPresent());

    // Undo reduce catalog
    MetaDataUtility.reduceCatalog(catalog, SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());

    final Table table = catalog.lookupTable(schema, "BOOKS").get();
    assertThat("BOOKS Table not found", table, notNullValue());
  }

  @Test
  public void tableUtilities() throws Exception {

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat("BOOKS Schema not found", schema, notNullValue());

    final Table table = catalog.lookupTable(schema, "BOOKS").get();
    assertThat("BOOKS Table not found", table, notNullValue());

    assertThat(
        MetaDataUtility.allIndexCoumnNames(table).stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet()),
        containsInAnyOrder("PUBLIC.BOOKS.BOOKS.ID", "PUBLIC.BOOKS.BOOKS.PREVIOUSEDITIONID"));

    assertThat(
        MetaDataUtility.uniqueIndexCoumnNames(table).stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet()),
        containsInAnyOrder("PUBLIC.BOOKS.BOOKS.ID", "PUBLIC.BOOKS.BOOKS.PREVIOUSEDITIONID"));

    final Index index = table.getIndexes().toArray(new Index[0])[0];
    assertThat("Index not found", index, notNullValue());

    assertThat(MetaDataUtility.columnNames(index), containsInAnyOrder("PUBLIC.BOOKS.BOOKS.ID"));
  }
}
