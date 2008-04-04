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


import schemacrawler.SchemaCrawlerException;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
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
   * @see schemacrawler.CrawlHandler#end()
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
    super.end();
  }

  @Override
  String getArrow()
  {
    return " --> ";
  }

  @Override
  void handleColumnDataTypeEnd()
  {
    out.println();
  }

  @Override
  void handleColumnDataTypesEnd()
  {
    out.println();
    out.println();
  }

  @Override
  void handleColumnDataTypesStart()
  {
  }

  @Override
  void handleColumnDataTypeStart()
  {
  }

  @Override
  void handleDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    FormatUtils.printDatabaseInfo(databaseInfo, out);
  }

  @Override
  void handleDatabasePropertiesEnd()
  {
    out.println();
    out.println();
  }

  @Override
  void handleDatabasePropertiesStart()
  {
  }

  @Override
  void handleDriverPropertiesEnd()
  {
    out.println();
    out.println();
  }

  @Override
  void handleDriverPropertiesStart()
  {
    out.println();
  }

  @Override
  void handleJdbcDriverInfo(final JdbcDriverInfo driverInfo)
  {
    FormatUtils.printJdbcDriverInfo(driverInfo, out);
  }

  @Override
  void handleJdbcDriverPropertyEnd()
  {
    out.println();
  }

  @Override
  void handleJdbcDriverPropertyStart()
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleProcedureEnd()
   */
  @Override
  void handleProcedureEnd()
  {
    out.println();
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
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleTableEnd()
   */
  @Override
  void handleTableEnd()
  {
    if (getSchemaTextDetailType() != SchemaTextDetailType.brief_schema)
    {
      out.println();
      out.println();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleTableStart()
   */
  @Override
  void handleTableStart()
  {
  }

}
