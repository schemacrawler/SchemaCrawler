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

  private static final int MAX_COLUMN_MODIFIER_WIDTH = 5;
  private static final int MAX_COLUMN_NAME_WIDTH = 32;
  private static final int MAX_COLUMN_TYPE_WIDTH = 23;
  private static final int MAX_TABLE_NAME_WIDTH = 36;
  private static final int MAX_TABLE_TYPE_WIDTH = 30;

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
    } else
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

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleCheckConstraintName(int, String,
   *      String)
   */
  void handleCheckConstraintName(final int ordinalNumber, final String name,
      final String definition)
  {
    String constraintName = "";
    if (isShowConstraintNames())
    {
      constraintName = name;
    }
    out.println(createEmptyRow());
    out.println(createNameRow(constraintName, "[check constraint]"));
    out.println(createDefinitionRow(definition));
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

    out.print(textFormattingFunctor.format(databaseSpecificTypeName,
        MAX_COLUMN_TYPE_WIDTH, true));

    out.print(textFormattingFunctor.getFieldSeparator());
    out.print(textFormattingFunctor.format("[data type]",
        MAX_COLUMN_TYPE_WIDTH, false));

    printColumnDataTypeProperty("based on " + typeName, 40);
    printColumnDataTypeProperty(userDefined, 8);
    printColumnDataTypeProperty(definedWith, 23);
    printColumnDataTypeProperty(nullable, 8);
    printColumnDataTypeProperty(autoIncrementable, 8);
    printColumnDataTypeProperty(columnDataType.getSearchable().toString(), 0);

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

  void handleDatabasePropertiesEnd()
  {
    out.println();
    out.println();
  }

  void handleDatabasePropertiesStart()
  {
  }

  void handleDatabaseProperty(final String name, final String value)
  {
    out.println(createNameValueRow(name, value));
  }

  void handleDefinition(final String definition)
  {
    out.println(createEmptyRow());

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
      final String type, final boolean unique, final String sortSequence)
  {
    out.println(createEmptyRow());

    String indexName = "";
    if (isShowConstraintNames())
    {
      indexName = name;
    }
    final String indexDetails = "[" + (unique? "": "non-") + "unique "
        + sortSequence + " " + type + " " + "index]";
    out.println(createNameRow(indexName, indexDetails));
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handlePrimaryKeyName(String)
   */
  void handlePrimaryKeyName(final String name)
  {
    out.println(createEmptyRow());

    String pkName = "";
    if (isShowConstraintNames())
    {
      pkName = name;
    }
    out.println(createNameRow(pkName, "[primary key]"));
  }

  void handlePrivilege(int i, String name, String privilegeType,
      String grantedFrom)
  {
    out.println(createEmptyRow());

    String privilegeName = "";
    if (isShowConstraintNames())
    {
      privilegeName = name;
    }
    final String privilegeDetails = "[" + privilegeType + "]";
    out.println(createNameRow(privilegeName, privilegeDetails));

    out.println(createDetailRow("", grantedFrom, "", ""));
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
    String ordinalNumberString = "";
    if (isShowOrdinalNumbers())
    {
      ordinalNumberString = String.valueOf(ordinalNumber);
    }
    out.println(createDetailRow(ordinalNumberString, name, type,
        procedureColumnType));
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
   * @see BaseSchemaTextFormatter#handleProcedureName(int, String,
   *      String)
   */
  void handleProcedureName(final int ordinalNumber, final String name,
      final String type)
  {
    out.println(createNameRow(name, "[" + type + "]"));
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
    out.println(Utilities.repeat("-", FormatUtils.MAX_LINE_LENGTH));
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
   * @see BaseSchemaTextFormatter#handleTableName(int, String, String)
   */
  void handleTableName(final int ordinalNumber, final String name,
      final String type)
  {
    final String typeBracketed = "[" + type.toLowerCase(Locale.ENGLISH) + "]";
    out.println(createNameRow(name, typeBracketed));
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseSchemaTextFormatter#handleTableStart()
   */
  void handleTableStart()
  {
  }

  void handleTrigger(int i, String name, String triggerType,
      String actionCondition, String actionStatement)
  {
    out.println(createEmptyRow());

    String triggerName = "";
    if (isShowConstraintNames())
    {
      triggerName = name;
    }
    out.println(createNameRow(triggerName, triggerType));

    if (!Utilities.isBlank(actionCondition))
    {
      out.println(createDefinitionRow(actionCondition));
    }
    if (!Utilities.isBlank(actionStatement))
    {
      out.println(createDefinitionRow(actionStatement));
    }
  }
  private String createDefinitionRow(final String definition)
  {
    StringBuffer row = new StringBuffer();
    row.append(textFormattingFunctor.getFieldSeparator());
    row.append(definition);
    row.append(Utilities.NEWLINE);
    return row.toString();
  }

  private String createDetailRow(String ordinal, final String subName,
      final String type, final String remarks)
  {
    StringBuffer row = new StringBuffer();
    out.print(textFormattingFunctor.getFieldSeparator());
    if (!Utilities.isBlank(ordinal))
    {
      row.append(textFormattingFunctor.format(ordinal, 2, true));
      row.append(textFormattingFunctor.getFieldSeparator());
    }
    row.append(textFormattingFunctor.format(subName, MAX_COLUMN_NAME_WIDTH,
        true));
    row.append(textFormattingFunctor.getFieldSeparator());
    row.append(textFormattingFunctor.format(type, MAX_COLUMN_TYPE_WIDTH, true));
    row.append(textFormattingFunctor.getFieldSeparator());
    row.append(textFormattingFunctor.format(remarks, MAX_COLUMN_MODIFIER_WIDTH,
        false));
    return row.toString();
  }

  private String createEmptyRow()
  {
    return Utilities.NEWLINE;
  }

  private String createNameRow(final String name, final String description)
  {
    StringBuffer row = new StringBuffer();
    row.append(textFormattingFunctor.format(name, MAX_TABLE_NAME_WIDTH, true));
    row.append(textFormattingFunctor.getFieldSeparator());
    row.append(textFormattingFunctor.format(description, MAX_TABLE_TYPE_WIDTH,
        false));
    return row.toString();
  }

  private String createNameValueRow(final String name, final String value)
  {
    StringBuffer row = new StringBuffer();
    row.append(textFormattingFunctor.format(name, MAX_TABLE_NAME_WIDTH, true));
    row.append(textFormattingFunctor.getFieldSeparator());
    row.append(value);
    return row.toString();
  }

  private void printColumnDataTypeProperty(final String userDefined,
      final int width)
  {
    out.println();
    out.print(textFormattingFunctor.getFieldSeparator());
    out.print(textFormattingFunctor.getFieldSeparator());
    out.print(textFormattingFunctor.format(userDefined, width, true));
  }

}
