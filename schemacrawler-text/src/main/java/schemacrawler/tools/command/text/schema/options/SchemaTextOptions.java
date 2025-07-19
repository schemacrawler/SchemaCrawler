/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.schema.options;

import java.util.EnumMap;
import java.util.Map;
import schemacrawler.tools.text.options.BaseTextOptions;

public class SchemaTextOptions extends BaseTextOptions {

  private final boolean isAlphabeticalSortForForeignKeys;
  private final boolean isAlphabeticalSortForIndexes;
  private final boolean isHideRemarks;
  private final boolean isShowOrdinalNumbers;
  private final boolean isShowStandardColumnTypeNames;
  private final boolean isHideTableRowCounts;
  private final boolean isHideTriggerActionStatements;
  private final Map<HideDatabaseObjectsType, Boolean> hideDatabaseObjects;
  private final Map<HideDependantDatabaseObjectsType, Boolean> hideDependantDatabaseObjects;
  private final Map<HideDatabaseObjectNamesType, Boolean> hideNames;

  protected SchemaTextOptions(
      final BaseSchemaTextOptionsBuilder<?, ? extends SchemaTextOptions> builder) {
    super(builder);

    isAlphabeticalSortForForeignKeys = builder.isAlphabeticalSortForForeignKeys;
    isAlphabeticalSortForIndexes = builder.isAlphabeticalSortForIndexes;
    isHideRemarks = builder.isHideRemarks;
    isShowOrdinalNumbers = builder.isShowOrdinalNumbers;
    isShowStandardColumnTypeNames = builder.isShowStandardColumnTypeNames;
    isHideTableRowCounts = builder.isHideTableRowCounts;
    isHideTriggerActionStatements = builder.isHideTriggerActionStatements;

    hideDatabaseObjects = new EnumMap<>(HideDatabaseObjectsType.class);
    for (final HideDatabaseObjectsType databaseObjectsType : HideDatabaseObjectsType.values()) {
      hideDatabaseObjects.put(
          databaseObjectsType,
          builder.hideDatabaseObjects.getOrDefault(databaseObjectsType, false));
    }
    hideDependantDatabaseObjects = new EnumMap<>(HideDependantDatabaseObjectsType.class);
    for (final HideDependantDatabaseObjectsType databaseObjectsType :
        HideDependantDatabaseObjectsType.values()) {
      hideDependantDatabaseObjects.put(
          databaseObjectsType,
          builder.hideDependantDatabaseObjects.getOrDefault(databaseObjectsType, false));
    }
    hideNames = new EnumMap<>(HideDatabaseObjectNamesType.class);
    for (final HideDatabaseObjectNamesType databaseObjectNamesType :
        HideDatabaseObjectNamesType.values()) {
      hideNames.put(
          databaseObjectNamesType, builder.hideNames.getOrDefault(databaseObjectNamesType, false));
    }
  }

  public boolean is(final HideDatabaseObjectNamesType key) {
    return hideNames.getOrDefault(key, false);
  }

  public boolean is(final HideDatabaseObjectsType key) {
    return hideDatabaseObjects.getOrDefault(key, false);
  }

  public boolean is(final HideDependantDatabaseObjectsType key) {
    return hideDependantDatabaseObjects.getOrDefault(key, false);
  }

  public boolean isAlphabeticalSortForForeignKeys() {
    return isAlphabeticalSortForForeignKeys;
  }

  public boolean isAlphabeticalSortForIndexes() {
    return isAlphabeticalSortForIndexes;
  }

  public boolean isHideRemarks() {
    return isHideRemarks;
  }

  public boolean isHideTableRowCounts() {
    return isHideTableRowCounts;
  }

  public boolean isHideTriggerActionStatements() {
    return isHideTriggerActionStatements;
  }

  public boolean isShowOrdinalNumbers() {
    return isShowOrdinalNumbers;
  }

  public boolean isShowStandardColumnTypeNames() {
    return isShowStandardColumnTypeNames;
  }
}
