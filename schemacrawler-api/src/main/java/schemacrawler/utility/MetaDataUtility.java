/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.utility;

import static schemacrawler.filter.ReducerFactory.getRoutineReducer;
import static schemacrawler.filter.ReducerFactory.getSchemaReducer;
import static schemacrawler.filter.ReducerFactory.getSequenceReducer;
import static schemacrawler.filter.ReducerFactory.getSynonymReducer;
import static schemacrawler.filter.ReducerFactory.getTableReducer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.JavaSqlTypeGroup;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class MetaDataUtility {

  public enum ForeignKeyCardinality {
    unknown(""),
    zero_one("(0..1)"),
    zero_many("(0..many)"),
    one_one("(1..1)");

    private final String description;

    ForeignKeyCardinality(final String description) {
      this.description = requireNonNull(description, "No description provided");
    }

    @Override
    public String toString() {
      return description;
    }
  }

  public static Collection<List<String>> allIndexCoumnNames(final Table table) {
    return indexCoumnNames(table, false);
  }

  public static List<String> columnNames(final Index index) {
    if (index == null) {
      return Collections.emptyList();
    }

    final List<String> columnNames = new ArrayList<>();
    for (final Column indexColumn : index) {
      columnNames.add(indexColumn.getFullName());
    }
    return columnNames;
  }

  public static ForeignKeyCardinality findForeignKeyCardinality(final TableReference tableRef) {
    if (tableRef == null) {
      return ForeignKeyCardinality.unknown;
    }
    final boolean isForeignKeyUnique = isForeignKeyUnique(tableRef);
    final boolean isColumnReference = tableRef.getDependentTable() instanceof PartialDatabaseObject;

    final ForeignKeyCardinality connectivity;
    if (isColumnReference) {
      connectivity = ForeignKeyCardinality.unknown;
    } else if (isForeignKeyUnique) {
      connectivity = ForeignKeyCardinality.zero_one;
    } else {
      connectivity = ForeignKeyCardinality.zero_many;
    }
    return connectivity;
  }

  public static List<String> foreignKeyColumnNames(final TableReference tableRef) {
    if (tableRef == null) {
      return Collections.emptyList();
    }
    final List<String> columnNames = new ArrayList<>();
    for (final ColumnReference columnReference : tableRef) {
      columnNames.add(columnReference.getForeignKeyColumn().getFullName());
    }
    return columnNames;
  }

  /**
   * Gets a comma-separated list of columns for an index.
   *
   * @param index Index
   * @param quotingStrategy Identifier quoting strategy
   * @param quoteString
   * @return Comma-separated list of columns
   */
  public static String getColumnsListAsString(final Index index, final Identifiers identifiers) {

    requireNonNull(index, "No index provided");
    requireNonNull(identifiers, "No identifier quoting strategy provided");

    final List<IndexColumn> columns = index.getColumns();
    return joinColumns(columns, false, identifiers);
  }

  /**
   * Gets a comma-separated list of columns for a table.
   *
   * @param table Table
   * @param quotingStrategy Identifier quoting strategy
   * @param quoteString
   * @return Comma-separated list of columns
   */
  public static String getColumnsListAsString(final Table table, final Identifiers identifiers) {

    requireNonNull(table, "No table provided");
    requireNonNull(identifiers, "No identifier quoting strategy provided");

    final List<Column> columns = table.getColumns();
    return joinColumns(columns, false, identifiers);
  }

  /**
   * Gets a comma-separated list of columns for an index.
   *
   * @param tableConstraint Table constraint
   * @param quotingStrategy Identifier quoting strategy
   * @param quoteString
   * @return Comma-separated list of columns
   */
  public static String getColumnsListAsString(
      final TableConstraint tableConstraint, final Identifiers identifiers) {

    requireNonNull(tableConstraint, "No table constraint provided");
    requireNonNull(identifiers, "No identifier quoting strategy provided");

    final List<TableConstraintColumn> columns = tableConstraint.getConstrainedColumns();
    return joinColumns(columns, false, identifiers);
  }

  /**
   * Gets a comma-separated list of columns for a foreign key.
   *
   * @param fk Foreign key
   * @param quotingStrategy Identifier quoting strategy
   * @param quoteString
   * @return Comma-separated list of columns
   */
  public static String getColumnsListAsString(
      final TableReference fk,
      final TableRelationshipType relationshipType,
      final Identifiers identifiers) {

    requireNonNull(fk, "No foreign key provided");
    requireNonNull(identifiers, "No identifier quoting strategy provided");
    if (relationshipType == null || relationshipType == TableRelationshipType.none) {
      return "";
    }

    final List<Column> columns = new ArrayList<>();
    for (final ColumnReference columnReference : fk.getColumnReferences()) {
      switch (relationshipType) {
        case parent:
          columns.add(columnReference.getPrimaryKeyColumn());
          break;
        case child:
          columns.add(columnReference.getForeignKeyColumn());
          break;
        default:
      }
    }
    return joinColumns(columns, false, identifiers);
  }

  public static boolean isForeignKeyUnique(final TableReference tableRef) {
    if (tableRef == null) {
      return false;
    }
    final Table fkTable = tableRef.getForeignKeyTable();
    final Collection<List<String>> uniqueIndexCoumnNames = uniqueIndexCoumnNames(fkTable);
    final List<String> foreignKeyColumnNames = foreignKeyColumnNames(tableRef);
    return uniqueIndexCoumnNames.contains(foreignKeyColumnNames);
  }

  public static String joinColumns(
      final List<? extends Column> columns,
      final boolean omitLargeObjectColumns,
      final Identifiers identifiers) {

    requireNonNull(columns, "No columns provided");
    requireNonNull(identifiers, "No identifiers provided");

    final List<String> columnsList = new ArrayList<>();
    for (int i = 0; i < columns.size(); i++) {
      final Column column = columns.get(i);
      if (!column.isColumnDataTypeKnown()) {
        continue;
      }
      final JavaSqlTypeGroup javaSqlTypeGroup =
          column.getColumnDataType().getJavaSqlType().getJavaSqlTypeGroup();
      if ((!omitLargeObjectColumns
          || ((javaSqlTypeGroup != JavaSqlTypeGroup.large_object)
              && (javaSqlTypeGroup != JavaSqlTypeGroup.object)))) {
        columnsList.add(identifiers.quoteName(column.getName()));
      }
    }
    return String.join(", ", columnsList);
  }

  public static void reduceCatalog(
      final Catalog catalog, final SchemaCrawlerOptions schemaCrawlerOptions) {
    requireNonNull(catalog, "No catalog provided");
    requireNonNull(schemaCrawlerOptions, "No SchemaCrawler options provided");

    catalog.undo(Schema.class, getSchemaReducer(schemaCrawlerOptions));
    catalog.reduce(Schema.class, getSchemaReducer(schemaCrawlerOptions));

    catalog.undo(Table.class, getTableReducer(schemaCrawlerOptions));
    catalog.reduce(Table.class, getTableReducer(schemaCrawlerOptions));

    catalog.undo(Routine.class, getRoutineReducer(schemaCrawlerOptions));
    catalog.reduce(Routine.class, getRoutineReducer(schemaCrawlerOptions));

    catalog.undo(Synonym.class, getSynonymReducer(schemaCrawlerOptions));
    catalog.reduce(Synonym.class, getSynonymReducer(schemaCrawlerOptions));

    catalog.undo(Sequence.class, getSequenceReducer(schemaCrawlerOptions));
    catalog.reduce(Sequence.class, getSequenceReducer(schemaCrawlerOptions));
  }

  public static String summarizeCatalog(final Catalog catalog) {
    final CrawlInfo crawlInfo = catalog.getCrawlInfo();

    final Map<String, Integer> countsMap = new HashMap<>();
    countsMap.put("column-data-types", catalog.getColumnDataTypes().size());
    countsMap.put("schemas", catalog.getSchemas().size());
    countsMap.put("tables", catalog.getTables().size());
    countsMap.put("routines", catalog.getRoutines().size());
    countsMap.put("sequences", catalog.getSequences().size());
    countsMap.put("synonyms", catalog.getSynonyms().size());

    return String.format("Loaded catalog%n%s%nCounts:%n%s", crawlInfo, Objects.toString(countsMap));
  }

  public static Collection<List<String>> uniqueIndexCoumnNames(final Table table) {
    return indexCoumnNames(table, true);
  }

  private static Collection<List<String>> indexCoumnNames(
      final Table table, final boolean includeUniqueOnly) {
    final List<List<String>> allIndexCoumns = new ArrayList<>();
    if (table instanceof PartialDatabaseObject) {
      return allIndexCoumns;
    }

    for (final Index index : table.getIndexes()) {
      if (includeUniqueOnly && !index.isUnique()) {
        continue;
      }

      final List<String> indexColumns = columnNames(index);
      allIndexCoumns.add(indexColumns);
    }
    return allIndexCoumns;
  }

  private MetaDataUtility() {
    // Prevent instantiation
  }
}
