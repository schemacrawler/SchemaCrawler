/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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

  private static final String SHOW_UNQUALIFIED_NAMES = "schemacrawler.format.show_unqualified_names";
  private static final String SHOW_ORDINAL_NUMBERS = "schemacrawler.format.show_ordinal_numbers";
  private static final String SHOW_STANDARD_COLUMN_TYPE_NAMES = "schemacrawler.format.show_standard_column_type_names";

  private static final String HIDE_PRIMARY_KEY_NAMES = "schemacrawler.format.hide_primarykey_names";
  private static final String HIDE_FOREIGN_KEY_NAMES = "schemacrawler.format.hide_foreignkey_names";
  private static final String HIDE_INDEX_NAMES = "schemacrawler.format.hide_index_names";
  private static final String HIDE_CONSTRAINT_NAMES = "schemacrawler.format.hide_constraint_names";

  private static final String SC_SORT_ALPHABETICALLY_TABLES = "schemacrawler.format.sort_alphabetically.tables";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_COLUMNS = "schemacrawler.format.sort_alphabetically.table_columns";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_INDEXES = "schemacrawler.format.sort_alphabetically.table_indices";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS = "schemacrawler.format.sort_alphabetically.table_foreignkeys";
  private static final String SC_SORT_ALPHABETICALLY_ROUTINE_COLUMNS = "schemacrawler.format.sort_alphabetically.routine_columns";

  private boolean showUnqualifiedNames;
  private boolean showStandardColumnTypeNames;
  private boolean showOrdinalNumbers;

  private boolean hidePrimaryKeyNames;
  private boolean hideForeignKeyNames;
  private boolean hideIndexNames;
  private boolean hideConstraintNames;

  private boolean isAlphabeticalSortForTables;
  private boolean isAlphabeticalSortForTableColumns;
  private boolean isAlphabeticalSortForForeignKeys;
  private boolean isAlphabeticalSortForIndexes;
  private boolean isAlphabeticalSortForRoutineColumns;

  /**
   * Creates the default SchemaTextOptions.
   */
  public SchemaTextOptions()
  {
    // NOTE: Most boolean options are false by default

    isAlphabeticalSortForTables = true;
    isAlphabeticalSortForTableColumns = false;
    isAlphabeticalSortForForeignKeys = false;
    isAlphabeticalSortForIndexes = false;
    isAlphabeticalSortForRoutineColumns = false;
  }

  /**
   * Options from properties. Constructor.
   * 
   * @param config
   *        Properties
   */
  public SchemaTextOptions(final Config config)
  {
    if (config != null)
    {
      showUnqualifiedNames = config.getBooleanValue(SHOW_UNQUALIFIED_NAMES);
      showStandardColumnTypeNames = config
        .getBooleanValue(SHOW_STANDARD_COLUMN_TYPE_NAMES);
      showOrdinalNumbers = config.getBooleanValue(SHOW_ORDINAL_NUMBERS);

      hideForeignKeyNames = config.getBooleanValue(HIDE_FOREIGN_KEY_NAMES);
      hidePrimaryKeyNames = config.getBooleanValue(HIDE_PRIMARY_KEY_NAMES);
      hideIndexNames = config.getBooleanValue(HIDE_INDEX_NAMES);
      hideConstraintNames = config.getBooleanValue(HIDE_CONSTRAINT_NAMES);

      isAlphabeticalSortForTables = Boolean.parseBoolean(config
        .getStringValue(SC_SORT_ALPHABETICALLY_TABLES, "true"));
      isAlphabeticalSortForTableColumns = config
        .getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_COLUMNS);
      isAlphabeticalSortForForeignKeys = config
        .getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS);
      isAlphabeticalSortForIndexes = config
        .getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_INDEXES);
      isAlphabeticalSortForRoutineColumns = config
        .getBooleanValue(SC_SORT_ALPHABETICALLY_ROUTINE_COLUMNS);
    }
  }

  public boolean isAlphabeticalSortForForeignKeys()
  {
    return isAlphabeticalSortForForeignKeys;
  }

  public boolean isAlphabeticalSortForIndexes()
  {
    return isAlphabeticalSortForIndexes;
  }

  public boolean isAlphabeticalSortForRoutineColumns()
  {
    return isAlphabeticalSortForRoutineColumns;
  }

  public boolean isAlphabeticalSortForTableColumns()
  {
    return isAlphabeticalSortForTableColumns;
  }

  public boolean isAlphabeticalSortForTables()
  {
    return isAlphabeticalSortForTables;
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

  public boolean isShowUnqualifiedNames()
  {
    return showUnqualifiedNames;
  }

  public void setAlphabeticalSortForForeignKeys(final boolean isAlphabeticalSortForForeignKeys)
  {
    this.isAlphabeticalSortForForeignKeys = isAlphabeticalSortForForeignKeys;
  }

  public void setAlphabeticalSortForIndexes(final boolean isAlphabeticalSortForIndexes)
  {
    this.isAlphabeticalSortForIndexes = isAlphabeticalSortForIndexes;
  }

  public void setAlphabeticalSortForRoutineColumns(final boolean isAlphabeticalSortForRoutineColumns)
  {
    this.isAlphabeticalSortForRoutineColumns = isAlphabeticalSortForRoutineColumns;
  }

  public void setAlphabeticalSortForTableColumns(final boolean isAlphabeticalSortForTableColumns)
  {
    this.isAlphabeticalSortForTableColumns = isAlphabeticalSortForTableColumns;
  }

  public void setAlphabeticalSortForTables(final boolean isAlphabeticalSortForTables)
  {
    this.isAlphabeticalSortForTables = isAlphabeticalSortForTables;
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

  public void setShowUnqualifiedNames(final boolean showUnqualifiedNames)
  {
    this.showUnqualifiedNames = showUnqualifiedNames;
  }

}
