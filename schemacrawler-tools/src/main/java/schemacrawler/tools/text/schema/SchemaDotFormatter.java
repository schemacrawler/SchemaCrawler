/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.tools.text.schema;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.analysis.associations.DatabaseWithAssociations;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseDotFormatter;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.utility.MetaDataUtility;
import schemacrawler.utility.MetaDataUtility.Connectivity;
import schemacrawler.utility.NamedObjectSort;
import sf.util.Utility;

/**
 * GraphViz DOT formatting of schema.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaDotFormatter
  extends BaseDotFormatter<SchemaTextOptions>
  implements SchemaTraversalHandler
{

  private final boolean isVerbose;
  private final boolean isList;
  private final Map<Schema, String> colorMap;

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
    isList = schemaTextDetailType == SchemaTextDetailType.list;
    colorMap = new HashMap<Schema, String>();
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
    final Schema schema = table.getSchema();

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

    final String tableNameBgColor;
    if (!colorMap.containsKey(schema))
    {
      tableNameBgColor = Utility.pastelColorHTMLValue(schema.getFullName());
      colorMap.put(schema, tableNameBgColor);
    }
    else
    {
      tableNameBgColor = colorMap.get(schema);
    }

    out.append("  /* ").append(table.getFullName())
      .append(" -=-=-=-=-=-=-=-=-=-=-=-=-=- */").append(Utility.NEWLINE);
    out.append("  \"").append(nodeId(table)).append("\" [")
      .append(Utility.NEWLINE).append("    label=<").append(Utility.NEWLINE);
    out
      .append("      <table border=\"1\" cellborder=\"0\" cellspacing=\"0\" bgcolor=\"white\">")
      .append(Utility.NEWLINE);
    out.append("        <tr>").append(Utility.NEWLINE);

    out.append("          <td colspan=\"3\" bgcolor=\"")
      .append(tableNameBgColor).append("\" align=\"left\"><b>")
      .append(tableName).append("</b></td>").append(Utility.NEWLINE);

    out.append("          <td bgcolor=\"").append(tableNameBgColor)
      .append("\" align=\"right\">").append(tableType).append("</td>")
      .append(Utility.NEWLINE);
    out.append("        </tr>").append(Utility.NEWLINE);

    if (!isList)
    {
      final List<Column> columns = table.getColumns();
      printTableColumns(columns);
    }

    out.append("      </table>").append(Utility.NEWLINE);
    out.append("    >").append(Utility.NEWLINE).append("  ];")
      .append(Utility.NEWLINE);

    if (!isList)
    {
      out.write(Utility.NEWLINE);
      for (final ForeignKey foreignKey: table.getForeignKeys())
      {
        for (final ColumnReference columnReference: foreignKey
          .getColumnReferences())
        {
          if (table.equals(columnReference.getPrimaryKeyColumn().getParent()))
          {
            out.write(printColumnReference(foreignKey.getName(),
                                           columnReference));
          }
        }
      }

      if (isVerbose)
      {
        printWeakAssociations(table);
      }
    }
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
      final String nodeId = print(column);
      //
      portIds[0] = nodeId;
      portIds[1] = nodeId;
    }
    return portIds;
  }

  private String nodeId(final NamedObject namedObject)
  {
    if (namedObject == null)
    {
      return "";
    }
    else
    {
      return Utility.convertForComparison(namedObject.getName()) + "_"
             + Integer.toHexString(namedObject.getFullName().hashCode());
    }
  }

  private String print(final Column column)
  {
    final String nodeId = "\"" + nodeId(column) + "\"";
    final String columnNode = String.format("  %s [label=<%s>];%n",
                                            nodeId,
                                            column.getFullName());

    out.write(columnNode);

    return nodeId;
  }

  private String printColumnReference(final String associationName,
                                      final ColumnReference columnReference)
  {
    final Column primaryKeyColumn = columnReference.getPrimaryKeyColumn();
    final Column foreignKeyColumn = columnReference.getForeignKeyColumn();

    final String[] pkPortIds = getPortIds(primaryKeyColumn);
    final String[] fkPortIds = getPortIds(foreignKeyColumn);

    final Connectivity connectivity = MetaDataUtility
      .getConnectivity(foreignKeyColumn);
    final String pkSymbol = "teetee";
    final String fkSymbol = connectivity.arrowhead();
    final String style;
    if (Utility.isBlank(associationName))
    {
      style = "dashed";
    }
    else
    {
      style = "solid";
    }

    return String
      .format("  %s:w -> %s:e [label=<%s> style=\"%s\" dir=\"both\" arrowhead=\"%s\" arrowtail=\"%s\"];%n",
              pkPortIds[0],
              fkPortIds[1],
              options.isHideForeignKeyNames()? "": associationName,
              style,
              fkSymbol,
              pkSymbol);
  }

  private void printTableColumns(final List<Column> columns)
  {
    Collections.sort(columns, NamedObjectSort.getNamedObjectSort(options
      .isAlphabeticalSortForTableColumns()));
    for (final Column column: columns)
    {
      String columnTypeName = column.getColumnDataType()
        .getDatabaseSpecificTypeName();
      if (options.isShowStandardColumnTypeNames())
      {
        columnTypeName = column.getColumnDataType().getTypeName();
      }
      final String columnType = columnTypeName + column.getWidth();
      final String nullable = column.isNullable()? "": " not null";
      final String columnDetails = columnType + nullable;

      out.append("        <tr>").append(Utility.NEWLINE);
      out.append("          <td port=\"").append(nodeId(column))
        .append(".start\" align=\"right\">");
      if (options.isShowOrdinalNumbers())
      {
        final String ordinalNumberString = String.valueOf(column
          .getOrdinalPosition());
        out.append(ordinalNumberString);
      }
      out.append("</td>").append(Utility.NEWLINE)
        .append("          <td align=\"left\">");
      if (column.isPartOfPrimaryKey())
      {
        out.append("<b><i><u>");
      }
      out.append(column.getName());
      if (column.isPartOfPrimaryKey())
      {
        out.append("</u></i></b>");
      }
      out.append("</td>").append(Utility.NEWLINE);
      out.append("          <td> </td>").append(Utility.NEWLINE);
      out.append("          <td port=\"").append(nodeId(column))
        .append(".end\" align=\"right\">");
      out.append(columnDetails);
      out.append("</td>").append(Utility.NEWLINE);
      out.append("        </tr>").append(Utility.NEWLINE);
    }

  }

  private void printWeakAssociations(final Table table)
  {
    final Collection<ColumnReference> weakAssociations = DatabaseWithAssociations
      .getWeakAssociations(table);
    for (final ColumnReference weakAssociation: weakAssociations)
    {
      out.write(printColumnReference("", weakAssociation));
    }
  }

}
