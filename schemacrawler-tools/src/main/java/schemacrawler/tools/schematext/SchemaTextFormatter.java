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
import schemacrawler.tools.util.PlainTextFormattingHelper;

/**
 * Formats the schema as plain text for output.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaTextFormatter
  extends BaseSchemaTextFormatter
{

  /**
   * Formats the schema as plain text for output.
   * 
   * @param options
   *        Options
   * @exception SchemaCrawlerException
   *            On an exception
   */
  public SchemaTextFormatter(final SchemaTextOptions options)
    throws SchemaCrawlerException
  {
    super(options, new PlainTextFormattingHelper(options.getOutputOptions()
      .getOutputFormat().name()));
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
      out.println();
      out.println(getTableCount() + " tables.");
    }
    out.flush();
    super.end();
  }

  @Override
  void handleDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    FormatUtils.printDatabaseInfo(databaseInfo, out);
  }

  @Override
  void handleJdbcDriverInfo(final JdbcDriverInfo driverInfo)
  {
    FormatUtils.printJdbcDriverInfo(driverInfo, out);
  }

}
