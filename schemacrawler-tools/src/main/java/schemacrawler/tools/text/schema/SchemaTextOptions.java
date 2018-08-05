/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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

  public boolean isShowWeakAssociations()
  {
    return isShowWeakAssociations;
  }

  protected void setAlphabeticalSortForForeignKeys(final boolean isAlphabeticalSortForForeignKeys)
  {
    this.isAlphabeticalSortForForeignKeys = isAlphabeticalSortForForeignKeys;
  }

  protected void setAlphabeticalSortForIndexes(final boolean isAlphabeticalSortForIndexes)
  {
    this.isAlphabeticalSortForIndexes = isAlphabeticalSortForIndexes;
  }

  protected void setHideForeignKeyNames(final boolean isHideForeignKeyNames)
  {
    this.isHideForeignKeyNames = isHideForeignKeyNames;
  }

  protected void setHideIndexNames(final boolean isHideIndexNames)
  {
    this.isHideIndexNames = isHideIndexNames;
  }

  protected void setHidePrimaryKeyNames(final boolean isHidePrimaryKeyNames)
  {
    this.isHidePrimaryKeyNames = isHidePrimaryKeyNames;
  }

  protected void setHideRemarks(final boolean isHideRemarks)
  {
    this.isHideRemarks = isHideRemarks;
  }

  protected void setHideRoutineSpecificNames(final boolean isHideRoutineSpecificNames)
  {
    this.isHideRoutineSpecificNames = isHideRoutineSpecificNames;
  }

  protected void setHideTableConstraintNames(final boolean isHideTableConstraintNames)
  {
    this.isHideTableConstraintNames = isHideTableConstraintNames;
  }

  protected void setHideTriggerNames(final boolean isHideTriggerNames)
  {
    this.isHideTriggerNames = isHideTriggerNames;
  }

  protected void setShowOrdinalNumbers(final boolean isShowOrdinalNumbers)
  {
    this.isShowOrdinalNumbers = isShowOrdinalNumbers;
  }

  protected void setShowRowCounts(final boolean isShowRowCounts)
  {
    this.isShowRowCounts = isShowRowCounts;
  }

  protected void setShowStandardColumnTypeNames(final boolean isShowStandardColumnTypeNames)
  {
    this.isShowStandardColumnTypeNames = isShowStandardColumnTypeNames;
  }

  protected void setShowWeakAssociations(final boolean isShowWeakAssociations)
  {
    this.isShowWeakAssociations = isShowWeakAssociations;
  }

}
