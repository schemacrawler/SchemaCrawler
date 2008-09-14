/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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


import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schemacrawler.SchemaCrawlerException;
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
   * @see schemacrawler.schemacrawler.CrawlHandler#begin()
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
   * @see schemacrawler.schemacrawler.CrawlHandler#end()
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
  void handleDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    out.println("<pre id=\'databaseInfo\'>");
    FormatUtils.printDatabaseInfo(databaseInfo, out);
    out.println("</pre>");
  }

  @Override
  void handleJdbcDriverInfo(final JdbcDriverInfo driverInfo)
  {
    out.println("<pre id=\'driverInfo\'>");
    FormatUtils.printJdbcDriverInfo(driverInfo, out);
    out.println("</pre>");
  }

}
