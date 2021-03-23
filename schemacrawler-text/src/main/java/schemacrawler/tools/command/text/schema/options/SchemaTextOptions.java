/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.util.Objects;

import schemacrawler.tools.text.options.BaseTextOptions;

public class SchemaTextOptions extends BaseTextOptions {

  private final boolean isAlphabeticalSortForForeignKeys;
  private final boolean isAlphabeticalSortForIndexes;
  private final boolean isHideRemarks;
  private final boolean isShowOrdinalNumbers;
  private final boolean isShowStandardColumnTypeNames;
  private final boolean isHideTableRowCounts;
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

    hideNames = new EnumMap<>(HideDatabaseObjectNamesType.class);
    for (final HideDatabaseObjectNamesType databaseObjectNamesType : HideDatabaseObjectNamesType.values()) {
      hideNames.put(
          databaseObjectNamesType, builder.hideNames.getOrDefault(databaseObjectNamesType, false));
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SchemaTextOptions)) {
      return false;
    }
    final SchemaTextOptions that = (SchemaTextOptions) o;
    return isAlphabeticalSortForForeignKeys == that.isAlphabeticalSortForForeignKeys
        && isAlphabeticalSortForIndexes == that.isAlphabeticalSortForIndexes
        && isHideRemarks == that.isHideRemarks
        && isShowOrdinalNumbers == that.isShowOrdinalNumbers
        && isShowStandardColumnTypeNames == that.isShowStandardColumnTypeNames
        && isHideTableRowCounts == that.isHideTableRowCounts
        && hideNames.equals(that.hideNames);
  }

  public boolean get(final HideDatabaseObjectNamesType key) {
    return hideNames.getOrDefault(key, false);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        isAlphabeticalSortForForeignKeys,
        isAlphabeticalSortForIndexes,
        isHideRemarks,
        isShowOrdinalNumbers,
        isShowStandardColumnTypeNames,
        isHideTableRowCounts,
        hideNames);
  }

  public boolean isAlphabeticalSortForForeignKeys() {
    return isAlphabeticalSortForForeignKeys;
  }

  public boolean isAlphabeticalSortForIndexes() {
    return isAlphabeticalSortForIndexes;
  }

  public boolean isHideAlternateKeyNames() {
    return get(HideDatabaseObjectNamesType.hideAlternateKeyNames);
  }

  public boolean isHideForeignKeyNames() {
    return get(HideDatabaseObjectNamesType.hideForeignKeyNames);
  }

  public boolean isHideIndexNames() {
    return get(HideDatabaseObjectNamesType.hideIndexNames);
  }

  public boolean isHidePrimaryKeyNames() {
    return get(HideDatabaseObjectNamesType.hidePrimaryKeyNames);
  }

  public boolean isHideRemarks() {
    return isHideRemarks;
  }

  public boolean isHideRoutineSpecificNames() {
    return get(HideDatabaseObjectNamesType.hideRoutineSpecificNames);
  }

  public boolean isHideTableConstraintNames() {
    return get(HideDatabaseObjectNamesType.hideTableConstraintNames);
  }

  public boolean isHideTableRowCounts() {
    return isHideTableRowCounts;
  }

  public boolean isHideTriggerNames() {
    return get(HideDatabaseObjectNamesType.hideTriggerNames);
  }

  public boolean isHideWeakAssociationNames() {
    return get(HideDatabaseObjectNamesType.hideWeakAssociationNames);
  }

  public boolean isShowOrdinalNumbers() {
    return isShowOrdinalNumbers;
  }

  public boolean isShowStandardColumnTypeNames() {
    return isShowStandardColumnTypeNames;
  }
}
