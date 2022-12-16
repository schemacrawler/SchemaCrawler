/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.text.schema.options;

import java.util.EnumMap;
import java.util.Map;

import schemacrawler.tools.options.Config;
import schemacrawler.tools.text.options.BaseTextOptionsBuilder;

public abstract class BaseSchemaTextOptionsBuilder<
        B extends BaseSchemaTextOptionsBuilder<B, O>, O extends SchemaTextOptions>
    extends BaseTextOptionsBuilder<BaseSchemaTextOptionsBuilder<B, O>, O> {

  private static final String SHOW_ORDINAL_NUMBERS =
      SCHEMACRAWLER_FORMAT_PREFIX + "show_ordinal_numbers";
  private static final String SHOW_STANDARD_COLUMN_TYPE_NAMES =
      SCHEMACRAWLER_FORMAT_PREFIX + "show_standard_column_type_names";
  private static final String HIDE_TABLE_ROW_COUNTS =
      SCHEMACRAWLER_FORMAT_PREFIX + "hide_table_row_counts";

  private static final String HIDE_REMARKS = SCHEMACRAWLER_FORMAT_PREFIX + "hide_remarks";

  private static final String SC_SORT_ALPHABETICALLY_TABLE_INDEXES =
      SCHEMACRAWLER_FORMAT_PREFIX + "sort_alphabetically.table_indexes";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS =
      SCHEMACRAWLER_FORMAT_PREFIX + "sort_alphabetically.table_foreignkeys";

  protected boolean isAlphabeticalSortForForeignKeys;
  protected boolean isAlphabeticalSortForIndexes;
  protected boolean isHideRemarks;
  protected boolean isShowOrdinalNumbers;
  protected boolean isShowStandardColumnTypeNames;
  protected boolean isHideTableRowCounts;
  protected final Map<HideDatabaseObjectNamesType, Boolean> hideNames;

  public BaseSchemaTextOptionsBuilder() {
    hideNames = new EnumMap<>(HideDatabaseObjectNamesType.class);
  }

  @Override
  public B fromConfig(final Config config) {
    if (config == null) {
      return (B) this;
    }
    super.fromConfig(config);

    isShowStandardColumnTypeNames = config.getBooleanValue(SHOW_STANDARD_COLUMN_TYPE_NAMES);
    isShowOrdinalNumbers = config.getBooleanValue(SHOW_ORDINAL_NUMBERS);
    isHideTableRowCounts = config.getBooleanValue(HIDE_TABLE_ROW_COUNTS);

    isHideRemarks = config.getBooleanValue(HIDE_REMARKS);

    isAlphabeticalSortForForeignKeys =
        config.getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS);
    isAlphabeticalSortForIndexes = config.getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_INDEXES);

    for (final HideDatabaseObjectNamesType databaseObjectNamesType : HideDatabaseObjectNamesType.values()) {
      final boolean booleanValue = config.getBooleanValue(databaseObjectNamesType.getKey());
      hideNames.put(databaseObjectNamesType, booleanValue);
    }

    // Override values from command line
    fromConfigCommandLineOverride(config);

    return (B) this;
  }

  @Override
  public B fromOptions(final O options) {
    if (options == null) {
      return (B) this;
    }
    super.fromOptions(options);

    isShowStandardColumnTypeNames = options.isShowStandardColumnTypeNames();
    isShowOrdinalNumbers = options.isShowOrdinalNumbers();
    isHideTableRowCounts = options.isHideTableRowCounts();

    isHideRemarks = options.isHideRemarks();

    isAlphabeticalSortForForeignKeys = options.isAlphabeticalSortForForeignKeys();
    isAlphabeticalSortForIndexes = options.isAlphabeticalSortForIndexes();

    for (final HideDatabaseObjectNamesType databaseObjectNamesType : HideDatabaseObjectNamesType.values()) {
      hideNames.put(databaseObjectNamesType, options.get(databaseObjectNamesType));
    }

    return (B) this;
  }

  public final B hideRowCounts() {
    return hideRowCounts(true);
  }

  public final B hideRowCounts(final boolean value) {
    isHideTableRowCounts = value;
    return (B) this;
  }

  public final B noAlternateKeyNames() {
    return noAlternateKeyNames(true);
  }

  public final B noAlternateKeyNames(final boolean value) {
    hideNames.put(HideDatabaseObjectNamesType.hideAlternateKeyNames, value);
    return (B) this;
  }

  public final B noConstraintNames() {
    return noConstraintNames(true);
  }

  public final B noConstraintNames(final boolean value) {
    hideNames.put(HideDatabaseObjectNamesType.hideTableConstraintNames, value);
    return (B) this;
  }

  public final B noForeignKeyNames() {
    return noForeignKeyNames(true);
  }

  public final B noForeignKeyNames(final boolean value) {
    hideNames.put(HideDatabaseObjectNamesType.hideForeignKeyNames, value);
    return (B) this;
  }

  public final B noIndexNames() {
    return noIndexNames(true);
  }

  public final B noIndexNames(final boolean value) {
    hideNames.put(HideDatabaseObjectNamesType.hideIndexNames, value);
    return (B) this;
  }

  public final B noPrimaryKeyNames() {
    return noPrimaryKeyNames(true);
  }

  public final B noPrimaryKeyNames(final boolean value) {
    hideNames.put(HideDatabaseObjectNamesType.hidePrimaryKeyNames, value);
    return (B) this;
  }

  /** Corresponds to the -noremarks command-line argument. */
  public final B noRemarks() {
    return noRemarks(true);
  }

  /** Corresponds to the -noremarks=&lt;boolean&gt; command-line argument. */
  public final B noRemarks(final boolean value) {
    isHideRemarks = value;
    return (B) this;
  }

  public final B noRoutineSpecificNames() {
    return noRoutineSpecificNames(true);
  }

  public final B noRoutineSpecificNames(final boolean value) {
    hideNames.put(HideDatabaseObjectNamesType.hideRoutineSpecificNames, value);
    return (B) this;
  }

  public final B noTriggerNames() {
    return noTriggerNames(true);
  }

  public final B noTriggerNames(final boolean value) {
    hideNames.put(HideDatabaseObjectNamesType.hideTriggerNames, value);
    return (B) this;
  }

  public final B noWeakAssociationNames() {
    return noWeakAssociationNames(true);
  }

  public final B noWeakAssociationNames(final boolean value) {
    hideNames.put(HideDatabaseObjectNamesType.hideWeakAssociationNames, value);
    return (B) this;
  }

  /** Corresponds to the --portable-names command-line argument. */
  public final B portableNames() {
    return portableNames(true);
  }

  /** Corresponds to the --portable-names=&lt;boolean&gt; command-line argument. */
  public final B portableNames(final boolean value) {

    for (final HideDatabaseObjectNamesType databaseObjectNamesType : HideDatabaseObjectNamesType.values()) {
      hideNames.put(databaseObjectNamesType, value);
    }
    isShowUnqualifiedNames = value;

    return (B) this;
  }

  public final B showOrdinalNumbers() {
    return showOrdinalNumbers(true);
  }

  public final B showOrdinalNumbers(final boolean value) {
    isShowOrdinalNumbers = value;
    return (B) this;
  }

  public final B showStandardColumnTypeNames() {
    return showStandardColumnTypeNames(true);
  }

  public final B showStandardColumnTypeNames(final boolean value) {
    isShowStandardColumnTypeNames = value;
    return (B) this;
  }

  public final B sortForeignKeys() {
    return sortForeignKeys(true);
  }

  public final B sortForeignKeys(final boolean value) {
    isAlphabeticalSortForForeignKeys = value;
    return (B) this;
  }

  public final B sortIndexes() {
    return sortIndexes(true);
  }

  public final B sortIndexes(final boolean value) {
    isAlphabeticalSortForIndexes = value;
    return (B) this;
  }

  @Override
  public Config toConfig() {
    final Config config = super.toConfig();

    config.put(SHOW_STANDARD_COLUMN_TYPE_NAMES, isShowStandardColumnTypeNames);
    config.put(SHOW_ORDINAL_NUMBERS, isShowOrdinalNumbers);
    config.put(HIDE_TABLE_ROW_COUNTS, isHideTableRowCounts);

    config.put(HIDE_REMARKS, isHideRemarks);

    config.put(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS, isAlphabeticalSortForForeignKeys);
    config.put(SC_SORT_ALPHABETICALLY_TABLE_INDEXES, isAlphabeticalSortForIndexes);

    for (final HideDatabaseObjectNamesType databaseObjectNamesType : HideDatabaseObjectNamesType.values()) {
      config.put(
          databaseObjectNamesType.getKey(), hideNames.getOrDefault(databaseObjectNamesType, false));
    }

    return config;
  }

  private void fromConfigCommandLineOverride(final Config config) {

    final String noremarksKey = "no-remarks";
    if (config.containsKey(noremarksKey)) {
      noRemarks(config.getBooleanValue(noremarksKey));
    }

    final String portablenamesKey = "portable-names";
    if (config.containsKey(portablenamesKey)) {
      portableNames(config.getBooleanValue(portablenamesKey));
    }
  }
}
