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
import schemacrawler.tools.util.FormatUtils.HtmlTable;
import schemacrawler.tools.util.FormatUtils.TableRow;
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
      out.println(FormatUtils.HTML_FOOTER);
      out.flush();
    }
    super.end();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleCheckConstraintName(int, String,
   *      String)
   */
  void handleCheckConstraintName(final int ordinalNumber, final String name,
                                 final String definition)
  {
    String nameValue = "";
    if (isShowConstraintNames())
    {
      nameValue = name;
    }
    out.println(createEmptyRow());
    out.println(createNameRow(nameValue, "[check constraint]"));
    out.println(createDefinitionRow(definition));
  }

  private TableRow createEmptyRow()
  {
    return new FormatUtils.TableRow(4);
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleColumn(int, String, String,
   *      String)
   */
  void handleColumn(final int ordinalNumber, final String name,
                    final String type, final String symbol)
  {
    String ordinalNumberString = "";
    if (isShowOrdinalNumbers())
    {
      ordinalNumberString = String.valueOf(ordinalNumber);
    }
    out.println(createDetailRow(ordinalNumberString, name, type, symbol));
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
    final String userDefined = negate(columnDataType.isUserDefined(),
                                      "user defined");
    final String nullable = negate(columnDataType.isNullable(), "nullable");
    final String autoIncrementable = negate(columnDataType
      .isAutoIncrementable(), "auto-incrementable");
    final String definedWith = makeDefinedWithString(columnDataType);

    HtmlTable htmlTable = new HtmlTable();
    htmlTable.addRow(createNameRow(databaseSpecificTypeName, "[data type]"));
    htmlTable.addRow(createDefinitionRow("based on " + typeName));
    htmlTable.addRow(createDefinitionRow(userDefined));
    htmlTable.addRow(createDefinitionRow(definedWith));
    htmlTable.addRow(createDefinitionRow(nullable));
    htmlTable.addRow(createDefinitionRow(autoIncrementable));
    htmlTable.addRow(createDefinitionRow(columnDataType.getSearchable()
      .toString()));

    out.println(htmlTable);
    out.println("<p></p>");

  }

  void handleColumnDataTypesEnd()
  {

  }

  void handleColumnDataTypesStart()
  {

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

  void handleDatabasePropertiesEnd()
  {
    out.println("</table>");
    out.println("<p></p>");
  }

  void handleDatabasePropertiesStart()
  {
    out.println("<table>");
    out.println();
  }

  void handleDatabaseProperty(final String name, final String value)
  {
    out.println(createNameRow(name, Entities.HTML40.escape(value)));
  }

  void handleDefinition(final String definition)
  {
    if (Utilities.isBlank(definition))
    {
      return;
    }
    out.println(createDefinitionRow(definition));
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleForeignKeyColumnPair(String,
   *      String, int)
   */
  void handleForeignKeyColumnPair(final String mapping, final int keySequence)
  {
    String keySequenceString = "";
    if (isShowOrdinalNumbers())
    {
      keySequenceString = Utilities.padLeft(String.valueOf(keySequence), 2);
    }
    out.println(createDetailRow(keySequenceString, mapping, "", ""));
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleForeignKeyName(int, String,
   *      String)
   */
  void handleForeignKeyName(final int ordinalNumber, final String name,
                            final String updateRule)
  {
    out.println(createEmptyRow());

    String fkName = "";
    if (isShowConstraintNames())
    {
      fkName = name;
    }    
    final String fkDetails = "[foreign key" + ", on update " + updateRule + "]";
    out.println(createNameRow(fkName, fkDetails));

  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleIndexName(int, String, String,
   *      boolean, String)
   */
  void handleIndexName(final int ordinalNumber, final String name,
                       final String type, final boolean unique,
                       final String sortSequence)
  {
    out.println();
    out.print(createEmptyRow());
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    if (isShowConstraintNames())
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
    out.print(createEmptyRow());
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    if (isShowConstraintNames())
    {
      out.print(FormatUtils.htmlBold(name));
    }
    out.print(FIELD_END + FIELD_BEGIN_2);
    out.print(FormatUtils.htmlAlignRight("[primary key]"));
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();
  }

  void handlePrivilege(int i, String name, String privilegeType,
                       String grantedFrom)
  {
    out.println();
    out.print(createEmptyRow());
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    if (isShowConstraintNames())
    {
      out.print(FormatUtils.htmlBold(name));
    }
    out.print(FIELD_END);
    out.print(FIELD_BEGIN_2);
    out.print(FormatUtils.htmlAlignRight("[" + privilegeType + "]"));
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();

    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN);
    out.print(FIELD_SEPARATOR);
    out.print(grantedFrom);
    out.print(FIELD_SEPARATOR);
    out.print(FIELD_SEPARATOR);
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleProcedureColumn(int, String,
   *      String, String)
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
   * @see BaseSchemaTextFormatter#handleProcedureName(int, String,
   *      String)
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

  void handleTrigger(int i, String name, String triggerType,
                     String actionCondition, String actionStatement)
  {
    out.println();
    out.print(createEmptyRow());
    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    if (isShowConstraintNames())
    {
      out.print(FormatUtils.htmlBold(name));
    }
    out.print(FIELD_END + FIELD_BEGIN_2);
    out.print(FormatUtils.htmlAlignRight(triggerType));
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();

    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    out.print(FIELD_END + FIELD_BEGIN_2);
    final String actionConditionValue = actionCondition == null
                                                               ? ""
                                                               : actionCondition;
    out.print(actionConditionValue);
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();

    out.print(RECORD_BEGIN);
    out.print(FIELD_BEGIN_2);
    out.print(FIELD_END + FIELD_BEGIN_2);
    final String actionStatementValue = actionStatement == null
                                                               ? ""
                                                               : actionStatement;
    out.print(actionStatementValue);
    out.print(FIELD_END);
    out.print(RECORD_END);
    out.println();
  }

  private FormatUtils.TableRow createDefinitionRow(final String definition)
  {
    FormatUtils.TableRow row = new FormatUtils.TableRow();
    row.addCell(new FormatUtils.TableCell("ordinal", ""));
    row.addCell(new FormatUtils.TableCell(3, "definition", definition));
    return row;
  }

  private FormatUtils.TableRow createDetailRow(String ordinal,
                                               final String name,
                                               final String type,
                                               final String remarks)
  {
    FormatUtils.TableRow row;
    row = new FormatUtils.TableRow();
    row.addCell(new FormatUtils.TableCell("ordinal", ordinal));
    row.addCell(new FormatUtils.TableCell("", name));
    row.addCell(new FormatUtils.TableCell("", type));
    row.addCell(new FormatUtils.TableCell("", remarks));
    return row;
  }

  private FormatUtils.TableRow createNameRow(final String name,
                                             final String description)
  {
    FormatUtils.TableRow row;
    row = new FormatUtils.TableRow();
    row.addCell(new FormatUtils.TableCell(2, "name", name));
    row.addCell(new FormatUtils.TableCell(2, "description", description));
    return row;
  }

}
