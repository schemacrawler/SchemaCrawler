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


import java.util.Properties;

import schemacrawler.tools.BaseToolOptions;
import schemacrawler.tools.OutputOptions;

/**
 * Options.
 * 
 * @author sfatehi
 */
public final class SchemaTextOptions
  extends BaseToolOptions
{

  private static final long serialVersionUID = -8133661515343358712L;

  private static final String HIDE_PRIMARY_KEY_NAMES = "schemacrawler.format.hide_primarykey_names";
  private static final String HIDE_FOREIGN_KEY_NAMES = "schemacrawler.format.hide_foreignkey_names";
  private static final String HIDE_INDEX_NAMES = "schemacrawler.format.hide_index_names";
  private static final String HIDE_CONSTRAINT_NAMES = "schemacrawler.format.hide_constraint_names";
  private static final String SHOW_ORDINAL_NUMBERS = "schemacrawler.format.show_ordinal_numbers";
  private static final String SHOW_JDBC_COLUMN_TYPE_NAMES = "schemacrawler.format.show_jdbc_column_type_names";

  private final boolean showJdbcColumnTypeNames;
  private final boolean showOrdinalNumbers;

  private final boolean hidePrimaryKeyNames;
  private final boolean hideForeignKeyNames;
  private final boolean hideIndexNames;
  private final boolean hideConstraintNames;

  private final SchemaTextDetailType schemaTextDetailType;

  /**
   * Options from properties. Constructor.
   * 
   * @param config
   *        Properties
   * @param schemaTextDetailType
   *        Schema detail
   * @param outputOptions
   *        Output options
   */
  public SchemaTextOptions(final Properties config,
                           final OutputOptions outputOptions,
                           final SchemaTextDetailType schemaTextDetailType)
  {
    super(outputOptions);

    if (schemaTextDetailType == null)
    {
      this.schemaTextDetailType = SchemaTextDetailType.BRIEF;
    }
    else
    {
      this.schemaTextDetailType = schemaTextDetailType;
    }

    if (config == null)
    {
      showJdbcColumnTypeNames = false;
      showOrdinalNumbers = false;

      hideForeignKeyNames = false;
      hidePrimaryKeyNames = false;
      hideIndexNames = false;
      hideConstraintNames = false;
    }
    else
    {
      showJdbcColumnTypeNames = getBooleanProperty(SHOW_JDBC_COLUMN_TYPE_NAMES,
                                                   config);
      showOrdinalNumbers = getBooleanProperty(SHOW_ORDINAL_NUMBERS, config);

      hideForeignKeyNames = getBooleanProperty(HIDE_FOREIGN_KEY_NAMES, config);
      hidePrimaryKeyNames = getBooleanProperty(HIDE_PRIMARY_KEY_NAMES, config);
      hideIndexNames = getBooleanProperty(HIDE_INDEX_NAMES, config);
      hideConstraintNames = getBooleanProperty(HIDE_CONSTRAINT_NAMES, config);
    }
  }

  /**
   * Schema text detail type.
   * 
   * @return Schema text detail type
   */
  public SchemaTextDetailType getSchemaTextDetailType()
  {
    return schemaTextDetailType;
  }

  boolean isHideConstraintNames()
  {
    return hideConstraintNames;
  }

  boolean isHideForeignKeyNames()
  {
    return hideForeignKeyNames;
  }

  boolean isHideIndexNames()
  {
    return hideIndexNames;
  }

  boolean isHidePrimaryKeyNames()
  {
    return hidePrimaryKeyNames;
  }

  boolean isShowJdbcColumnTypeNames()
  {
    return showJdbcColumnTypeNames;
  }

  boolean isShowOrdinalNumbers()
  {
    return showOrdinalNumbers;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("TextFormatOptions[");
    buffer.append("hideIndexNames=").append(hideConstraintNames);
    buffer.append(", showJdbcColumnTypeNames=").append(showJdbcColumnTypeNames);
    buffer.append(", showOrdinalNumbers=").append(showOrdinalNumbers);
    buffer.append(", schemaTextDetailType=").append(schemaTextDetailType);
    buffer.append(", outputOptions=").append(getOutputOptions());
    buffer.append("]");
    return buffer.toString();
  }

}
