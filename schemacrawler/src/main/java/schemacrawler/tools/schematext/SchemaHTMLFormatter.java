/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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

package schemacrawler.tools.schematext;


import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.tools.util.FormatUtils;
import schemacrawler.tools.util.HtmlTableCell;
import schemacrawler.tools.util.HtmlTableRow;

/**
 * Formats the schema as HTML for output.
 * 
 * @author sfatehi
 */
public final class SchemaHTMLFormatter
  extends BaseSchemaTextFormatter
{
  /**
   * Formats the schema as HTML for output.
   * 
   * @param options
   *        Options
   * @param writer
   *        Writer to output to
   */
  SchemaHTMLFormatter(final SchemaTextOptions options)
    throws SchemaCrawlerException
  {
    super(options);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.CrawlHandler#begin()
   */
  public void begin()
    throws SchemaCrawlerException
  {
    if (!getNoHeader())
    {
      out.println(FormatUtils.HTML_HEADER);
      out.flush();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.CrawlHandler#end()
   */
  public void end()
    throws SchemaCrawlerException
  {
    if (!getNoFooter())
    {
      out.println("<pre id='tableCount'>" + getTableCount() + " tables"
                  + "</pre>");
    }
    out.println(FormatUtils.HTML_FOOTER);
    out.flush();
    super.end();
  }

  String createDefinitionRow(final String definition)
  {
    HtmlTableRow row = new HtmlTableRow();
    row.addCell(new HtmlTableCell("ordinal", ""));
    row.addCell(new HtmlTableCell(3, "definition", definition));
    return row.toString();
  }

  String createDetailRow(String ordinal, final String subName,
                         final String type, final String remarks)
  {
    HtmlTableRow row;
    row = new HtmlTableRow();
    row.addCell(new HtmlTableCell("ordinal", ordinal));
    row.addCell(new HtmlTableCell("subname", subName));
    row.addCell(new HtmlTableCell("type", type));
    row.addCell(new HtmlTableCell("remarks", remarks));
    return row.toString();
  }

  String createEmptyRow()
  {
    return new HtmlTableRow(4).toString();
  }

  String createNameRow(final String name, final String description)
  {
    HtmlTableRow row;
    row = new HtmlTableRow();
    row.addCell(new HtmlTableCell(2, "name", name));
    row.addCell(new HtmlTableCell(2, "description", description));
    return row.toString();
  }

  String createNameValueRow(final String name, final String value)
  {
    HtmlTableRow row;
    row = new HtmlTableRow();
    row.addCell(new HtmlTableCell("", name));
    row.addCell(new HtmlTableCell("", value));
    return row.toString();
  }

  String createSeparatorRow()
  {
    return "<tr><td colspan='4'><hr/></td></tr>";
  }

  String getArrow()
  {
    return " &rarr; ";
  }

  void handleColumnDataTypeEnd()
  {
    out.println("</table>");
    out.println("<p></p>");
  }

  void handleColumnDataTypesEnd()
  {

  }

  void handleColumnDataTypesStart()
  {

  }

  void handleColumnDataTypeStart()
  {
    out.println("<table>");
  }

  void handleDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    out.println("<pre>");
    FormatUtils.printDatabaseInfo(databaseInfo, out);
    out.println("</pre>");
  }

  void handleDatabasePropertiesEnd()
  {
    out.println("</table>");
    out.println("<p></p>");
    out.println();
  }

  void handleDatabasePropertiesStart()
  {
    out.println("<table>");
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleProcedureEnd()
   */
  void handleProcedureEnd()
  {
    out.println("</table>");
    out.println("<p></p>");
    out.println();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleProcedureStart()
   */
  void handleProcedureStart()
  {
    out.println("<table>");
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleTableEnd()
   */
  void handleTableEnd()
  {
    out.println("</table>");
    out.println("<p></p>");
    out.println();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleTableStart()
   */
  void handleTableStart()
  {
    out.println("<table>");
  }

}
