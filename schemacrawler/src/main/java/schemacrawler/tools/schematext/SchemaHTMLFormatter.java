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


import java.util.Locale;

import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.tools.util.FormatUtils;
import sf.util.Entities;
import sf.util.Utilities;

/**
 * Formats the schema as HTML for output.
 * 
 * @author sfatehi
 */
public final class SchemaHTMLFormatter
  extends BaseSchemaTextFormatter
{

  private static final String FIELD_BEGIN_2 = "<td colspan='2'>";
  private static final String RECORD_END = "</tr>" + Utilities.NEWLINE;
  private static final String RECORD_BEGIN = "  <tr>";
  private static final String RECORD_EMPTY = "  <tr><td colspan='4'>&nbsp;</td></tr>"
                                             + Utilities.NEWLINE;
  private static final String FIELD_BEGIN = "<td>";
  private static final String FIELD_END = "</td>" + Utilities.NEWLINE;
  private static final String FIELD_SEPARATOR = "</td>" + Utilities.NEWLINE
                                                + "<td>";
  private static final String FIELD_EMPTY = "<td></td>" + Utilities.NEWLINE;

  /**
   * Formats the schema as HTML for output.
   * 
   * @param options
   *          Options
   * @param writer
   *          Writer to output to
   */
  SchemaHTMLFormatter(final SchemaTextOptions options)
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
      out.println(FormatUtils.HTML_FOOTER);
      out.flush();
    }
    super.end();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.CrawlHandler#handle(schemacrawler.schema.DatabaseInfo)
   */
  void handleDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    if (!getNoInfo())
    {
      out.println("<pre id='databaseInfo'>");
      out.println(databaseInfo);
      out.println("</pre>");
      out.flush();
    }
  }

  void handleColumnDataTypesStart()
  {

  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleColumnDataType(schemacrawler.schema.ColumnDataType)
   */
  void handleColumnDataType(final ColumnDataType columnDataType)
  {
    final String databaseSpecificTypeName = columnDataType
      .getDatabaseSpecificTypeName();
    final String typeName = columnDataType.getTypeName();
    String userDefined = negate(columnDataType.isUserDefined(), "user defined");
    String nullable = negate(columnDataType.isNullable(), "nullable");
    String autoIncrementable = negate(columnDataType.isAutoIncrementable(),
                                      "auto-incrementable");
    String definedWith = makeDefinedWithString(columnDataType);

    out.println("<table>");
    out.println();

    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    out.print(FormatUtils.htmlBold(databaseSpecificTypeName));
    out.print(FIELD_SEPARATOR);
    out.print(FormatUtils.htmlAlignRight("[data type]"));
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();

    printColumnDataTypeProperty("based on " + typeName);
    printColumnDataTypeProperty(userDefined);
    printColumnDataTypeProperty(definedWith);
    printColumnDataTypeProperty(nullable);
    printColumnDataTypeProperty(autoIncrementable);
    printColumnDataTypeProperty(columnDataType.getSearchable().toString());

    out.println("</table>");
    out.println("<p></p>");

  }

  private void printColumnDataTypeProperty(String userDefined)
  {
    out.print(RECORD_BEGIN);
    out.print(FIELD_EMPTY);
    out.print(FIELD_BEGIN);
    out.print(userDefined);
    out.print(FIELD_END);
    out.print(FIELD_EMPTY);
    out.print(RECORD_END);
    out.println();
  }

  void handleColumnDataTypesEnd()
  {

  }

  void handleDatabasePropertiesStart()
  {
    out.println("<table>");
    out.println();
  }

  void handleDatabaseProperty(final String name, final String value)
  {
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN);
    out.print(name);
    out.print(FIELD_END + FIELD_BEGIN);
    out.print(Entities.HTML40.escape(value));
    out.print(FIELD_END);
    out.print(RECORD_END);
  }

  void handleDatabasePropertiesEnd()
  {
    out.println("</table>");
    out.println("<p></p>");
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleColumn(int, String, String, String)
   */
  void handleColumn(final int ordinalNumber, final String name,
                    final String type, final String symbol)
  {
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN);
    if (isShowOrdinalNumbers())
    {
      final String ordinalNumberString = String.valueOf(ordinalNumber);
      out.print(ordinalNumberString);
    }
    out.print(FIELD_SEPARATOR);
    out.print(name);
    out.print(FIELD_SEPARATOR);
    out.print(type);
    out.print(FIELD_SEPARATOR);
    out.print(symbol);
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleForeignKeyColumnPair(String, String,
   *      int)
   */
  void handleForeignKeyColumnPair(final String pkColumnName,
                                  final String fkColumnName,
                                  final int keySequence)
  {
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN);
    if (isShowOrdinalNumbers())
    {
      final String keySequenceString = String.valueOf(keySequence);
      out.print(Utilities.padLeft(keySequenceString, 2));
    }
    out.print(FIELD_SEPARATOR);
    final String mapping = pkColumnName + " --> " + fkColumnName;
    out.print(mapping);
    out.print(FIELD_SEPARATOR);
    out.print(FIELD_SEPARATOR);
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleForeignKeyName(int, String, String)
   */
  void handleForeignKeyName(final int ordinalNumber, final String name,
                            final String updateRule)
  {
    out.println();
    out.print(RECORD_EMPTY);
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    if (isShowIndexNames())
    {
      out.print(FormatUtils.htmlBold(name));
    }
    out.print(FIELD_END);
    out.print(FIELD_BEGIN_2);
    final String fkDetails = "[foreign key" + ", on update " + updateRule + "]";
    out.print(FormatUtils.htmlAlignRight(fkDetails));
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleIndexName(int, String, String, boolean,
   *      String)
   */
  void handleIndexName(final int ordinalNumber, final String name,
                       final String type, final boolean unique,
                       final String sortSequence)
  {
    out.println();
    out.print(RECORD_EMPTY);
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    if (isShowIndexNames())
    {
      out.print(FormatUtils.htmlBold(name));
    }
    out.print(FIELD_END + FIELD_BEGIN_2);
    final String indexDetails = "[" + (unique? "": "non-") + "unique "
                                + sortSequence + " " + type + " " + "index]";
    out.print(FormatUtils.htmlAlignRight(indexDetails));
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handlePrimaryKeyName(String)
   */
  void handlePrimaryKeyName(final String name)
  {
    out.println();
    out.print(RECORD_EMPTY);
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    if (isShowIndexNames())
    {
      out.print(FormatUtils.htmlBold(name));
    }
    out.print(FIELD_END + FIELD_BEGIN_2);
    out.print(FormatUtils.htmlAlignRight("[primary key]"));
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleProcedureColumn(int, String, String,
   *      String)
   */
  void handleProcedureColumn(final int ordinalNumber, final String name,
                             final String type, final String procedureColumnType)
  {
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN);
    if (isShowOrdinalNumbers())
    {
      out.print(String.valueOf(ordinalNumber));
    }
    out.print(FIELD_SEPARATOR);
    out.print(name);
    out.print(FIELD_SEPARATOR);
    out.print(type);
    out.print(FIELD_SEPARATOR);
    out.print(procedureColumnType);
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();
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
   * @see BaseSchemaTextFormatter#handleProcedureName(int, String, String)
   */
  void handleProcedureName(final int ordinalNumber, final String name,
                           final String type)
  {
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    out.print(FormatUtils.htmlBold(name));
    out.print(FIELD_END + FIELD_BEGIN_2);
    out.print(FormatUtils.htmlAlignRight("[" + type + "]"));
    out.print(FIELD_END);
    out.print(RECORD_END);
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
   * @see BaseSchemaTextFormatter#handleStartTableColumns()
   */
  void handleStartTableColumns()
  {

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
   * @see BaseSchemaTextFormatter#handleTableName(int, String, String)
   */
  void handleTableName(final int ordinalNumber, final String name,
                       final String type)
  {
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    out.print(FormatUtils.htmlBold(name));
    out.print(FIELD_END + FIELD_BEGIN_2);
    final String typeBracketed = "[" + type.toLowerCase(Locale.ENGLISH) + "]";
    out.print(FormatUtils.htmlAlignRight(typeBracketed));
    out.print(FIELD_END);
    out.print(RECORD_END);
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
