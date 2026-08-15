/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter.schemacrawler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.loader.utility.TableRowCountsUtility.TABLE_ROW_COUNT_KEY;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.scribe.model.TableTraits;
import schemacrawler.test.utility.crawl.LightColumn;
import schemacrawler.test.utility.crawl.LightColumnReference;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.test.utility.crawl.LightTrigger;

public class TableTraitsTest {

  private static Table bridgeCandidateTable(final boolean hasUniqueIndex) {
    final LightTable bridgeDelegate =
        new LightTable(new SchemaReference("PUBLIC", "BOOKS"), "BOOK_AUTHORS");
    final LightColumn bookId = bridgeDelegate.addColumn("BOOK_ID");
    final LightColumn authorId = bridgeDelegate.addColumn("AUTHOR_ID");

    final LightTable books = new LightTable(new SchemaReference("PUBLIC", "BOOKS"), "BOOKS");
    final LightColumn booksId = books.addColumn("ID");
    final LightTable authors = new LightTable(new SchemaReference("PUBLIC", "BOOKS"), "AUTHORS");
    final LightColumn authorsId = authors.addColumn("ID");

    final AtomicReference<Table> tableReference = new AtomicReference<>();
    final ForeignKey fkBooks = foreignKey("FK_BOOK", tableReference, books, bookId, booksId);
    final ForeignKey fkAuthors =
        foreignKey("FK_AUTHOR", tableReference, authors, authorId, authorsId);
    final List<ForeignKey> importedForeignKeys = List.of(fkBooks, fkAuthors);
    final List<Index> indexes = List.of(index(hasUniqueIndex, bookId, authorId));

    final Table table =
        (Table)
            Proxy.newProxyInstance(
                Table.class.getClassLoader(),
                new Class<?>[] {Table.class},
                (proxy, method, args) ->
                    tableMethodResult(proxy, method, args, importedForeignKeys, indexes));
    tableReference.set(table);

    return table;
  }

  private static Object defaultValue(final Method method) {
    if (!method.getReturnType().isPrimitive()) {
      return null;
    }
    if (method.getReturnType().equals(boolean.class)) {
      return false;
    }
    if (method.getReturnType().equals(int.class)) {
      return 0;
    }
    if (method.getReturnType().equals(long.class)) {
      return 0L;
    }
    if (method.getReturnType().equals(double.class)) {
      return 0D;
    }
    if (method.getReturnType().equals(float.class)) {
      return 0F;
    }
    if (method.getReturnType().equals(short.class)) {
      return (short) 0;
    }
    if (method.getReturnType().equals(byte.class)) {
      return (byte) 0;
    }
    if (method.getReturnType().equals(char.class)) {
      return '\0';
    }
    return null;
  }

  private static ForeignKey foreignKey(
      final String name,
      final AtomicReference<Table> tableReference,
      final Table primaryKeyTable,
      final Column foreignKeyColumn,
      final Column primaryKeyColumn) {
    final ColumnReference columnReference =
        new LightColumnReference(foreignKeyColumn, primaryKeyColumn);
    return (ForeignKey)
        Proxy.newProxyInstance(
            ForeignKey.class.getClassLoader(),
            new Class<?>[] {ForeignKey.class},
            (proxy, method, args) ->
                foreignKeyMethodResult(
                    proxy, method, args, name, tableReference, primaryKeyTable, columnReference));
  }

  private static Object foreignKeyMethodResult(
      final Object proxy,
      final Method method,
      final Object[] args,
      final String name,
      final AtomicReference<Table> tableReference,
      final Table primaryKeyTable,
      final ColumnReference columnReference) {
    return switch (method.getName()) {
      case "key" -> new NamedObjectKey(name);
      case "getForeignKeyTable", "getParent" -> tableReference.get();
      case "getPrimaryKeyTable" -> primaryKeyTable;
      case "getColumnReferences" -> List.of(columnReference);
      case "isSelfReferencing" -> false;
      case "equals" -> proxy == (args != null && args.length > 0 ? args[0] : null);
      case "hashCode" -> System.identityHashCode(proxy);
      case "toString" -> "ForeignKey[" + name + "]";
      default -> defaultValue(method);
    };
  }

  private static Index index(final boolean unique, final Column... columns) {
    final List<Column> indexColumns = List.of(columns);
    return (Index)
        Proxy.newProxyInstance(
            Index.class.getClassLoader(),
            new Class<?>[] {Index.class},
            (proxy, method, args) -> indexMethodResult(method, unique, indexColumns));
  }

  private static Object indexMethodResult(
      final Method method, final boolean unique, final List<Column> indexColumns) {
    return switch (method.getName()) {
      case "getColumns", "getConstrainedColumns" -> indexColumns;
      case "isUnique" -> unique;
      case "key" -> new NamedObjectKey("UIDX_BOOK_AUTHORS");
      case "toString" -> "Index[UIDX_BOOK_AUTHORS]";
      default -> defaultValue(method);
    };
  }

  private static Object tableMethodResult(
      final Object proxy,
      final Method method,
      final Object[] args,
      final Collection<ForeignKey> importedForeignKeys,
      final Collection<Index> indexes) {
    return switch (method.getName()) {
      case "getImportedForeignKeys" -> importedForeignKeys;
      case "hasForeignKeys" -> !importedForeignKeys.isEmpty();
      case "getIndexes" -> indexes;
      case "hasIndexes" -> !indexes.isEmpty();
      case "hasPrimaryKey" -> false;
      case "getPrimaryKey" -> null;
      case "isSelfReferencing", "hasTriggers" -> false;
      case "equals" -> proxy == (args != null && args.length > 0 ? args[0] : null);
      case "hashCode" -> System.identityHashCode(proxy);
      case "toString" -> "Table[BOOK_AUTHORS]";
      default -> defaultValue(method);
    };
  }

  @Test
  public void derivesAttributesFromTable() {
    final LightTable delegate = new LightTable(new SchemaReference("PUBLIC", "BOOKS"), "BOOKS");
    delegate.addTrigger(new LightTrigger(delegate, "TRG_BOOKS"));
    delegate.setAttribute(TABLE_ROW_COUNT_KEY, 0L);

    final Table table =
        (Table)
            Proxy.newProxyInstance(
                Table.class.getClassLoader(),
                new Class<?>[] {Table.class},
                (proxy, method, args) -> {
                  if ("isSelfReferencing".equals(method.getName())) {
                    return true;
                  }
                  try {
                    return method.invoke(delegate, args);
                  } catch (final InvocationTargetException e) {
                    throw e.getCause();
                  }
                });

    final TableTraits attributes = TableTraits.from(table);

    assertThat(attributes.noPrimaryKey(), is(Boolean.TRUE));
    assertThat(attributes.noForeignKeys(), is(Boolean.TRUE));
    assertThat(attributes.noIndexes(), is(Boolean.TRUE));
    assertThat(attributes.selfReferencing(), is(Boolean.TRUE));
    assertThat(attributes.hasTriggers(), is(Boolean.TRUE));
    assertThat(attributes.emptyTable(), is(Boolean.TRUE));
    assertThat(attributes.bridgeTable(), is(nullValue()));
  }

  @Test
  public void doesNotMarkBridgeTableWhenNotInferredAsBridge() {
    final Table table = bridgeCandidateTable(false);

    final TableTraits attributes = TableTraits.from(table);

    assertThat(attributes.bridgeTable(), is(nullValue()));
  }

  @Test
  public void doesNotMarkEmptyWhenRowCountIsNonZero() {
    final LightTable table = new LightTable(new SchemaReference("PUBLIC", "BOOKS"), "BOOKS");
    table.setAttribute(TABLE_ROW_COUNT_KEY, 7L);

    final TableTraits attributes = TableTraits.from(table);

    assertThat(attributes.emptyTable(), is(nullValue()));
  }

  @Test
  public void doesNotMarkEmptyWhenRowCountUnavailable() {
    final LightTable table = new LightTable(new SchemaReference("PUBLIC", "BOOKS"), "BOOKS");

    final TableTraits attributes = TableTraits.from(table);

    assertThat(attributes.emptyTable(), is(nullValue()));
  }

  @Test
  public void handlesNullTable() {
    final TableTraits attributes = TableTraits.from(null);

    assertThat(attributes.noPrimaryKey(), is(nullValue()));
    assertThat(attributes.noForeignKeys(), is(nullValue()));
    assertThat(attributes.noIndexes(), is(nullValue()));
    assertThat(attributes.selfReferencing(), is(nullValue()));
    assertThat(attributes.hasTriggers(), is(nullValue()));
    assertThat(attributes.emptyTable(), is(nullValue()));
    assertThat(attributes.bridgeTable(), is(nullValue()));
  }

  @Test
  public void marksBridgeTableWhenInferredAsBridge() {
    final Table table = bridgeCandidateTable(true);

    final TableTraits attributes = TableTraits.from(table);

    assertThat(attributes.bridgeTable(), is(Boolean.TRUE));
  }
}
