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

import java.util.Objects;

import schemacrawler.tools.text.options.BaseTextOptions;

public class SchemaTextOptions extends BaseTextOptions {

  private final boolean isAlphabeticalSortForForeignKeys;
  private final boolean isAlphabeticalSortForIndexes;
  private final boolean isHideForeignKeyNames;
  private final boolean isHideWeakAssociationNames;
  private final boolean isHideIndexNames;
  private final boolean isHidePrimaryKeyNames;
  private final boolean isHideRemarks;
  private final boolean isHideRoutineSpecificNames;
  private final boolean isHideTableConstraintNames;
  private final boolean isHideTriggerNames;
  private final boolean isShowOrdinalNumbers;
  private final boolean isShowStandardColumnTypeNames;
  private final boolean isHideTableRowCounts;

  protected SchemaTextOptions(
      final BaseSchemaTextOptionsBuilder<?, ? extends SchemaTextOptions> builder) {
    super(builder);

    isAlphabeticalSortForForeignKeys = builder.isAlphabeticalSortForForeignKeys;
    isAlphabeticalSortForIndexes = builder.isAlphabeticalSortForIndexes;
    isHideForeignKeyNames = builder.isHideForeignKeyNames;
    isHideWeakAssociationNames = builder.isHideWeakAssociationNames;
    isHideIndexNames = builder.isHideIndexNames;
    isHidePrimaryKeyNames = builder.isHidePrimaryKeyNames;
    isHideRemarks = builder.isHideRemarks;
    isHideRoutineSpecificNames = builder.isHideRoutineSpecificNames;
    isHideTableConstraintNames = builder.isHideTableConstraintNames;
    isHideTriggerNames = builder.isHideTriggerNames;
    isShowOrdinalNumbers = builder.isShowOrdinalNumbers;
    isShowStandardColumnTypeNames = builder.isShowStandardColumnTypeNames;
    isHideTableRowCounts = builder.isHideTableRowCounts;
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
        && isHideForeignKeyNames == that.isHideForeignKeyNames
        && isHideWeakAssociationNames == that.isHideWeakAssociationNames
        && isHideIndexNames == that.isHideIndexNames
        && isHidePrimaryKeyNames == that.isHidePrimaryKeyNames
        && isHideRemarks == that.isHideRemarks
        && isHideRoutineSpecificNames == that.isHideRoutineSpecificNames
        && isHideTableConstraintNames == that.isHideTableConstraintNames
        && isHideTriggerNames == that.isHideTriggerNames
        && isShowOrdinalNumbers == that.isShowOrdinalNumbers
        && isShowStandardColumnTypeNames == that.isShowStandardColumnTypeNames
        && isHideTableRowCounts == that.isHideTableRowCounts;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        isAlphabeticalSortForForeignKeys,
        isAlphabeticalSortForIndexes,
        isHideForeignKeyNames,
        isHideWeakAssociationNames,
        isHideIndexNames,
        isHidePrimaryKeyNames,
        isHideRemarks,
        isHideRoutineSpecificNames,
        isHideTableConstraintNames,
        isHideTriggerNames,
        isShowOrdinalNumbers,
        isShowStandardColumnTypeNames,
        isHideTableRowCounts);
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

  public boolean isHideTableRowCounts() {
    return isHideTableRowCounts;
  }

  public boolean isHideTriggerNames() {
    return isHideTriggerNames;
  }

  public boolean isHideWeakAssociationNames() {
    return isHideWeakAssociationNames;
  }

  public boolean isShowOrdinalNumbers() {
    return isShowOrdinalNumbers;
  }

  public boolean isShowStandardColumnTypeNames() {
    return isShowStandardColumnTypeNames;
  }
}
