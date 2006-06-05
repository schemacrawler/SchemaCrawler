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

  private static final String HIDE_INDEX_NAMES = "schemacrawler.format.hide_index_names";
  private static final String SHOW_ORDINAL_NUMBERS = "schemacrawler.format.show_ordinal_numbers";
  private static final String SHOW_JDBC_COLUMN_TYPE_NAMES = "schemacrawler.format.show_jdbc_column_type_names";

  private final boolean showJdbcColumnTypeNames;
  private final boolean showOrdinalNumbers;
  private final boolean hideIndexNames;
  private final SchemaTextDetailType schemaTextDetailType;

  /**
   * Options from properties. Constructor.
   * 
   * @param config
   *          Properties
   * @param schemaTextDetailType
   *          Schema detail
   * @param outputOptions
   *          Output options
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
      this.showJdbcColumnTypeNames = false;
      this.showOrdinalNumbers = false;
      this.hideIndexNames = false;
    }
    else
    {
      this.showJdbcColumnTypeNames = getBooleanProperty(SHOW_JDBC_COLUMN_TYPE_NAMES,
                                                        config);
      this.showOrdinalNumbers = getBooleanProperty(SHOW_ORDINAL_NUMBERS, config);
      this.hideIndexNames = getBooleanProperty(HIDE_INDEX_NAMES, config);
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

  boolean isHideIndexNames()
  {
    return hideIndexNames;
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
    buffer.append("hideIndexNames=").append(hideIndexNames);
    buffer.append(", showJdbcColumnTypeNames=").append(showJdbcColumnTypeNames);
    buffer.append(", showOrdinalNumbers=").append(showOrdinalNumbers);
    buffer.append(", schemaTextDetailType=").append(schemaTextDetailType);
    buffer.append(", outputOptions=").append(getOutputOptions());
    buffer.append("]");
    return buffer.toString();
  }

}
