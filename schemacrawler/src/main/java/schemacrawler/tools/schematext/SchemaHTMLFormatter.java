/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.tools.util.FormatUtils;
import schemacrawler.tools.util.HtmlFormattingHelper;

/**
 * Formats the schema as HTML for output.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaHTMLFormatter
  extends BaseSchemaTextFormatter
{

  /**
   * Formats the schema as HTML for output.
   * 
   * @param options
   *        Options
   * @exception SchemaCrawlerException
   *            On an exception
   */
  public SchemaHTMLFormatter(final SchemaTextOptions options)
    throws SchemaCrawlerException
  {
    super(options, new HtmlFormattingHelper());
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.CrawlHandler#begin()
   */
  @Override
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
  @Override
  public void end()
    throws SchemaCrawlerException
  {
    if (!getNoFooter())
    {
      out.println("<p id='tableCount'>" + getTableCount() + " tables" + "</p>");
    }
    out.println(FormatUtils.HTML_FOOTER);
    out.flush();
    super.end();
  }

  @Override
  String getArrow()
  {
    return " &rarr; ";
  }

  @Override
  void handleColumnDataTypeEnd()
  {
    out.println(HtmlFormattingHelper.createTableEnd());
  }

  @Override
  void handleColumnDataTypesEnd()
  {

  }

  @Override
  void handleColumnDataTypesStart()
  {

  }

  @Override
  void handleColumnDataTypeStart()
  {
    out.println(HtmlFormattingHelper.createTableStart());
  }

  @Override
  void handleDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    out.println("<pre id=\'databaseInfo\'>");
    FormatUtils.printDatabaseInfo(databaseInfo, out);
    out.println("</pre>");
  }

  @Override
  void handleDatabasePropertiesEnd()
  {
    out.println(HtmlFormattingHelper.createTableEnd());
    out.println();
  }

  @Override
  void handleDatabasePropertiesStart()
  {
    out.println(HtmlFormattingHelper.createTableStart());
  }

  @Override
  void handleDriverPropertiesEnd()
  {
    out.println(HtmlFormattingHelper.createTableEnd());
  }

  @Override
  void handleDriverPropertiesStart()
  {
    out.println(HtmlFormattingHelper.createTableStart());
  }

  @Override
  void handleJdbcDriverInfo(final JdbcDriverInfo driverInfo)
  {
    out.println("<pre id=\'driverInfo\'>");
    FormatUtils.printJdbcDriverInfo(driverInfo, out);
    out.println("</pre>");
  }

  @Override
  void handleJdbcDriverPropertyEnd()
  {
    out.println(HtmlFormattingHelper.createTableEnd());
  }

  @Override
  void handleJdbcDriverPropertyStart()
  {
    out.println(HtmlFormattingHelper.createTableStart());
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleProcedureEnd()
   */
  @Override
  void handleProcedureEnd()
  {
    out.println(HtmlFormattingHelper.createTableEnd());
    out.println();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleProcedureStart()
   */
  @Override
  void handleProcedureStart()
  {
    out.println(HtmlFormattingHelper.createTableStart());
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleTableEnd()
   */
  @Override
  void handleTableEnd()
  {
    out.println(HtmlFormattingHelper.createTableEnd());
    out.println();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleTableStart()
   */
  @Override
  void handleTableStart()
  {
    out.println(HtmlFormattingHelper.createTableStart());
  }

}
