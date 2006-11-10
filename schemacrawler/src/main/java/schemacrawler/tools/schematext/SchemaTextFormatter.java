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
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.util.CsvFormattingFunctor;
import schemacrawler.tools.util.FormatUtils;
import schemacrawler.tools.util.PlainTextFormattingFunctor;
import schemacrawler.tools.util.TextFormattingFunctor;
import sf.util.Utilities;

/**
 * Formats the schema as plain text for output.
 * 
 * @author sfatehi
 */
public final class SchemaTextFormatter
  extends BaseSchemaTextFormatter
{

  private final TextFormattingFunctor textFormattingFunctor;

  /**
   * Formats the schema as plain text for output.
   * 
   * @param options
   *        Options
   */
  SchemaTextFormatter(final SchemaTextOptions options)
    throws SchemaCrawlerException
  {
    super(options);
    if (options.getOutputOptions().getOutputFormat() == OutputFormat.CSV)
    {
      textFormattingFunctor = new CsvFormattingFunctor();
    }
    else
    {
      textFormattingFunctor = new PlainTextFormattingFunctor();
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
      out.println();
      out.println(getTableCount() + " tables.");
    }
    super.end();
  }

  String createDefinitionRow(final String definition)
  {
    StringBuffer row = new StringBuffer();
    row.append(textFormattingFunctor.getFieldSeparator());
    row.append(definition);
    return row.toString();
  }

  String createDetailRow(String ordinal, final String subName,
                         final String type, final String remarks)
  {
    final int REMARKS_WIDTH = 5;
    final int SUB_NAME_WIDTH = 32;
    final int TYPE_WIDTH = 23;

    StringBuffer row = new StringBuffer();
    out.print(textFormattingFunctor.getFieldSeparator());
    if (!Utilities.isBlank(ordinal))
    {
      row.append(textFormattingFunctor.format(ordinal, 2, true));
      row.append(textFormattingFunctor.getFieldSeparator());
    }
    row.append(textFormattingFunctor.format(subName, SUB_NAME_WIDTH, true));
    row.append(textFormattingFunctor.getFieldSeparator());
    row.append(textFormattingFunctor.format(type, TYPE_WIDTH, true));
    row.append(textFormattingFunctor.getFieldSeparator());
    row.append(textFormattingFunctor.format(remarks, REMARKS_WIDTH, true));
    return row.toString();
  }

  String createEmptyRow()
  {
    return "";
  }

  String createNameRow(final String name, final String description)
  {
    final int NAME_WIDTH = 36;
    final int DESCRIPTION_WIDTH = 34;

    StringBuffer row = new StringBuffer();
    row.append(textFormattingFunctor.format(name, NAME_WIDTH, true));
    row.append(textFormattingFunctor.getFieldSeparator());
    row.append(textFormattingFunctor.format(description, DESCRIPTION_WIDTH,
                                            false));
    return row.toString();
  }

  String createNameValueRow(final String name, final String value)
  {
    final int NAME_WIDTH = 36;

    StringBuffer row = new StringBuffer();
    row.append(textFormattingFunctor.format(name, NAME_WIDTH, true));
    row.append(textFormattingFunctor.getFieldSeparator());
    row.append(value);
    return row.toString();
  }

  String createSeparatorRow()
  {
    return Utilities.repeat("-", FormatUtils.MAX_LINE_LENGTH);
  }

  String getArrow()
  {
    return " --> ";
  }

  void handleColumnDataTypeEnd()
  {
    out.println();
    out.println();
  }

  void handleColumnDataTypesEnd()
  {
    out.println();
    out.println();
  }

  void handleColumnDataTypesStart()
  {
  }

  void handleColumnDataTypeStart()
  {
  }

  void handleDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    FormatUtils.printDatabaseInfo(databaseInfo, out);
  }

  void handleDatabasePropertiesEnd()
  {
    out.println();
    out.println();
  }

  void handleDatabasePropertiesStart()
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleProcedureEnd()
   */
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
  void handleProcedureStart()
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleStartTableColumns()
   */
  void handleStartTableColumns()
  {
    createSeparatorRow();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleTableEnd()
   */
  void handleTableEnd()
  {
    if (getSchemaTextDetailType() != SchemaTextDetailType.BRIEF)
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
  void handleTableStart()
  {
  }

}
