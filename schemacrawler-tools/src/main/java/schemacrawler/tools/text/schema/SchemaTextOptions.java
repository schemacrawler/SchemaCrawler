/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import schemacrawler.tools.text.base.BaseTextOptions;

public class SchemaTextOptions
  extends BaseTextOptions
{

  private static final long serialVersionUID = -8133661515343358712L;

  private boolean isAlphabeticalSortForForeignKeys;
  private boolean isAlphabeticalSortForIndexes;
  private boolean isHideForeignKeyNames;
  private boolean isHideIndexNames;
  private boolean isHidePrimaryKeyNames;
  private boolean isHideRemarks;
  private boolean isHideRoutineSpecificNames;
  private boolean isHideTableConstraintNames;
  private boolean isHideTriggerNames;
  private boolean isShowWeakAssociations;
  private boolean isShowOrdinalNumbers;
  private boolean isShowStandardColumnTypeNames;
  private boolean isShowRowCounts;

  public boolean isAlphabeticalSortForForeignKeys()
  {
    return isAlphabeticalSortForForeignKeys;
  }

  public boolean isAlphabeticalSortForIndexes()
  {
    return isAlphabeticalSortForIndexes;
  }

  public boolean isHideForeignKeyNames()
  {
    return isHideForeignKeyNames;
  }

  public boolean isHideIndexNames()
  {
    return isHideIndexNames;
  }

  public boolean isHidePrimaryKeyNames()
  {
    return isHidePrimaryKeyNames;
  }

  public boolean isHideRemarks()
  {
    return isHideRemarks;
  }

  public boolean isHideRoutineSpecificNames()
  {
    return isHideRoutineSpecificNames;
  }

  public boolean isHideTableConstraintNames()
  {
    return isHideTableConstraintNames;
  }

  public boolean isHideTriggerNames()
  {
    return isHideTriggerNames;
  }

  public boolean isShowWeakAssociations()
  {
    return isShowWeakAssociations;
  }

  public boolean isShowOrdinalNumbers()
  {
    return isShowOrdinalNumbers;
  }

  public boolean isShowRowCounts()
  {
    return isShowRowCounts;
  }

  public boolean isShowStandardColumnTypeNames()
  {
    return isShowStandardColumnTypeNames;
  }

  public void setAlphabeticalSortForForeignKeys(final boolean isAlphabeticalSortForForeignKeys)
  {
    this.isAlphabeticalSortForForeignKeys = isAlphabeticalSortForForeignKeys;
  }

  public void setAlphabeticalSortForIndexes(final boolean isAlphabeticalSortForIndexes)
  {
    this.isAlphabeticalSortForIndexes = isAlphabeticalSortForIndexes;
  }

  public void setHideConstraintNames(final boolean isHideTableConstraintNames)
  {
    this.isHideTableConstraintNames = isHideTableConstraintNames;
  }

  public void setHideForeignKeyNames(final boolean isHideForeignKeyNames)
  {
    this.isHideForeignKeyNames = isHideForeignKeyNames;
  }

  public void setHideIndexNames(final boolean isHideIndexNames)
  {
    this.isHideIndexNames = isHideIndexNames;
  }

  public void setHidePrimaryKeyNames(final boolean isHidePrimaryKeyNames)
  {
    this.isHidePrimaryKeyNames = isHidePrimaryKeyNames;
  }

  public void setHideRemarks(final boolean isHideRemarks)
  {
    this.isHideRemarks = isHideRemarks;
  }

  public void setHideRoutineSpecificNames(final boolean isHideRoutineSpecificNames)
  {
    this.isHideRoutineSpecificNames = isHideRoutineSpecificNames;
  }

  public void setHideTableConstraintNames(final boolean isHideTableConstraintNames)
  {
    this.isHideTableConstraintNames = isHideTableConstraintNames;
  }

  public void setHideTriggerNames(final boolean isHideTriggerNames)
  {
    this.isHideTriggerNames = isHideTriggerNames;
  }

  public void setShowWeakAssociations(final boolean isShowWeakAssociations)
  {
    this.isShowWeakAssociations = isShowWeakAssociations;
  }

  public void setShowOrdinalNumbers(final boolean isShowOrdinalNumbers)
  {
    this.isShowOrdinalNumbers = isShowOrdinalNumbers;
  }

  public void setShowRowCounts(final boolean isShowRowCounts)
  {
    this.isShowRowCounts = isShowRowCounts;
  }

  public void setShowStandardColumnTypeNames(final boolean isShowStandardColumnTypeNames)
  {
    this.isShowStandardColumnTypeNames = isShowStandardColumnTypeNames;
  }

}
