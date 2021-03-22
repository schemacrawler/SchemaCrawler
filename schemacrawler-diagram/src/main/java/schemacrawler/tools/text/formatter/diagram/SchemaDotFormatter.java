/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.text.formatter.diagram;

import static schemacrawler.loader.counts.TableRowCountsUtility.getRowCountMessage;
import static schemacrawler.loader.counts.TableRowCountsUtility.hasRowCount;
import static schemacrawler.utility.MetaDataUtility.findForeignKeyCardinality;
import static schemacrawler.utility.MetaDataUtility.getColumnsListAsString;
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
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.TableReferenceType;
import schemacrawler.schema.WeakAssociation;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.utility.MetaDataUtility.ForeignKeyCardinality;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.Color;
import us.fatehi.utility.html.Alignment;
import us.fatehi.utility.html.Tag;

/**
 * Graphviz DOT formatting of schema.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaDotFormatter extends BaseDotFormatter implements SchemaTraversalHandler {

  private final boolean isVerbose;
  private final boolean isBrief;

  /**
   * Text formatting of schema.
   *
   * @param schemaTextDetailType Types for text formatting of schema
   * @param options Options for text formatting of schema
   * @param outputOptions Options for text formatting of schema
   * @param identifierQuoteString Quote character for database objects
   * @throws SchemaCrawlerException On an exception
   */
  public SchemaDotFormatter(
      final SchemaTextDetailType schemaTextDetailType,
      final DiagramOptions options,
      final OutputOptions outputOptions,
      final String identifierQuoteString)
      throws SchemaCrawlerException {
    super(
        options,
        schemaTextDetailType == SchemaTextDetailType.details,
        outputOptions,
        identifierQuoteString);
    isVerbose = schemaTextDetailType == SchemaTextDetailType.details;
    isBrief = schemaTextDetailType == SchemaTextDetailType.brief;
  }

  @Override
  public void handle(final ColumnDataType columnDataType) throws SchemaCrawlerException {
    // No output required
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

    final String tableName;
    if (options.isShowUnqualifiedNames()) {
      tableName = identifiers.quoteName(table);
    } else {
      tableName = identifiers.quoteFullName(table);
    }
    final String tableType = "[" + table.getTableType() + "]";

    final Color tableNameBgColor = colorMap.getColor(table);
    final int colspan = options.isShowOrdinalNumbers() ? 3 : 2;

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
                        .withColumnSpan(colspan)
                        .make())
                .addInnerTag(
                    tableCell()
                        .withEscapedText(tableType)
                        .withAlignment(Alignment.right)
                        .withBackground(tableNameBgColor)
                        .make())
                .render(html))
        .println();

    printTableRemarks(table);

    printTableColumns(table.getColumns());
    if (isVerbose) {
      printTableColumns(new ArrayList<>(table.getHiddenColumns()));
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
  public void handleRoutinesEnd() throws SchemaCrawlerException {
    // No output required
  }

  @Override
  public void handleRoutinesStart() throws SchemaCrawlerException {
    // No output required
  }

  @Override
  public void handleSequencesEnd() throws SchemaCrawlerException {
    // No output required
  }

  @Override
  public void handleSequencesStart() throws SchemaCrawlerException {
    // No output required
  }

  @Override
  public void handleSynonymsEnd() throws SchemaCrawlerException {
    // No output required
  }

  @Override
  public void handleSynonymsStart() throws SchemaCrawlerException {
    // No output required
  }

  @Override
  public void handleTablesEnd() throws SchemaCrawlerException {
    // No output required
  }

  @Override
  public void handleTablesStart() throws SchemaCrawlerException {
    // No output required
  }

  private String arrowhead(final ForeignKeyCardinality connectivity) {
    switch (connectivity) {
      case unknown:
        return "box";
      case zero_one:
        return "teeodot";
      case zero_many:
        return "crowodot";
      case one_one:
        return "teetee";
      default:
        return "box";
    }
  }

  private String[] getPortIds(final Column column, final boolean isNewNode) {
    final String[] portIds = new String[2];

    if (!isNewNode) {
      portIds[0] = String.format("\"%s\":\"%s.start\"", nodeId(column.getParent()), nodeId(column));
      portIds[1] = String.format("\"%s\":\"%s.end\"", nodeId(column.getParent()), nodeId(column));
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
    if (table == null) {
      return;
    }
    final List<TableConstraint> tableConstraints = new ArrayList<>();
    tableConstraints.addAll(table.getAlternateKeys());

    for (final TableConstraint tableConstraint : tableConstraints) {
      final String constraintText =
          String.format(
              "\u2022 %s (%s) [alternate key]",
              identifiers.quoteName(tableConstraint.getName()),
              getColumnsListAsString(
                  tableConstraint,
                  identifiers.getIdentifierQuotingStrategy(),
                  identifiers.getIdentifierQuoteString()));

      formattingHelper
          .append(
              tableRow()
                  .make()
                  .addInnerTag(
                      tableCell()
                          .withEscapedText(constraintText)
                          .withAlignment(Alignment.left)
                          .withColumnSpan(3)
                          .make())
                  .render(html))
          .println();

      if (tableConstraint.hasRemarks()) {
        formattingHelper
            .append(
                tableRow()
                    .make()
                    .addInnerTag(
                        tableCell()
                            .withEscapedText(tableConstraint.getRemarks())
                            .withAlignment(Alignment.left)
                            .withColumnSpan(3)
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

    final String[] pkPortIds = getPortIds(primaryKeyColumn, isPkColumnFiltered);
    final String[] fkPortIds = getPortIds(foreignKeyColumn, isFkColumnFiltered);

    final DiagramOptions diagramOptions = options;
    final String pkSymbol;
    if (diagramOptions.isShowPrimaryKeyCardinality()) {
      pkSymbol = "teetee";
    } else {
      pkSymbol = "none";
    }

    final String fkSymbol;
    if (diagramOptions.isShowForeignKeyCardinality()) {
      fkSymbol = arrowhead(fkCardinality);
    } else {
      fkSymbol = "none";
    }

    final String style;
    if (isForeignKey) {
      style = "solid";
    } else {
      style = "dashed";
    }

    final String associationName;
    if (isForeignKey && options.isHideForeignKeyNames()
        || !isForeignKey && options.isHideWeakAssociationNames()) {
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

    return String.format(
        "  %s:w -> %s:e [label=<%s> style=\"%s\" dir=\"both\" arrowhead=\"%s\" arrowtail=\"%s\"];%n",
        fkPortIds[0], pkPortIds[1], label, style, pkSymbol, fkSymbol);
  }

  private void printForeignKeys(final Table table) {
    printForeignKeys(table, table.getForeignKeys());
  }

  private <R extends ColumnReference> void printForeignKeys(
      final Table table, final Collection<? extends TableReference> foreignKeys) {
    for (final TableReference foreignKey : foreignKeys) {
      final boolean isForeignKey =
          foreignKey.getTableReferenceType() == TableReferenceType.foreign_key;
      final ForeignKeyCardinality fkCardinality = findForeignKeyCardinality(foreignKey);
      boolean showRemarks = !options.isHideRemarks() && foreignKey.hasRemarks();
      for (final ColumnReference columnRef : foreignKey) {
        final Table referencedTable = columnRef.getPrimaryKeyColumn().getParent();
        final Table referencingTable = columnRef.getForeignKeyColumn().getParent();
        final boolean isForeignKeyFiltered =
            referencingTable.getAttribute("schemacrawler.table.no_grep_match", false);
        if (isForeignKeyFiltered) {
          continue;
        }
        final boolean isPkColumnFiltered =
            referencedTable.getAttribute("schemacrawler.table.filtered_out", false)
                || referencedTable instanceof PartialDatabaseObject;
        final boolean isFkColumnFiltered =
            referencingTable.getAttribute("schemacrawler.table.filtered_out", false)
                || referencingTable instanceof PartialDatabaseObject;
        final String remarks;
        if (showRemarks) {
          remarks = foreignKey.getRemarks();
        } else {
          remarks = "";
        }

        if (table.equals(referencedTable) || isPkColumnFiltered && table.equals(referencingTable)) {
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

  private String printNewNode(final Column column) {
    final String nodeId = "\"" + nodeId(column) + "\"";
    final String columnName;
    if (options.isShowUnqualifiedNames()) {
      columnName = identifiers.quoteShortName(column);
    } else {
      columnName = identifiers.quoteFullName(column);
    }
    final String columnNode = String.format("  %s [label=<%s>];%n", nodeId, columnName);

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
    if (column == null) {
      return;
    }
    try {
      if (!column.getColumnDataType().isEnumerated()) {
        return;
      }
    } catch (final NotLoadedException e) {
      // The column may be partial for index pseudo-columns
      return;
    }

    final String enumValues =
        String.format("'%s'", String.join("', ", column.getColumnDataType().getEnumValues()));

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
      if (isBrief && !isColumnSignificant(column)) {
        continue;
      }

      final String columnTypeName;
      if (options.isShowStandardColumnTypeNames()) {
        columnTypeName = column.getColumnDataType().getJavaSqlType().getName();
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
                        .withColumnSpan(3)
                        .make())
                .render(html))
        .println();
  }

  private void printTableRowCount(final Table table) {
    if (options.isHideTableRowCounts() || table == null || !hasRowCount(table)) {
      return;
    }
    formattingHelper
        .append(
            tableRow()
                .make()
                .addInnerTag(
                    tableCell()
                        .withEscapedText(getRowCountMessage(table))
                        .withAlignment(Alignment.right)
                        .withColumnSpan(3)
                        .make())
                .render(html))
        .println();
  }

  private void printWeakAssociations(final Table table) {
    if (!options.isShowWeakAssociations()) {
      return;
    }

    final Collection<WeakAssociation> weakFks = table.getWeakAssociations();
    printForeignKeys(table, weakFks);
  }
}
