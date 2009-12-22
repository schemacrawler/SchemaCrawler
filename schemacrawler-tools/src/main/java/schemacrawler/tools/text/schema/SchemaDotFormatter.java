/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.CrawlHandler;
import schemacrawler.tools.text.util.HtmlFormattingHelper;
import schemacrawler.tools.text.util.PastelColor;
import schemacrawler.utility.MetaDataUtility;
import schemacrawler.utility.MetaDataUtility.Connectivity;
import sf.util.Utility;

final class SchemaDotFormatter
  implements CrawlHandler
{
  private static final Logger LOGGER = Logger
    .getLogger(SchemaDotFormatter.class.getName());

  private static final String NEWLINE = System.getProperty("line.separator");

  private final SchemaTextOptions options;
  private final PrintWriter out;
  private final Map<Schema, PastelColor> colorMap;
  private final StringBuilder graphInfo;

  /**
   * Text formatting of schema.
   * 
   * @param options
   *        Options for text formatting of schema
   */
  SchemaDotFormatter(final SchemaTextOptions options)
    throws SchemaCrawlerException
  {
    if (options == null)
    {
      throw new IllegalArgumentException("Options not provided");
    }
    this.options = options;

    out = options.getOutputOptions().openOutputWriter();

    colorMap = new HashMap<Schema, PastelColor>();
    graphInfo = new StringBuilder();
  }

  public void begin()
    throws SchemaCrawlerException
  {
    final String text = Utility.readFully(HtmlFormattingHelper.class
      .getResourceAsStream("/dot.header.txt"));
    out.println(text);
  }

  public void end()
    throws SchemaCrawlerException
  {
    out.println("}");
    out.flush();
    //
    options.getOutputOptions().closeOutputWriter(out);
    LOGGER.log(Level.FINE, "Wrote output, "
                           + options.getOutputOptions().getOutputFile());
  }

  public void handle(final ColumnDataType dataType)
    throws SchemaCrawlerException
  {
  }

  public void handle(final ColumnMap[] weakAssociations)
    throws SchemaCrawlerException
  {
    if (weakAssociations == null)
    {
      return;
    }

    for (final ColumnMap columnMap: weakAssociations)
    {
      final Column primaryKeyColumn = columnMap.getPrimaryKeyColumn();
      final Column foreignKeyColumn = columnMap.getForeignKeyColumn();
      out.write(printColumnAssociation("", primaryKeyColumn, foreignKeyColumn));
    }
  }

  public void handle(final DatabaseInfo databaseInfo)
    throws SchemaCrawlerException
  {
    graphInfo.append("        <tr>").append(NEWLINE);
    graphInfo.append("          <td align=\"right\">Database:</td>")
      .append(NEWLINE);
    graphInfo.append("          <td align=\"left\">"
                     + databaseInfo.getProductName() + "  "
                     + databaseInfo.getProductVersion() + "</td>")
      .append(NEWLINE);
    graphInfo.append("        </tr>").append(NEWLINE);
  }

  public void handle(final JdbcDriverInfo jdbcDriverInfo)
    throws SchemaCrawlerException
  {
    graphInfo.append("        <tr>").append(NEWLINE);
    graphInfo.append("          <td align=\"right\">JDBC Connection:</td>")
      .append(NEWLINE);
    graphInfo.append("          <td align=\"left\">"
                     + jdbcDriverInfo.getConnectionUrl() + "</td>")
      .append(NEWLINE);
    graphInfo.append("        </tr>").append(NEWLINE);

    graphInfo.append("        <tr>").append(NEWLINE);
    graphInfo.append("          <td align=\"right\">JDBC Driver:</td>")
      .append(NEWLINE);
    graphInfo.append("          <td align=\"left\">"
                     + jdbcDriverInfo.getDriverName() + "  "
                     + jdbcDriverInfo.getDriverVersion() + "</td>")
      .append(NEWLINE);
    graphInfo.append("        </tr>").append(NEWLINE);

    graphInfo.append("      </table>");

    final String graphLabel = String
      .format("  graph [%n    label=<%n%s    >%n    labeljust=r%n    labelloc=b%n  ];%n%n",
              graphInfo.toString());
    out.println(graphLabel);
  }

  public void handle(final Procedure procedure)
    throws SchemaCrawlerException
  {
    // No-op
  }

  public void handle(final SchemaCrawlerInfo schemaCrawlerInfo)
    throws SchemaCrawlerException
  {
    graphInfo
      .append("      <table border=\"1\" cellborder=\"0\" cellspacing=\"0\">")
      .append(NEWLINE);

    graphInfo.append("        <tr>").append(NEWLINE);
    graphInfo.append("          <td colspan=\"2\" align=\"left\">Generated by "
                     + schemaCrawlerInfo.getSchemaCrawlerProductName() + " "
                     + schemaCrawlerInfo.getSchemaCrawlerVersion() + "</td>")
      .append(NEWLINE);
    graphInfo.append("        </tr>").append(NEWLINE);

  }

  public void handle(final Table table)
    throws SchemaCrawlerException
  {
    final Schema schema = table.getSchema();
    if (!colorMap.containsKey(schema))
    {
      colorMap.put(schema, new PastelColor());
    }
    final PastelColor bgcolor = colorMap.get(schema);
    final PastelColor tableBgColor = bgcolor.shade();
    final StringBuilder buffer = new StringBuilder();
    final String tableName = table.getFullName();
    buffer.append("  \"" + tableName + "\" [").append(NEWLINE)
      .append("    label=<").append(NEWLINE);
    buffer
      .append("      <table border=\"1\" cellborder=\"0\" cellspacing=\"0\">")
      .append(NEWLINE);
    buffer.append("        <tr>").append(NEWLINE);

    buffer.append("          <td colspan=\"2\" bgcolor=\"" + tableBgColor
                  + "\" align=\"left\">" + tableName + "</td>").append(NEWLINE);
    buffer.append("          <td bgcolor=\"" + tableBgColor
                  + "\" align=\"right\">"
                  + (table instanceof View? "[view]": "[table]") + "</td>")
      .append(NEWLINE);
    buffer.append("        </tr>").append(NEWLINE);
    for (final Column column: table.getColumns())
    {
      final String columnName = column.getName();
      final PastelColor columnBgcolor;
      if (column.isPartOfPrimaryKey())
      {
        columnBgcolor = bgcolor;
      }
      else
      {
        columnBgcolor = bgcolor.tint();
      }
      buffer.append("        <tr>").append(NEWLINE);
      buffer.append("          <td port=\"" + columnName
                    + ".start\" bgcolor=\"" + columnBgcolor
                    + "\" align=\"left\">" + columnName + "</td>")
        .append(NEWLINE);
      buffer.append("          <td bgcolor=\"" + columnBgcolor + "\"> </td>")
        .append(NEWLINE);
      buffer.append("          <td port=\"" + columnName
                    + ".end\" align=\"right\" bgcolor=\"" + columnBgcolor
                    + "\">" + column.getType().getDatabaseSpecificTypeName()
                    + column.getWidth() + "</td>").append(NEWLINE);
      buffer.append("        </tr>").append(NEWLINE);
    }
    buffer.append("      </table>").append(NEWLINE);
    buffer.append("    >").append(NEWLINE).append("  ];").append(NEWLINE);

    for (final ForeignKey foreignKey: table.getForeignKeys())
    {
      for (final ForeignKeyColumnMap foreignKeyColumnMap: foreignKey
        .getColumnPairs())
      {
        final Column primaryKeyColumn = foreignKeyColumnMap
          .getPrimaryKeyColumn();
        final Column foreignKeyColumn = foreignKeyColumnMap
          .getForeignKeyColumn();
        if (primaryKeyColumn.getParent().equals(table))
        {
          buffer.append(printColumnAssociation(foreignKey.getName(),
                                               primaryKeyColumn,
                                               foreignKeyColumn));
        }
      }
    }

    buffer.append(NEWLINE).append(NEWLINE);
    out.write(buffer.toString());
  }

  private String printColumnAssociation(final String associationName,
                                        final Column primaryKeyColumn,
                                        final Column foreignKeyColumn)
  {
    final Connectivity connectivity = MetaDataUtility
      .getConnectivity(foreignKeyColumn);
    final String pkSymbol = "teetee";
    final String fkSymbol;
    if (connectivity != null)
    {
      switch (connectivity)
      {
        case OneToOne:
          fkSymbol = "teeodot";
          break;
        case OneToMany:
          fkSymbol = "crowodot";
          break;
        default:
          fkSymbol = "none";
          break;
      }
    }
    else
    {
      fkSymbol = "none";
    }
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
      .format("  \"%s\":\"%s.start\":w -> \"%s\":\"%s.end\":e [label=\"%s\" style=\"%s\" arrowhead=\"%s\" arrowtail=\"%s\"];%n",
              primaryKeyColumn.getParent().getFullName(),
              primaryKeyColumn.getName(),
              foreignKeyColumn.getParent().getFullName(),
              foreignKeyColumn.getName(),
              associationName,
              style,
              fkSymbol,
              pkSymbol);
  }

}
