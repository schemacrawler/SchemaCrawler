/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.text.schema;

import schemacrawler.tools.options.Config;
import schemacrawler.tools.text.base.BaseTextOptionsBuilder;

public abstract class BaseSchemaTextOptionsBuilder<
        B extends BaseSchemaTextOptionsBuilder<B, O>, O extends SchemaTextOptions>
    extends BaseTextOptionsBuilder<BaseSchemaTextOptionsBuilder<B, O>, O> {

  private static final String SHOW_ORDINAL_NUMBERS =
      SCHEMACRAWLER_FORMAT_PREFIX + "show_ordinal_numbers";
  private static final String SHOW_STANDARD_COLUMN_TYPE_NAMES =
      SCHEMACRAWLER_FORMAT_PREFIX + "show_standard_column_type_names";
  private static final String SHOW_ROW_COUNTS = SCHEMACRAWLER_FORMAT_PREFIX + "show_row_counts";

  private static final String HIDE_PRIMARY_KEY_NAMES =
      SCHEMACRAWLER_FORMAT_PREFIX + "hide_primarykey_names";
  private static final String HIDE_FOREIGN_KEY_NAMES =
      SCHEMACRAWLER_FORMAT_PREFIX + "hide_foreignkey_names";
  private static final String HIDE_INDEX_NAMES = SCHEMACRAWLER_FORMAT_PREFIX + "hide_index_names";
  private static final String HIDE_CONSTRAINT_NAMES =
      SCHEMACRAWLER_FORMAT_PREFIX + "hide_constraint_names";
  private static final String HIDE_TRIGGER_NAMES =
      SCHEMACRAWLER_FORMAT_PREFIX + "hide_trigger_names";
  private static final String HIDE_ROUTINE_SPECIFIC_NAMES =
      SCHEMACRAWLER_FORMAT_PREFIX + "hide_routine_specific_names";
  private static final String HIDE_REMARKS = SCHEMACRAWLER_FORMAT_PREFIX + "hide_remarks";
  private static final String SHOW_WEAK_ASSOCIATIONS =
      SCHEMACRAWLER_FORMAT_PREFIX + "show_weak_associations";

  private static final String SC_SORT_ALPHABETICALLY_TABLE_INDEXES =
      SCHEMACRAWLER_FORMAT_PREFIX + "sort_alphabetically.table_indexes";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS =
      SCHEMACRAWLER_FORMAT_PREFIX + "sort_alphabetically.table_foreignkeys";

  protected boolean isAlphabeticalSortForForeignKeys;
  protected boolean isAlphabeticalSortForIndexes;
  protected boolean isHideForeignKeyNames;
  protected boolean isHideIndexNames;
  protected boolean isHidePrimaryKeyNames;
  protected boolean isHideRemarks;
  protected boolean isHideRoutineSpecificNames;
  protected boolean isHideTableConstraintNames;
  protected boolean isHideTriggerNames;
  protected boolean isShowWeakAssociations;
  protected boolean isShowOrdinalNumbers;
  protected boolean isShowStandardColumnTypeNames;
  protected boolean isShowRowCounts;

  public BaseSchemaTextOptionsBuilder() {}

  @Override
  public B fromConfig(final Config config) {
    if (config == null) {
      return (B) this;
    }
    super.fromConfig(config);

    isShowStandardColumnTypeNames = config.getBooleanValue(SHOW_STANDARD_COLUMN_TYPE_NAMES);
    isShowOrdinalNumbers = config.getBooleanValue(SHOW_ORDINAL_NUMBERS);
    isShowRowCounts = config.getBooleanValue(SHOW_ROW_COUNTS);

    isHideForeignKeyNames = config.getBooleanValue(HIDE_FOREIGN_KEY_NAMES);
    isHidePrimaryKeyNames = config.getBooleanValue(HIDE_PRIMARY_KEY_NAMES);
    isHideIndexNames = config.getBooleanValue(HIDE_INDEX_NAMES);
    isHideTriggerNames = config.getBooleanValue(HIDE_TRIGGER_NAMES);
    isHideRoutineSpecificNames = config.getBooleanValue(HIDE_ROUTINE_SPECIFIC_NAMES);
    isHideTableConstraintNames = config.getBooleanValue(HIDE_CONSTRAINT_NAMES);
    isHideRemarks = config.getBooleanValue(HIDE_REMARKS);
    isShowWeakAssociations = config.getBooleanValue(SHOW_WEAK_ASSOCIATIONS);

    isAlphabeticalSortForForeignKeys =
        config.getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS);
    isAlphabeticalSortForIndexes = config.getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_INDEXES);

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
    isShowRowCounts = options.isShowRowCounts();

    isHideForeignKeyNames = options.isHideForeignKeyNames();
    isHidePrimaryKeyNames = options.isHidePrimaryKeyNames();
    isHideIndexNames = options.isHideIndexNames();
    isHideTriggerNames = options.isHideTriggerNames();
    isHideRoutineSpecificNames = options.isHideRoutineSpecificNames();
    isHideTableConstraintNames = options.isHideTableConstraintNames();
    isHideRemarks = options.isHideRemarks();
    isShowWeakAssociations = options.isShowWeakAssociations();

    isAlphabeticalSortForForeignKeys = options.isAlphabeticalSortForForeignKeys();
    isAlphabeticalSortForIndexes = options.isAlphabeticalSortForIndexes();

    return (B) this;
  }

  public final B noConstraintNames() {
    return noConstraintNames(true);
  }

  public final B noConstraintNames(final boolean value) {
    isHideTableConstraintNames = value;
    return (B) this;
  }

  public final B noForeignKeyNames() {
    return noForeignKeyNames(true);
  }

  public final B noForeignKeyNames(final boolean value) {
    isHideForeignKeyNames = value;
    return (B) this;
  }

  public final B noIndexNames() {
    return noIndexNames(true);
  }

  public final B noIndexNames(final boolean value) {
    isHideIndexNames = value;
    return (B) this;
  }

  public final B noPrimaryKeyNames() {
    return noPrimaryKeyNames(true);
  }

  public final B noPrimaryKeyNames(final boolean value) {
    isHidePrimaryKeyNames = value;
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
    isHideRoutineSpecificNames = value;
    return (B) this;
  }

  public final B noTriggerNames() {
    return noTriggerNames(true);
  }

  public final B noTriggerNames(final boolean value) {
    isHideTriggerNames = value;
    return (B) this;
  }

  /** Corresponds to the --portable-names command-line argument. */
  public final B portableNames() {
    return portableNames(true);
  }

  /** Corresponds to the --portable-names=&lt;boolean&gt; command-line argument. */
  public final B portableNames(final boolean value) {
    isHideTableConstraintNames = value;
    isHideForeignKeyNames = value;
    isHideIndexNames = value;
    isHidePrimaryKeyNames = value;
    isHideTriggerNames = value;
    isHideRoutineSpecificNames = value;
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

  public final B showRowCounts() {
    return showRowCounts(true);
  }

  public final B showRowCounts(final boolean value) {
    isShowRowCounts = value;
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

    config.setBooleanValue(SHOW_STANDARD_COLUMN_TYPE_NAMES, isShowStandardColumnTypeNames);
    config.setBooleanValue(SHOW_ORDINAL_NUMBERS, isShowOrdinalNumbers);
    config.setBooleanValue(SHOW_ROW_COUNTS, isShowRowCounts);

    config.setBooleanValue(HIDE_FOREIGN_KEY_NAMES, isHideForeignKeyNames);
    config.setBooleanValue(HIDE_PRIMARY_KEY_NAMES, isHidePrimaryKeyNames);
    config.setBooleanValue(HIDE_INDEX_NAMES, isHideIndexNames);
    config.setBooleanValue(HIDE_TRIGGER_NAMES, isHideTriggerNames);
    config.setBooleanValue(HIDE_ROUTINE_SPECIFIC_NAMES, isHideRoutineSpecificNames);
    config.setBooleanValue(HIDE_CONSTRAINT_NAMES, isHideTableConstraintNames);
    config.setBooleanValue(HIDE_REMARKS, isHideRemarks);
    config.setBooleanValue(SHOW_WEAK_ASSOCIATIONS, isShowWeakAssociations);

    config.setBooleanValue(
        SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS, isAlphabeticalSortForForeignKeys);
    config.setBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_INDEXES, isAlphabeticalSortForIndexes);

    return config;
  }

  /** Corresponds to the --weak-associations command-line argument. */
  public final B weakAssociations() {
    return weakAssociations(true);
  }

  /** Corresponds to the --weak-associations=&lt;boolean&gt; command-line argument. */
  public final B weakAssociations(final boolean value) {
    isShowWeakAssociations = value;
    return (B) this;
  }

  private void fromConfigCommandLineOverride(final Config config) {

    final String noremarksKey = "no-remarks";
    if (config.containsKey(noremarksKey)) {
      noRemarks(config.getBooleanValue(noremarksKey));
    }

    final String weakassociationsKey = "weak-associations";
    if (config.containsKey(weakassociationsKey)) {
      weakAssociations(config.getBooleanValue(weakassociationsKey));
    }

    final String portablenamesKey = "portable-names";
    if (config.containsKey(portablenamesKey)) {
      portableNames(config.getBooleanValue(portablenamesKey));
    }
  }
}
