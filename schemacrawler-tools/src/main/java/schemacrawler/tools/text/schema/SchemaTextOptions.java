/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

package schemacrawler.tools.text.schema;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.Options;

/**
 * Options.
 * 
 * @author Sualeh Fatehi
 */
public class SchemaTextOptions
  implements Options
{

  private static final long serialVersionUID = -8133661515343358712L;

  private static final String HIDE_PRIMARY_KEY_NAMES = "schemacrawler.format.hide_primarykey_names";
  private static final String HIDE_FOREIGN_KEY_NAMES = "schemacrawler.format.hide_foreignkey_names";
  private static final String HIDE_INDEX_NAMES = "schemacrawler.format.hide_index_names";
  private static final String HIDE_CONSTRAINT_NAMES = "schemacrawler.format.hide_constraint_names";
  private static final String SHOW_ORDINAL_NUMBERS = "schemacrawler.format.show_ordinal_numbers";
  private static final String SHOW_STANDARD_COLUMN_TYPE_NAMES = "schemacrawler.format.show_standard_column_type_names";

  private boolean showStandardColumnTypeNames;
  private boolean showOrdinalNumbers;

  private boolean hidePrimaryKeyNames;
  private boolean hideForeignKeyNames;
  private boolean hideIndexNames;
  private boolean hideConstraintNames;

  /**
   * Creates the default SchemaTextOptions.
   */
  public SchemaTextOptions()
  {
    setDefaultValues();
  }

  /**
   * Options from properties. Constructor.
   * 
   * @param config
   *        Properties
   */
  public SchemaTextOptions(final Config config)
  {
    setDefaultValues();
    if (config != null)
    {
      showStandardColumnTypeNames = config
        .getBooleanValue(SHOW_STANDARD_COLUMN_TYPE_NAMES);
      showOrdinalNumbers = config.getBooleanValue(SHOW_ORDINAL_NUMBERS);

      hideForeignKeyNames = config.getBooleanValue(HIDE_FOREIGN_KEY_NAMES);
      hidePrimaryKeyNames = config.getBooleanValue(HIDE_PRIMARY_KEY_NAMES);
      hideIndexNames = config.getBooleanValue(HIDE_INDEX_NAMES);
      hideConstraintNames = config.getBooleanValue(HIDE_CONSTRAINT_NAMES);
    }
  }

  /**
   * Whether to hide constraint names.
   * 
   * @return Hide constraint names.
   */
  public boolean isHideConstraintNames()
  {
    return hideConstraintNames;
  }

  /**
   * Whether to hide foreign key names.
   * 
   * @return Hide foreign key names.
   */
  public boolean isHideForeignKeyNames()
  {
    return hideForeignKeyNames;
  }

  /**
   * Whether to hide index names.
   * 
   * @return Hide index names.
   */
  public boolean isHideIndexNames()
  {
    return hideIndexNames;
  }

  /**
   * Whether to hide primary key names.
   * 
   * @return Hide primary key names.
   */
  public boolean isHidePrimaryKeyNames()
  {
    return hidePrimaryKeyNames;
  }

  /**
   * Whether to show ordinal numbers.
   * 
   * @return Whether to show ordinal numbers.
   */
  public boolean isShowOrdinalNumbers()
  {
    return showOrdinalNumbers;
  }

  /**
   * Whether to show standard column types.
   * 
   * @return Whether to show standard column types.
   */
  public boolean isShowStandardColumnTypeNames()
  {
    return showStandardColumnTypeNames;
  }

  /**
   * Sets whether to hide constraint names.
   * 
   * @param hideConstraintNames
   *        Whether to hide constraint names.
   */
  public void setHideConstraintNames(final boolean hideConstraintNames)
  {
    this.hideConstraintNames = hideConstraintNames;
  }

  /**
   * Sets whether to hide foreign key names.
   * 
   * @param hideForeignKeyNames
   *        Whether to hide foreign key names.
   */
  public void setHideForeignKeyNames(final boolean hideForeignKeyNames)
  {
    this.hideForeignKeyNames = hideForeignKeyNames;
  }

  /**
   * Sets whether to hide index names.
   * 
   * @param hideIndexNames
   *        Whether to hide index names.
   */
  public void setHideIndexNames(final boolean hideIndexNames)
  {
    this.hideIndexNames = hideIndexNames;
  }

  /**
   * Sets whether to hide primary key names.
   * 
   * @param hidePrimaryKeyNames
   *        Whether to hide primary key names.
   */
  public void setHidePrimaryKeyNames(final boolean hidePrimaryKeyNames)
  {
    this.hidePrimaryKeyNames = hidePrimaryKeyNames;
  }

  /**
   * Sets whether to show ordinal numbers.
   * 
   * @param showOrdinalNumbers
   *        Whether to show ordinal numbers.
   */
  public void setShowOrdinalNumbers(final boolean showOrdinalNumbers)
  {
    this.showOrdinalNumbers = showOrdinalNumbers;
  }

  /**
   * Sets whether to show standard column type names.
   * 
   * @param showStandardColumnTypeNames
   *        Whether to show standard column type names.
   */
  public void setShowStandardColumnTypeNames(final boolean showStandardColumnTypeNames)
  {
    this.showStandardColumnTypeNames = showStandardColumnTypeNames;
  }

  private void setDefaultValues()
  {
    showStandardColumnTypeNames = false;
    showOrdinalNumbers = false;

    hideForeignKeyNames = false;
    hidePrimaryKeyNames = false;
    hideIndexNames = false;
    hideConstraintNames = false;
  }

}
