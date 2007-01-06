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
  private static final String SHOW_STANDARD_COLUMN_TYPE_NAMES = "schemacrawler.format.show_standard_column_type_names";
  /** Deprecated */
  private static final String SHOW_JDBC_COLUMN_TYPE_NAMES = "schemacrawler.format.show_jdbc_column_type_names";

  private boolean showStandardColumnTypeNames;
  private boolean showOrdinalNumbers;

  private boolean hidePrimaryKeyNames;
  private boolean hideForeignKeyNames;
  private boolean hideIndexNames;
  private boolean hideConstraintNames;

  private SchemaTextDetailType schemaTextDetailType;

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
      showStandardColumnTypeNames = false;
      showOrdinalNumbers = false;

      hideForeignKeyNames = false;
      hidePrimaryKeyNames = false;
      hideIndexNames = false;
      hideConstraintNames = false;
    }
    else
    {
      showStandardColumnTypeNames = getBooleanProperty(SHOW_JDBC_COLUMN_TYPE_NAMES,
                                                       config)
                                    || getBooleanProperty(SHOW_STANDARD_COLUMN_TYPE_NAMES,
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

  public boolean isHideConstraintNames()
  {
    return hideConstraintNames;
  }

  public boolean isHideForeignKeyNames()
  {
    return hideForeignKeyNames;
  }

  public boolean isHideIndexNames()
  {
    return hideIndexNames;
  }

  public boolean isHidePrimaryKeyNames()
  {
    return hidePrimaryKeyNames;
  }

  public boolean isShowOrdinalNumbers()
  {
    return showOrdinalNumbers;
  }

  public boolean isShowStandardColumnTypeNames()
  {
    return showStandardColumnTypeNames;
  }

  public void setHideConstraintNames(boolean hideConstraintNames)
  {
    this.hideConstraintNames = hideConstraintNames;
  }

  public void setHideForeignKeyNames(boolean hideForeignKeyNames)
  {
    this.hideForeignKeyNames = hideForeignKeyNames;
  }

  public void setHideIndexNames(boolean hideIndexNames)
  {
    this.hideIndexNames = hideIndexNames;
  }

  public void setHidePrimaryKeyNames(boolean hidePrimaryKeyNames)
  {
    this.hidePrimaryKeyNames = hidePrimaryKeyNames;
  }

  public void setSchemaTextDetailType(SchemaTextDetailType schemaTextDetailType)
  {
    this.schemaTextDetailType = schemaTextDetailType;
  }

  public void setShowOrdinalNumbers(boolean showOrdinalNumbers)
  {
    this.showOrdinalNumbers = showOrdinalNumbers;
  }

  public void setShowStandardColumnTypeNames(boolean showStandardColumnTypeNames)
  {
    this.showStandardColumnTypeNames = showStandardColumnTypeNames;
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
    buffer.append(", showStandardColumnTypeNames=")
      .append(showStandardColumnTypeNames);
    buffer.append(", showOrdinalNumbers=").append(showOrdinalNumbers);
    buffer.append(", schemaTextDetailType=").append(schemaTextDetailType);
    buffer.append(", outputOptions=").append(getOutputOptions());
    buffer.append("]");
    return buffer.toString();
  }

}
