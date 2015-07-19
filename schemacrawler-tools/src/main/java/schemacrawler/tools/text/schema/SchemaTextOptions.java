/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
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
  private boolean isShowOrdinalNumbers;
  private boolean isShowStandardColumnTypeNames;

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

  public void setHideTriggerNames(final boolean isHideTriggerNames)
  {
    this.isHideTriggerNames = isHideTriggerNames;
  }

  public void setShowOrdinalNumbers(final boolean isShowOrdinalNumbers)
  {
    this.isShowOrdinalNumbers = isShowOrdinalNumbers;
  }

  public void setShowStandardColumnTypeNames(final boolean isShowStandardColumnTypeNames)
  {
    this.isShowStandardColumnTypeNames = isShowStandardColumnTypeNames;
  }

}
