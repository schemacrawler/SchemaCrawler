/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package schemacrawler.tools.text.schema;


import static schemacrawler.utility.MetaDataUtility.getConnectivity;
import static schemacrawler.utility.MetaDataUtility.isForeignKeyUnique;
import static sf.util.Utility.convertForComparison;
import static sf.util.Utility.isBlank;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.NamedObjectWithAttributes;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.associations.CatalogWithAssociations;
import schemacrawler.tools.integration.graph.GraphOptions;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.base.BaseDotFormatter;
import schemacrawler.tools.text.utility.Alignment;
import schemacrawler.tools.text.utility.TableRow;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.utility.MetaDataUtility.Connectivity;
import schemacrawler.utility.NamedObjectSort;

/**
 * GraphViz DOT formatting of schema.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaDotFormatter
  extends BaseDotFormatter<SchemaTextOptions>
  implements SchemaTraversalHandler
{

  private static Color pastelColorHTMLValue(final String text)
  {
    final float hue;
    if (isBlank(text))
    {
      hue = 0.123456f;
    }
    else
    {
      hue = text.hashCode() / 5119f % 1;
    }

    final float saturation = 0.4f;
    final float luminance = 0.98f;

    final Color color = Color.getHSBColor(hue, saturation, luminance);
    return color;
  }

  private final boolean isVerbose;
  private final boolean isBrief;
  private final Map<Schema, Color> colorMap;

  /**
   * Text formatting of schema.
   *
   * @param schemaTextDetailType
   *        Types for text formatting of schema
   * @param options
   *        Options for text formatting of schema
   * @param outputOptions
   *        Options for text formatting of schema
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public SchemaDotFormatter(final SchemaTextDetailType schemaTextDetailType,
                            final SchemaTextOptions options,
                            final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    super(options,
          schemaTextDetailType == SchemaTextDetailType.details,
          outputOptions);
    isVerbose = schemaTextDetailType == SchemaTextDetailType.details;
    isBrief = schemaTextDetailType == SchemaTextDetailType.brief;
    colorMap = new HashMap<>();
  }

  @Override
  public void handle(final ColumnDataType columnDataType)
    throws SchemaCrawlerException
  {
  }

  /**
   * Provides information on the database schema.
   *
   * @param routine
   *        Routine metadata.
   */
  @Override
  public void handle(final Routine routine)
  {
  }

  /**
   * Provides information on the database schema.
   *
   * @param sequence
   *        Sequence metadata.
   */
  @Override
  public void handle(final Sequence sequence)
  {
  }

  /**
   * Provides information on the database schema.
   *
   * @param synonym
   *        Synonym metadata.
   */
  @Override
  public void handle(final Synonym synonym)
  {
  }

  /**
   * Provides information on the database schema.
   *
   * @param table
   *        Table metadata.
   */
  @Override
  public void handle(final Table table)
  {

    final String tableName;
    if (options.isShowUnqualifiedNames())
    {
      tableName = table.getName();
    }
    else
    {
      tableName = table.getFullName();
    }
    final String tableType = "[" + table.getTableType() + "]";

    final Color tableNameBgColor = getTableNameBgColor(table);
    final int colspan = options.isShowOrdinalNumbers()? 3: 2;

    out.append("  /* ").append(table.getFullName())
      .append(" -=-=-=-=-=-=-=-=-=-=-=-=-=- */").println();
    out.append("  \"").append(nodeId(table)).append("\" [").println();
    out.append("    label=<").println();
    out
      .append("      <table border=\"1\" cellborder=\"0\" cellpadding=\"2\" cellspacing=\"0\" bgcolor=\"white\" color=\"#555555\">")
      .println();

    out
      .append(new TableRow(TextOutputFormat.html)
        .add(newTableCell(tableName,
                          Alignment.left,
                          true,
                          tableNameBgColor,
                          colspan))
        .add(newTableCell(tableType,
                          Alignment.right,
                          false,
                          tableNameBgColor,
                          1)).toString());
    out.println();

    printTableRemarks(table);

    final List<Column> columns = table.getColumns();
    printTableColumns(columns);

    out.append("      </table>").println();
    out.append("    >").println();
    out.append("  ];").println();
    out.println();

    printForeignKeys(table);

    if (isVerbose)
    {
      printWeakAssociations(table);
    }

    out.println();
    out.println();

    out.flush();
  }

  @Override
  public void handleColumnDataTypesEnd()
  {
  }

  @Override
  public void handleColumnDataTypesStart()
  {
  }

  @Override
  public void handleRoutinesEnd()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleRoutinesStart()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleSequencesEnd()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleSequencesStart()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleSynonymsEnd()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleSynonymsStart()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleTablesEnd()
    throws SchemaCrawlerException
  {
  }

  @Override
  public void handleTablesStart()
    throws SchemaCrawlerException
  {
  }

  private String arrowhead(final Connectivity connectivity)
  {
    switch (connectivity)
    {
      case unknown:
        return "vee";
      case zero_one:
        return "teeodot";
      case zero_many:
        return "crowodot";
      case one_one:
        return "teetee";
      default:
        return "vee";
    }
  }

  private String[] getPortIds(final Column column)
  {
    final String portIds[] = new String[2];

    boolean isColumnReference;
    try
    {
      column.getColumnDataType();
      isColumnReference = false;
    }
    catch (final Exception e)
    {
      isColumnReference = true;
    }

    if (!isColumnReference)
    {
      portIds[0] = String.format("\"%s\":\"%s.start\"",
                                 nodeId(column.getParent()),
                                 nodeId(column));
      portIds[1] = String.format("\"%s\":\"%s.end\"",
                                 nodeId(column.getParent()),
                                 nodeId(column));
    }
    else
    {
      // Create new node
      final String nodeId = printNewNode(column);
      //
      portIds[0] = nodeId;
      portIds[1] = nodeId;
    }
    return portIds;
  }

  private Color getTableNameBgColor(final Table table)
  {
    final Color tableNameBgColor;
    final Schema schema = table.getSchema();
    if (!colorMap.containsKey(schema))
    {
      tableNameBgColor = pastelColorHTMLValue(schema.getFullName());
      colorMap.put(schema, tableNameBgColor);
    }
    else
    {
      tableNameBgColor = colorMap.get(schema);
    }
    return tableNameBgColor;
  }

  private String nodeId(final NamedObjectWithAttributes namedObject)
  {
    if (namedObject == null)
    {
      return "";
    }
    else
    {
      return convertForComparison(namedObject.getName()) + "_"
             + Integer.toHexString(namedObject.getFullName().hashCode());
    }
  }

  private String printColumnReference(final String associationName,
                                      final ColumnReference columnReference,
                                      final boolean isForeignKeyUnique)
  {
    final Column primaryKeyColumn = columnReference.getPrimaryKeyColumn();
    final Column foreignKeyColumn = columnReference.getForeignKeyColumn();

    final String[] pkPortIds = getPortIds(primaryKeyColumn);
    final String[] fkPortIds = getPortIds(foreignKeyColumn);

    final Connectivity connectivity = getConnectivity(foreignKeyColumn,
                                                      isForeignKeyUnique);
    final GraphOptions graphOptions = (GraphOptions) options;
    final String pkSymbol;
    if (graphOptions.isShowPrimaryKeyCardinality())
    {
      pkSymbol = "teetee";
    }
    else
    {
      pkSymbol = "none";
    }

    final String fkSymbol;
    if (graphOptions.isShowForeignKeyCardinality())
    {
      fkSymbol = arrowhead(connectivity);
    }
    else
    {
      fkSymbol = "none";
    }

    final String style;
    if (isBlank(associationName))
    {
      style = "dashed";
    }
    else
    {
      style = "solid";
    }

    return String
      .format("  %s:w -> %s:e [label=<%s> style=\"%s\" dir=\"both\" arrowhead=\"%s\" arrowtail=\"%s\"];%n",
              fkPortIds[0],
              pkPortIds[1],
              options.isHideForeignKeyNames()? "": associationName,
              style,
              pkSymbol,
              fkSymbol);

  }

  private void printForeignKeys(final Table table)
  {
    for (final ForeignKey foreignKey: table.getForeignKeys())
    {
      final boolean isForeignKeyUnique = isForeignKeyUnique(foreignKey, table);
      for (final ColumnReference columnReference: foreignKey
        .getColumnReferences())
      {
        if (table.equals(columnReference.getPrimaryKeyColumn().getParent()))
        {
          out.write(printColumnReference(foreignKey.getName(),
                                         columnReference,
                                         isForeignKeyUnique));
        }
      }
    }
  }

  private String printNewNode(final Column column)
  {
    final String nodeId = "\"" + nodeId(column) + "\"";
    final String columnName;
    if (options.isShowUnqualifiedNames())
    {
      columnName = column.getShortName();
    }
    else
    {
      columnName = column.getFullName();
    }
    final String columnNode = String.format("  %s [label=<%s>];%n",
                                            nodeId,
                                            columnName);

    out.write(columnNode);

    return nodeId;
  }

  private void printTableColumnAutoIncremented(final Column column)
  {
    if (column == null || !column.isAutoIncremented())
    {
      return;
    }
    final TableRow remarksRow = new TableRow(TextOutputFormat.html);
    if (options.isShowOrdinalNumbers())
    {
      remarksRow.add(newTableCell("", Alignment.right, false, Color.white, 1));
    }
    remarksRow
      .add(newTableCell("", Alignment.left, false, Color.white, 1))
      .add(newTableCell(" ", Alignment.left, false, Color.white, 1))
      .add(newTableCell("auto-incremented",
                        Alignment.left,
                        false,
                        Color.white,
                        1));
    out.println(remarksRow.toString());
  }

  private void printTableColumnRemarks(final Column column)
  {
    if (column == null || !column.hasRemarks())
    {
      return;
    }
    final TableRow remarksRow = new TableRow(TextOutputFormat.html);
    if (options.isShowOrdinalNumbers())
    {
      remarksRow.add(newTableCell("", Alignment.right, false, Color.white, 1));
    }
    remarksRow
      .add(newTableCell("", Alignment.left, false, Color.white, 1))
      .add(newTableCell(" ", Alignment.left, false, Color.white, 1))
      .add(newTableCell(column.getRemarks(),
                        Alignment.left,
                        false,
                        Color.white,
                        1));
    out.println(remarksRow.toString());
  }

  private void printTableColumns(final List<Column> columns)
  {
    Collections.sort(columns, NamedObjectSort.getNamedObjectSort(options
      .isAlphabeticalSortForTableColumns()));
    for (final Column column: columns)
    {
      if (isBrief && !isColumnSignificant(column))
      {
        continue;
      }

      final String columnTypeName;
      if (options.isShowStandardColumnTypeNames())
      {
        columnTypeName = column.getColumnDataType().getJavaSqlType()
          .getJavaSqlTypeName();
      }
      else
      {
        columnTypeName = column.getColumnDataType()
          .getDatabaseSpecificTypeName();
      }
      final String columnType = columnTypeName + column.getWidth();
      final String nullable = columnNullable(columnTypeName,
                                             column.isNullable());
      final String columnDetails = columnType + nullable;
      final boolean emphasize = column.isPartOfPrimaryKey();

      final TableRow row = new TableRow(TextOutputFormat.html);
      if (options.isShowOrdinalNumbers())
      {
        final String ordinalNumberString = String.valueOf(column
          .getOrdinalPosition());
        row.add(newTableCell(ordinalNumberString,
                             Alignment.right,
                             false,
                             Color.white,
                             1));
      }
      row
        .add(newTableCell(column.getName(),
                          Alignment.left,
                          emphasize,
                          Color.white,
                          1))
        .add(newTableCell(" ", Alignment.left, false, Color.white, 1))
        .add(newTableCell(columnDetails, Alignment.left, false, Color.white, 1));

      row.firstCell().addAttribute("port", nodeId(column) + ".start");
      row.lastCell().addAttribute("port", nodeId(column) + ".end");
      out.println(row.toString());

      printTableColumnAutoIncremented(column);
      printTableColumnRemarks(column);
    }
  }

  private void printTableRemarks(final Table table)
  {
    if (table == null || !table.hasRemarks())
    {
      return;
    }
    out.append(new TableRow(TextOutputFormat.html).add(newTableCell(table
      .getRemarks(), Alignment.left, false, Color.white, 3)).toString());
    out.println();
  }

  private void printWeakAssociations(final Table table)
  {
    final Collection<ColumnReference> weakAssociations = CatalogWithAssociations
      .getWeakAssociations(table);
    for (final ColumnReference weakAssociation: weakAssociations)
    {
      if (table.equals(weakAssociation.getPrimaryKeyColumn().getParent()))
      {
        out.write(printColumnReference("", weakAssociation, false));
      }
    }
  }

}
