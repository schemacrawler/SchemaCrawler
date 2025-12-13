/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.diagram;

import static schemacrawler.loader.counts.TableRowCountsUtility.getRowCountMessage;
import static schemacrawler.loader.counts.TableRowCountsUtility.hasRowCount;
import static schemacrawler.schema.TableConstraintType.foreign_key;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideAlternateKeyNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideForeignKeyNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideIndexNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideWeakAssociationNames;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideAlternateKeys;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideForeignKeys;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideIndexes;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideTableColumns;
import static schemacrawler.tools.command.text.schema.options.HideDependantDatabaseObjectsType.hideWeakAssociations;
import static schemacrawler.utility.MetaDataUtility.findForeignKeyCardinality;
import static schemacrawler.utility.MetaDataUtility.getColumnsListAsString;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.html.TagBuilder.tableCell;
import static us.fatehi.utility.html.TagBuilder.tableRow;
import static us.fatehi.utility.html.TagOutputFormat.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import schemacrawler.crawl.NotLoadedException;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.WeakAssociation;
import schemacrawler.schema.Identifiers;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.utility.MetaDataUtility.ForeignKeyCardinality;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.Color;
import us.fatehi.utility.html.Alignment;
import us.fatehi.utility.html.Tag;

/** Graphviz DOT formatting of schema. */
public final class SchemaDotFormatter extends BaseDotFormatter implements SchemaTraversalHandler {

  private final int tableColspan;

  /**
   * Text formatting of schema.
   *
   * @param schemaTextDetailType Types for text formatting of schema
   * @param options Options for text formatting of schema
   * @param outputOptions Options for text formatting of schema
   * @param identifierQuoteString Quote character for database objects
   */
  public SchemaDotFormatter(
      final SchemaTextDetailType schemaTextDetailType,
      final DiagramOptions options,
      final OutputOptions outputOptions,
      final Identifiers identifiers) {
    super(schemaTextDetailType, options, outputOptions, identifiers);

    tableColspan = options.isShowOrdinalNumbers() ? 4 : 3;
  }

  @Override
  public void handle(final ColumnDataType columnDataType) {
    // No output required
  }

  @Override
  public void handleInfo(final DatabaseInfo dbInfo) {
    // No-op
  }

  @Override
  public void handleInfo(final JdbcDriverInfo driverInfo) {
    // No-op
  }

  /**
   * Provides information on the database schema.
   *
   * @param routine Routine metadata.
   */
  @Override
  public void handle(final Routine routine) {
    // No output required
  }

  /**
   * Provides information on the database schema.
   *
   * @param sequence Sequence metadata.
   */
  @Override
  public void handle(final Sequence sequence) {
    // No output required
  }

  /**
   * Provides information on the database schema.
   *
   * @param synonym Synonym metadata.
   */
  @Override
  public void handle(final Synonym synonym) {
    // No output required
  }

  @Override
  public void handle(final Table table) {

    final String tableName = quoteName(table);
    final String tableType = "[" + table.getTableType() + "]";

    final Color tableNameBgColor = colorMap.getColor(table);

    formattingHelper
        .append("  /* ")
        .append(table.getFullName())
        .append(" -=-=-=-=-=-=-=-=-=-=-=-=-=- */")
        .println();
    formattingHelper.append("  \"").append(nodeId(table)).append("\" [").println();
    formattingHelper.append("    label=<").println();
    formattingHelper
        .append("      <table border=\"1\" cellborder=\"0\" cellspacing=\"0\" color=\"#888888\">")
        .println();

    formattingHelper
        .append(
            tableRow()
                .make()
                .addInnerTag(
                    tableCell()
                        .withEscapedText(tableName)
                        .withAlignment(Alignment.left)
                        .withEmphasis(true)
                        .withBackground(tableNameBgColor)
                        .withColumnSpan(tableColspan - 1)
                        .make())
                .addInnerTag(
                    tableCell()
                        .withEscapedText(tableType)
                        .withBackground(tableNameBgColor)
                        .withAlignment(Alignment.right)
                        .make())
                .render(html))
        .println();

    printTableRemarks(table);

    if (!options.is(hideTableColumns)) {
      printTableColumns(table.getColumns());
      if (isVerbose()) {
        printTableColumns(new ArrayList<>(table.getHiddenColumns()));
      }
    }
    if (isVerbose()) {
      printIndexes(table);
    }
    printAlternateKeys(table);
    printTableRowCount(table);

    formattingHelper.append("      </table>").println();
    formattingHelper.append("    >").println();
    formattingHelper.append("  ];").println();
    formattingHelper.println();

    printForeignKeys(table);
    printWeakAssociations(table);

    formattingHelper.println();
    formattingHelper.println();
  }

  @Override
  public void handleColumnDataTypesEnd() {
    // No output required
  }

  @Override
  public void handleColumnDataTypesStart() {
    // No output required
  }

  @Override
  public void handleInfoEnd() {
    // No-op
  }

  @Override
  public void handleInfoStart() {
    // No-op
  }

  @Override
  public void handleRoutinesEnd() {
    // No output required
  }

  @Override
  public void handleRoutinesStart() {
    // No output required
  }

  @Override
  public void handleSequencesEnd() {
    // No output required
  }

  @Override
  public void handleSequencesStart() {
    // No output required
  }

  @Override
  public void handleSynonymsEnd() {
    // No output required
  }

  @Override
  public void handleSynonymsStart() {
    // No output required
  }

  @Override
  public void handleTablesEnd() {
    // No output required
  }

  @Override
  public void handleTablesStart() {
    // No output required
  }

  private String arrowheadFk(final ForeignKeyCardinality connectivity) {
    if (!options.isShowForeignKeyCardinality()) {
      return "none";
    }
    switch (connectivity) {
      case zero_one:
        return "teeodot";
      case zero_many:
        return "crowodot";
      case one_one:
        return "teetee";
      default: // Including "unknown"
        return "box";
    }
  }

  private String arrowheadPk() {
    final String pkSymbol;
    if (options.isShowPrimaryKeyCardinality()) {
      pkSymbol = "teetee";
    } else {
      pkSymbol = "none";
    }
    return pkSymbol;
  }

  private String columnReferenceLineStyle(final boolean isForeignKey) {
    final String style;
    if (isForeignKey) {
      style = "solid";
    } else {
      style = "dashed";
    }
    return style;
  }

  private String[] getPortIds(final Column column, final boolean isNewNode) {
    final String[] portIds = new String[2];

    if (!isNewNode) {
      portIds[0] = "\"%s\":\"%s.start\"".formatted(nodeId(column.getParent()), nodeId(column));
      portIds[1] = "\"%s\":\"%s.end\"".formatted(nodeId(column.getParent()), nodeId(column));
    } else {
      // Create new node
      final String nodeId = printNewNode(column);
      //
      portIds[0] = nodeId;
      portIds[1] = nodeId;
    }
    return portIds;
  }

  private void printAlternateKeys(final Table table) {
    if (table == null || options.is(hideAlternateKeys)) {
      return;
    }

    final Collection<PrimaryKey> alternateKeys = table.getAlternateKeys();
    if (alternateKeys == null || alternateKeys.isEmpty()) {
      return;
    }

    formattingHelper.append("\t<hr/>").append(System.lineSeparator());

    for (final TableConstraint alternateKey : alternateKeys) {
      final String name = identifiers.quoteName(alternateKey);
      final String akName;
      if (!options.is(hideAlternateKeyNames)) {
        akName = name;
      } else {
        akName = "";
      }

      String columnsList = getColumnsListAsString(alternateKey, identifiers);
      if (!isBlank(columnsList)) {
        columnsList = " (" + columnsList + ")";
      }
      final String constraintText = "\u2022 %s%s [alternate key]".formatted(akName, columnsList);

      formattingHelper
          .append(
              tableRow()
                  .make()
                  .addInnerTag(
                      tableCell()
                          .withEscapedText(constraintText)
                          .withAlignment(Alignment.left)
                          .withColumnSpan(tableColspan)
                          .make())
                  .render(html))
          .println();

      if (alternateKey.hasRemarks()) {
        formattingHelper
            .append(
                tableRow()
                    .make()
                    .addInnerTag(
                        tableCell()
                            .withEscapedText(alternateKey.getRemarks())
                            .withAlignment(Alignment.left)
                            .withBackground(Color.fromRGB(0xF4, 0xF4, 0xF4))
                            .withColumnSpan(tableColspan)
                            .make())
                    .render(html))
            .println();
      }
    }
  }

  private String printColumnReference(
      final boolean isForeignKey,
      final String fkName,
      final ColumnReference columnRef,
      final ForeignKeyCardinality fkCardinality,
      final boolean isPkColumnFiltered,
      final boolean isFkColumnFiltered,
      final boolean showRemarks,
      final String remarks) {

    final Column primaryKeyColumn = columnRef.getPrimaryKeyColumn();
    final Column foreignKeyColumn = columnRef.getForeignKeyColumn();

    final boolean isPkColumnSignificant = isColumnSignificant(primaryKeyColumn);
    final boolean isFkColumnSignificant = isColumnSignificant(foreignKeyColumn);

    // Primary key column in a weak association is not a significant column
    // Hide hanging foreign keys when filtered tables are not shown
    if (!isPkColumnSignificant || !options.isShowFilteredTables() && !isFkColumnSignificant) {
      return "";
    }

    final String[] pkPortIds = getPortIds(primaryKeyColumn, isPkColumnFiltered);
    final String[] fkPortIds =
        getPortIds(foreignKeyColumn, isFkColumnFiltered || !isFkColumnSignificant);

    final String pkSymbol = arrowheadPk();
    final String fkSymbol = arrowheadFk(fkCardinality);
    final String lineStyle = columnReferenceLineStyle(isForeignKey);

    final String associationName;
    if (isForeignKey && options.is(hideForeignKeyNames)
        || !isForeignKey && options.is(hideWeakAssociationNames)) {
      associationName = "";
    } else {
      associationName = fkName;
    }

    final String label;
    if (showRemarks) {
      final String remarksLines = remarks.replaceAll("\\R", "<br/>");
      label = associationName + "<br/>" + remarksLines;
    } else {
      label = associationName;
    }

    return "  %s:w -> %s:e [label=<%s> style=\"%s\" dir=\"both\" arrowhead=\"%s\" arrowtail=\"%s\"];%n"
        .formatted(fkPortIds[0], pkPortIds[1], label, lineStyle, pkSymbol, fkSymbol);
  }

  private void printForeignKeys(final Table table) {
    if (table == null || options.is(hideForeignKeys)) {
      return;
    }
    printForeignKeys(table, table.getForeignKeys());
  }

  private <R extends ColumnReference> void printForeignKeys(
      final Table table, final Collection<? extends TableReference> foreignKeys) {
    for (final TableReference foreignKey : foreignKeys) {
      final boolean isForeignKey = foreignKey.getType() == foreign_key;
      final ForeignKeyCardinality fkCardinality = findForeignKeyCardinality(foreignKey);
      boolean showRemarks = !options.isHideRemarks() && foreignKey.hasRemarks();
      for (final ColumnReference columnRef : foreignKey) {
        final Table referencedTable = columnRef.getPrimaryKeyColumn().getParent();
        final Table dependentTable = columnRef.getForeignKeyColumn().getParent();

        final boolean isPkColumnFiltered = isTableFiltered(referencedTable);
        final boolean isFkColumnFiltered = isTableFiltered(dependentTable);

        // Hide foreign keys to filtered tables
        if (!options.isShowFilteredTables() && (isPkColumnFiltered || isFkColumnFiltered)) {
          continue;
        }

        final String remarks;
        if (showRemarks) {
          remarks = foreignKey.getRemarks();
        } else {
          remarks = "";
        }

        if (table.equals(referencedTable) || isPkColumnFiltered && table.equals(dependentTable)) {
          formattingHelper.append(
              printColumnReference(
                  isForeignKey,
                  identifiers.quoteName(foreignKey.getName()),
                  columnRef,
                  fkCardinality,
                  isPkColumnFiltered,
                  isFkColumnFiltered,
                  showRemarks,
                  remarks));
        }
        // Show remarks only on the first reference
        showRemarks = false;
      }
    }
  }

  private void printIndexes(final Table table) {
    if (table == null || options.is(hideIndexes)) {
      return;
    }

    final Collection<Index> indexes = table.getIndexes();
    if (indexes == null || indexes.isEmpty()) {
      return;
    }

    formattingHelper.append("\t<hr/>").append(System.lineSeparator());

    for (final Index index : indexes) {
      final String name = identifiers.quoteName(index);
      final String indexName;
      if (!options.is(hideIndexNames)) {
        indexName = name;
      } else {
        indexName = "";
      }

      final IndexType indexType = index.getIndexType();
      String indexTypeString = "";
      if (indexType != IndexType.unknown && indexType != IndexType.other) {
        indexTypeString = indexType.toString() + " ";
      }
      final String indexDetails =
          (index.isUnique() ? "" : "non-") + "unique " + indexTypeString + "index";
      String columnsList = getColumnsListAsString(index, identifiers);
      if (!isBlank(columnsList)) {
        columnsList = " (" + columnsList + ")";
      }
      final String constraintText =
          "\u2022 %s%s [%s]".formatted(indexName, columnsList, indexDetails);

      formattingHelper
          .append(
              tableRow()
                  .make()
                  .addInnerTag(
                      tableCell()
                          .withEscapedText(constraintText)
                          .withAlignment(Alignment.left)
                          .withBackground(Color.fromRGB(0xF4, 0xF4, 0xF4))
                          .withColumnSpan(tableColspan)
                          .make())
                  .render(html))
          .println();

      if (index.hasRemarks()) {
        formattingHelper
            .append(
                tableRow()
                    .make()
                    .addInnerTag(
                        tableCell()
                            .withEscapedText(index.getRemarks())
                            .withAlignment(Alignment.left)
                            .withBackground(Color.fromRGB(0xF4, 0xF4, 0xF4))
                            .withColumnSpan(3)
                            .make())
                    .render(html))
            .println();
      }
    }
  }

  private String printNewNode(final Column column) {
    final String nodeId = "\"" + nodeId(column) + "\"";
    final String columnName;
    if (options.isShowUnqualifiedNames()) {
      columnName = identifiers.quoteShortName(column);
    } else {
      columnName = identifiers.quoteFullName(column);
    }
    final String columnNode = "  %s [label=<%s>];%n".formatted(nodeId, columnName);

    formattingHelper.append(columnNode);

    return nodeId;
  }

  private void printTableColumnAutoIncremented(final Column column) {
    if (column == null) {
      return;
    }
    try {
      if (!column.isAutoIncremented()) {
        return;
      }
    } catch (final NotLoadedException e) {
      // The column may be partial for index pseudo-columns
      return;
    }

    final Tag row = tableRow().make();
    if (options.isShowOrdinalNumbers()) {
      row.addInnerTag(tableCell().withAlignment(Alignment.right).make());
    }
    row.addInnerTag(tableCell().withAlignment(Alignment.left).make())
        .addInnerTag(tableCell().withText(" ").withAlignment(Alignment.left).make())
        .addInnerTag(
            tableCell().withEscapedText("auto-incremented").withAlignment(Alignment.left).make());
    formattingHelper.append(row.render(html)).println();
  }

  private void printTableColumnEnumValues(final Column column) {
    if (column == null
        || !column.isColumnDataTypeKnown()
        || !column.getColumnDataType().isEnumerated()) {
      return;
    }

    final String enumValues =
        "'%s'".formatted(String.join("', ", column.getColumnDataType().getEnumValues()));

    final Tag row = tableRow().make();
    if (options.isShowOrdinalNumbers()) {
      row.addInnerTag(tableCell().withAlignment(Alignment.right).make());
    }
    row.addInnerTag(tableCell().withAlignment(Alignment.left).make())
        .addInnerTag(tableCell().withEscapedText(" ").withAlignment(Alignment.left).make())
        .addInnerTag(tableCell().withEscapedText(enumValues).withAlignment(Alignment.left).make());
    formattingHelper.append(row.render(html)).println();
  }

  private void printTableColumnGenerated(final Column column) {
    if (column == null) {
      return;
    }
    try {
      if (!column.isGenerated()) {
        return;
      }
    } catch (final NotLoadedException e) {
      // The column may be partial for index pseudo-columns
      return;
    }

    final Tag row = tableRow().make();
    if (options.isShowOrdinalNumbers()) {
      row.addInnerTag(tableCell().withAlignment(Alignment.right).make());
    }
    row.addInnerTag(tableCell().withAlignment(Alignment.left).make())
        .addInnerTag(tableCell().withAlignment(Alignment.left).make())
        .addInnerTag(tableCell().withEscapedText("generated").withAlignment(Alignment.left).make());
    formattingHelper.append(row.render(html)).println();
  }

  private void printTableColumnHidden(final Column column) {
    if (column == null) {
      return;
    }
    try {
      if (!column.isHidden()) {
        return;
      }
    } catch (final NotLoadedException e) {
      // The column may be partial for index pseudo-columns
      return;
    }

    final Tag row = tableRow().make();
    if (options.isShowOrdinalNumbers()) {
      row.addInnerTag(tableCell().withAlignment(Alignment.right).make());
    }
    row.addInnerTag(tableCell().withAlignment(Alignment.left).make())
        .addInnerTag(tableCell().withEscapedText(" ").withAlignment(Alignment.left).make())
        .addInnerTag(tableCell().withEscapedText("hidden").withAlignment(Alignment.left).make());
    formattingHelper.append(row.render(html)).println();
  }

  private void printTableColumnRemarks(final Column column) {
    if (column == null || !column.hasRemarks() || options.isHideRemarks()) {
      return;
    }
    final Tag remarksRow = tableRow().make();
    if (options.isShowOrdinalNumbers()) {
      remarksRow.addInnerTag(tableCell().withAlignment(Alignment.right).make());
    }
    remarksRow
        .addInnerTag(tableCell().withAlignment(Alignment.left).make())
        .addInnerTag(tableCell().withEscapedText(" ").withAlignment(Alignment.left).make())
        .addInnerTag(
            tableCell().withEscapedText(column.getRemarks()).withAlignment(Alignment.left).make());
    formattingHelper.append(remarksRow.render(html)).println();
  }

  private void printTableColumns(final List<Column> columns) {
    if (columns.isEmpty()) {
      return;
    }

    Collections.sort(
        columns, NamedObjectSort.getNamedObjectSort(options.isAlphabeticalSortForTableColumns()));

    for (final Column column : columns) {
      if (!isColumnSignificant(column)) {
        continue;
      }

      final String columnTypeName;
      if (options.isShowStandardColumnTypeNames()) {
        columnTypeName = column.getColumnDataType().getStandardTypeName();
      } else {
        columnTypeName = column.getColumnDataType().getDatabaseSpecificTypeName();
      }
      final String columnType = columnTypeName + column.getWidth();
      final String nullable = columnNullable(columnTypeName, column.isNullable());
      final String columnDetails = columnType + nullable;
      final boolean emphasize = column.isPartOfPrimaryKey();

      final Tag row = tableRow().make();
      if (options.isShowOrdinalNumbers()) {
        final String ordinalNumberString = String.valueOf(column.getOrdinalPosition());
        row.addInnerTag(
            tableCell().withEscapedText(ordinalNumberString).withAlignment(Alignment.right).make());
      }
      row.addInnerTag(
              tableCell()
                  .withEscapedText(identifiers.quoteName(column.getName()))
                  .withAlignment(Alignment.left)
                  .withEmphasis(emphasize)
                  .make())
          .addInnerTag(tableCell().withEscapedText(" ").withAlignment(Alignment.left).make())
          .addInnerTag(
              tableCell().withEscapedText(columnDetails).withAlignment(Alignment.left).make());

      row.firstInnerTag().addAttribute("port", nodeId(column) + ".start");
      row.lastInnerTag().addAttribute("port", nodeId(column) + ".end");
      formattingHelper.append(row.render(html)).println();

      printTableColumnEnumValues(column);
      printTableColumnHidden(column);
      printTableColumnAutoIncremented(column);
      printTableColumnGenerated(column);
      printTableColumnRemarks(column);
    }
  }

  private void printTableRemarks(final Table table) {
    if (table == null || !table.hasRemarks() || options.isHideRemarks()) {
      return;
    }
    formattingHelper
        .append(
            tableRow()
                .make()
                .addInnerTag(
                    tableCell()
                        .withEscapedText(table.getRemarks())
                        .withAlignment(Alignment.left)
                        .withColumnSpan(tableColspan)
                        .make())
                .render(html))
        .println();
  }

  private void printTableRowCount(final Table table) {
    if (options.isHideTableRowCounts() || !hasRowCount(table)) {
      return;
    }

    formattingHelper.append("\t<hr/>").append(System.lineSeparator());

    formattingHelper
        .append(
            tableRow()
                .make()
                .addInnerTag(
                    tableCell()
                        .withEscapedText(getRowCountMessage(table))
                        .withAlignment(Alignment.right)
                        .withColumnSpan(tableColspan)
                        .make())
                .render(html))
        .println();
  }

  private void printWeakAssociations(final Table table) {
    if (table == null || options.is(hideWeakAssociations)) {
      return;
    }
    final Collection<WeakAssociation> weakFks = table.getWeakAssociations();
    printForeignKeys(table, weakFks);
  }
}
