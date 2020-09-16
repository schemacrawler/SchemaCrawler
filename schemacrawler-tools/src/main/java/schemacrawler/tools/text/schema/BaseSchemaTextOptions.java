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

import java.util.Objects;

import schemacrawler.tools.text.base.BaseTextOptions;

public abstract class BaseSchemaTextOptions extends BaseTextOptions {

  private final boolean isAlphabeticalSortForForeignKeys;
  private final boolean isAlphabeticalSortForIndexes;
  private final boolean isHideForeignKeyNames;
  private final boolean isHideIndexNames;
  private final boolean isHidePrimaryKeyNames;
  private final boolean isHideRemarks;
  private final boolean isHideRoutineSpecificNames;
  private final boolean isHideTableConstraintNames;
  private final boolean isHideTriggerNames;
  private final boolean isShowWeakAssociations;
  private final boolean isShowOrdinalNumbers;
  private final boolean isShowStandardColumnTypeNames;
  private final boolean isShowRowCounts;

  protected BaseSchemaTextOptions(
      final BaseSchemaTextOptionsBuilder<?, ? extends BaseSchemaTextOptions> builder) {
    super(builder);

    isAlphabeticalSortForForeignKeys = builder.isAlphabeticalSortForForeignKeys;
    isAlphabeticalSortForIndexes = builder.isAlphabeticalSortForIndexes;
    isHideForeignKeyNames = builder.isHideForeignKeyNames;
    isHideIndexNames = builder.isHideIndexNames;
    isHidePrimaryKeyNames = builder.isHidePrimaryKeyNames;
    isHideRemarks = builder.isHideRemarks;
    isHideRoutineSpecificNames = builder.isHideRoutineSpecificNames;
    isHideTableConstraintNames = builder.isHideTableConstraintNames;
    isHideTriggerNames = builder.isHideTriggerNames;
    isShowWeakAssociations = builder.isShowWeakAssociations;
    isShowOrdinalNumbers = builder.isShowOrdinalNumbers;
    isShowStandardColumnTypeNames = builder.isShowStandardColumnTypeNames;
    isShowRowCounts = builder.isShowRowCounts;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BaseSchemaTextOptions)) {
      return false;
    }
    final BaseSchemaTextOptions that = (BaseSchemaTextOptions) o;
    return isAlphabeticalSortForForeignKeys == that.isAlphabeticalSortForForeignKeys
        && isAlphabeticalSortForIndexes == that.isAlphabeticalSortForIndexes
        && isHideForeignKeyNames == that.isHideForeignKeyNames
        && isHideIndexNames == that.isHideIndexNames
        && isHidePrimaryKeyNames == that.isHidePrimaryKeyNames
        && isHideRemarks == that.isHideRemarks
        && isHideRoutineSpecificNames == that.isHideRoutineSpecificNames
        && isHideTableConstraintNames == that.isHideTableConstraintNames
        && isHideTriggerNames == that.isHideTriggerNames
        && isShowWeakAssociations == that.isShowWeakAssociations
        && isShowOrdinalNumbers == that.isShowOrdinalNumbers
        && isShowStandardColumnTypeNames == that.isShowStandardColumnTypeNames
        && isShowRowCounts == that.isShowRowCounts;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        isAlphabeticalSortForForeignKeys,
        isAlphabeticalSortForIndexes,
        isHideForeignKeyNames,
        isHideIndexNames,
        isHidePrimaryKeyNames,
        isHideRemarks,
        isHideRoutineSpecificNames,
        isHideTableConstraintNames,
        isHideTriggerNames,
        isShowWeakAssociations,
        isShowOrdinalNumbers,
        isShowStandardColumnTypeNames,
        isShowRowCounts);
  }

  public boolean isAlphabeticalSortForForeignKeys() {
    return isAlphabeticalSortForForeignKeys;
  }

  public boolean isAlphabeticalSortForIndexes() {
    return isAlphabeticalSortForIndexes;
  }

  public boolean isHideForeignKeyNames() {
    return isHideForeignKeyNames;
  }

  public boolean isHideIndexNames() {
    return isHideIndexNames;
  }

  public boolean isHidePrimaryKeyNames() {
    return isHidePrimaryKeyNames;
  }

  public boolean isHideRemarks() {
    return isHideRemarks;
  }

  public boolean isHideRoutineSpecificNames() {
    return isHideRoutineSpecificNames;
  }

  public boolean isHideTableConstraintNames() {
    return isHideTableConstraintNames;
  }

  public boolean isHideTriggerNames() {
    return isHideTriggerNames;
  }

  public boolean isShowOrdinalNumbers() {
    return isShowOrdinalNumbers;
  }

  public boolean isShowRowCounts() {
    return isShowRowCounts;
  }

  public boolean isShowStandardColumnTypeNames() {
    return isShowStandardColumnTypeNames;
  }

  public boolean isShowWeakAssociations() {
    return isShowWeakAssociations;
  }
}
