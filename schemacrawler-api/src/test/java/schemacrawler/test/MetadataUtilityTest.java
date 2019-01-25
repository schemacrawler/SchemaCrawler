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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.utility.MetaDataUtility;
import schemacrawler.utility.MetaDataUtility.ForeignKeyCardinality;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class MetadataUtilityTest
{

  @Test
  public void fkUtilities(final TestContext testContext,
                          final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat("BOOKS Schema not found", schema, notNullValue());

    final Table table = catalog.lookupTable(schema, "BOOKS").get();
    assertThat("BOOKS Table not found", table, notNullValue());

    final ForeignKey fk = table.getForeignKeys().toArray(new ForeignKey[0])[0];
    assertThat("Foreign key not found", fk, notNullValue());

    final ColumnReference columnReference = fk.getColumnReferences()
      .toArray(new ColumnReference[0])[0];
    assertThat("Column reference not found", columnReference, notNullValue());

    assertThat(MetaDataUtility
      .constructForeignKeyName(columnReference.getForeignKeyColumn(),
                               columnReference.getPrimaryKeyColumn()),
               is("SC_AA4376_AFD2BA21"));

    assertThat(MetaDataUtility.findForeignKeyCardinality(fk),
               is(ForeignKeyCardinality.zero_many));

    assertThat(MetaDataUtility.foreignKeyColumnNames(fk),
               containsInAnyOrder("PUBLIC.BOOKS.BOOKAUTHORS.BOOKID"));
  }

  @Test
  public void tableUtilities(final TestContext testContext,
                             final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").get();
    assertThat("BOOKS Schema not found", schema, notNullValue());

    final Table table = catalog.lookupTable(schema, "BOOKS").get();
    assertThat("BOOKS Table not found", table, notNullValue());

    assertThat(MetaDataUtility.allIndexCoumnNames(table).stream()
      .flatMap(List::stream).collect(Collectors.toSet()),
               containsInAnyOrder("PUBLIC.BOOKS.BOOKS.ID",
                                  "PUBLIC.BOOKS.BOOKS.PREVIOUSEDITIONID"));

    assertThat(MetaDataUtility.uniqueIndexCoumnNames(table).stream()
      .flatMap(List::stream).collect(Collectors.toSet()),
               containsInAnyOrder("PUBLIC.BOOKS.BOOKS.ID",
                                  "PUBLIC.BOOKS.BOOKS.PREVIOUSEDITIONID"));

    final Index index = table.getIndexes().toArray(new Index[0])[0];
    assertThat("Index not found", index, notNullValue());

    assertThat(MetaDataUtility.columnNames(index),
               containsInAnyOrder("PUBLIC.BOOKS.BOOKS.ID"));

    assertThat(MetaDataUtility.containsGeneratedColumns(index), is(false));
  }

}
