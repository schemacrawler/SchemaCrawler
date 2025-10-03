/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.utility;

import static java.util.Objects.requireNonNull;
import static schemacrawler.filter.ReducerFactory.getRoutineReducer;
import static schemacrawler.filter.ReducerFactory.getSchemaReducer;
import static schemacrawler.filter.ReducerFactory.getSequenceReducer;
import static schemacrawler.filter.ReducerFactory.getSynonymReducer;
import static schemacrawler.filter.ReducerFactory.getTableReducer;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.InclusionRuleWithRegularExpression;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Function;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.JavaSqlTypeGroup;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.TypedObject;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.graph.TreeNode;

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

  public enum SimpleDatabaseObjectType {
    unknown,
    table,
    view,
    procedure,
    function,
    synonym,
    sequence;
  }

  private static final Logger LOGGER = Logger.getLogger(MetaDataUtility.class.getName());

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
   * @param identifiers Identifier quoting strategy
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
   * @param identifiers Identifier quoting strategy
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
   * @param identifiers Identifier quoting strategy
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
   * @param relationshipType Table relationship type
   * @param identifiers Identifier quoting strategy
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

  public static SimpleDatabaseObjectType getSimpleTypeName(final DatabaseObject databaseObject) {
    if (databaseObject == null) {
      return SimpleDatabaseObjectType.unknown;
    }
    if (databaseObject instanceof Synonym) {
      return SimpleDatabaseObjectType.synonym;
    }
    if (databaseObject instanceof Sequence) {
      return SimpleDatabaseObjectType.sequence;
    }
    if (databaseObject instanceof Function) {
      return SimpleDatabaseObjectType.function;
    }
    if (databaseObject instanceof Procedure) {
      return SimpleDatabaseObjectType.procedure;
    }
    // NOTE: Check View before Table, since View is a subclass of Table
    if (databaseObject instanceof View) {
      return SimpleDatabaseObjectType.view;
    }
    if (databaseObject instanceof Table) {
      return SimpleDatabaseObjectType.table;
    }
    return SimpleDatabaseObjectType.unknown;
  }

  public static String getTypeName(final DatabaseObject databaseObject) {
    if (databaseObject instanceof TypedObject<?> typedObject) {
      return typedObject.getType().toString();
    }
    final SimpleDatabaseObjectType simpleTypeName = getSimpleTypeName(databaseObject);
    if (simpleTypeName == SimpleDatabaseObjectType.unknown) {
      return "";
    }
    return simpleTypeName.name();
  }

  public static String inclusionRuleString(final InclusionRule inclusionRule) {
    String inclusionRuleString = ".*";
    if (inclusionRule instanceof InclusionRuleWithRegularExpression expression) {
      final String schemaInclusionPattern = expression.getInclusionPattern().pattern();
      if (!isBlank(schemaInclusionPattern)) {
        inclusionRuleString = schemaInclusionPattern;
      }
    }
    return inclusionRuleString;
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

  public static boolean isView(final Table table) {
    return table instanceof View;
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
      if (!omitLargeObjectColumns
          || javaSqlTypeGroup != JavaSqlTypeGroup.large_object
              && javaSqlTypeGroup != JavaSqlTypeGroup.object) {
        columnsList.add(identifiers.quoteName(column.getName()));
      }
    }
    return String.join(", ", columnsList);
  }

  public static void logCatalogSummary(final Catalog catalog, final Level logLevel) {
    if (catalog == null || logLevel == null) {
      return;
    }
    LOGGER.log(logLevel, () -> summarizeCatalog(catalog));
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
    if (catalog == null) {
      return "";
    }

    final TreeNode<String> countTree = new TreeNode<>("catalog", catalog.getName());

    final Collection<Schema> schemas = catalog.getSchemas();
    final TreeNode<?> schemasNode = countTree.addChild(new TreeNode<>("schemas", schemas.size()));
    for (final Schema schema : schemas) {
      final TreeNode<?> schemaNode =
          schemasNode.addChild(new TreeNode<>("schema", schema.getFullName()));
      // Column data types
      final Collection<ColumnDataType> columnDataTypes = catalog.getColumnDataTypes(schema);
      schemaNode.addChild(new TreeNode<>("data-types", columnDataTypes.size()));
      // Tables
      final Collection<Table> tables = catalog.getTables(schema);
      final TreeNode<?> tablesNode = schemaNode.addChild(new TreeNode<>("tables", tables.size()));
      if (!tables.isEmpty()) {
        int columnCount = 0;
        int pkCount = 0;
        int fkCount = 0;
        int indexCount = 0;
        int triggerCount = 0;
        for (final Table table : tables) {
          columnCount = columnCount + table.getColumns().size();
          if (table.hasPrimaryKey()) {
            pkCount = pkCount + 1;
          }
          fkCount = fkCount + table.getImportedForeignKeys().size();
          indexCount = indexCount + table.getIndexes().size();
          triggerCount = triggerCount + table.getTriggers().size();
        }
        tablesNode.addChild(new TreeNode<>("columns", columnCount));
        tablesNode.addChild(new TreeNode<>("primary-keys", pkCount));
        tablesNode.addChild(new TreeNode<>("foreign-keys", fkCount));
        tablesNode.addChild(new TreeNode<>("indexes", indexCount));
        tablesNode.addChild(new TreeNode<>("triggers", triggerCount));
      }
      // Routines
      final Collection<Routine> routines = catalog.getRoutines(schema);
      final TreeNode<?> routinesNode =
          schemaNode.addChild(new TreeNode<>("routines", routines.size()));
      if (!routines.isEmpty()) {
        int procedureCount = 0;
        int functionCount = 0;
        int parametersCount = 0;
        for (final Routine routine : routines) {
          final RoutineType routineType = routine.getType();
          switch (routineType) {
            case procedure:
              procedureCount = procedureCount + 1;
              break;
            case function:
              functionCount = functionCount + 1;
              break;
            default:
              continue;
          }
          parametersCount = parametersCount + routine.getParameters().size();
        }
        routinesNode.addChild(new TreeNode<>("procedures", procedureCount));
        routinesNode.addChild(new TreeNode<>("functions", functionCount));
        routinesNode.addChild(new TreeNode<>("parameters", parametersCount));
      }
      // Synonyms
      final Collection<Synonym> synonyms = catalog.getSynonyms(schema);
      schemaNode.addChild(new TreeNode<>("synonyms", synonyms.size()));
      // Sequences
      final Collection<Sequence> sequences = catalog.getSequences(schema);
      schemaNode.addChild(new TreeNode<>("sequences", sequences.size()));
    }

    final CrawlInfo crawlInfo = catalog.getCrawlInfo();
    return "Loaded catalog%n%s%n%s".formatted(crawlInfo, countTree);
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
