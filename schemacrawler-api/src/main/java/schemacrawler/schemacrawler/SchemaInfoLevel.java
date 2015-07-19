/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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

package schemacrawler.schemacrawler;


/**
 * Descriptor for level of schema detail.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaInfoLevel
  implements Options
{

  private static final long serialVersionUID = -6721986729175552425L;

  private String tag;

  private boolean retrieveTables;
  private boolean retrieveRoutines;
  private boolean retrieveColumnDataTypes;
  private boolean retrieveAdditionalDatabaseInfo;
  private boolean retrieveAdditionalJdbcDriverInfo;
  private boolean retrieveUserDefinedColumnDataTypes;
  private boolean retrieveRoutineColumns;
  private boolean retrieveRoutineInformation;
  private boolean retrieveTableConstraintInformation;
  private boolean retrieveViewInformation;
  private boolean retrieveIndexInformation;
  private boolean retrieveForeignKeys;
  private boolean retrieveIndexes;
  private boolean retrieveTablePrivileges;
  private boolean retrieveTableColumnPrivileges;
  private boolean retrieveTriggerInformation;
  private boolean retrieveSynonymInformation;
  private boolean retrieveSequenceInformation;
  private boolean retrieveTableColumns;
  private boolean retrieveAdditionalTableAttributes;
  private boolean retrieveAdditionalColumnAttributes;
  private boolean retrieveTableDefinitionsInformation;

  public String getTag()
  {
    return tag;
  }

  public boolean isRetrieveAdditionalColumnAttributes()
  {
    return retrieveAdditionalColumnAttributes;
  }

  public boolean isRetrieveAdditionalDatabaseInfo()
  {
    return retrieveAdditionalDatabaseInfo;
  }

  public boolean isRetrieveAdditionalJdbcDriverInfo()
  {
    return retrieveAdditionalJdbcDriverInfo;
  }

  public boolean isRetrieveAdditionalTableAttributes()
  {
    return retrieveAdditionalTableAttributes;
  }

  public boolean isRetrieveColumnDataTypes()
  {
    return retrieveColumnDataTypes;
  }

  public boolean isRetrieveForeignKeys()
  {
    return retrieveForeignKeys;
  }

  public boolean isRetrieveIndexes()
  {
    return retrieveIndexes;
  }

  public boolean isRetrieveIndexInformation()
  {
    return retrieveIndexInformation;
  }

  public boolean isRetrieveRoutineColumns()
  {
    return retrieveRoutineColumns;
  }

  public boolean isRetrieveRoutineInformation()
  {
    return retrieveRoutineInformation;
  }

  public boolean isRetrieveRoutines()
  {
    return retrieveRoutines;
  }

  public boolean isRetrieveSequenceInformation()
  {
    return retrieveSequenceInformation;
  }

  public boolean isRetrieveSynonymInformation()
  {
    return retrieveSynonymInformation;
  }

  public boolean isRetrieveTableColumnPrivileges()
  {
    return retrieveTableColumnPrivileges;
  }

  public boolean isRetrieveTableColumns()
  {
    return retrieveTableColumns;
  }

  public boolean isRetrieveTableConstraintInformation()
  {
    return retrieveTableConstraintInformation;
  }

  public boolean isRetrieveTableDefinitionsInformation()
  {
    return retrieveTableDefinitionsInformation;
  }

  public boolean isRetrieveTablePrivileges()
  {
    return retrieveTablePrivileges;
  }

  public boolean isRetrieveTables()
  {
    return retrieveTables;
  }

  public boolean isRetrieveTriggerInformation()
  {
    return retrieveTriggerInformation;
  }

  public boolean isRetrieveUserDefinedColumnDataTypes()
  {
    return retrieveUserDefinedColumnDataTypes;
  }

  public boolean isRetrieveViewInformation()
  {
    return retrieveViewInformation;
  }

  public void setRetrieveAdditionalColumnAttributes(final boolean retrieveAdditionalColumnAttributes)
  {
    this.retrieveAdditionalColumnAttributes = retrieveAdditionalColumnAttributes;
  }

  public void setRetrieveAdditionalDatabaseInfo(final boolean retrieveAdditionalDatabaseInfo)
  {
    this.retrieveAdditionalDatabaseInfo = retrieveAdditionalDatabaseInfo;
  }

  public void setRetrieveAdditionalJdbcDriverInfo(final boolean retrieveAdditionalJdbcDriverInfo)
  {
    this.retrieveAdditionalJdbcDriverInfo = retrieveAdditionalJdbcDriverInfo;
  }

  public void setRetrieveAdditionalTableAttributes(final boolean retrieveAdditionalTableAttributes)
  {
    this.retrieveAdditionalTableAttributes = retrieveAdditionalTableAttributes;
  }

  public void setRetrieveColumnDataTypes(final boolean retrieveColumnDataTypes)
  {
    this.retrieveColumnDataTypes = retrieveColumnDataTypes;
  }

  public void setRetrieveForeignKeys(final boolean retrieveForeignKeys)
  {
    this.retrieveForeignKeys = retrieveForeignKeys;
  }

  public void setRetrieveIndexes(final boolean retrieveIndexes)
  {
    this.retrieveIndexes = retrieveIndexes;
  }

  public void setRetrieveIndexInformation(final boolean retrieveIndexInformation)
  {
    this.retrieveIndexInformation = retrieveIndexInformation;
  }

  public void setRetrieveRoutineColumns(final boolean retrieveRoutineColumns)
  {
    this.retrieveRoutineColumns = retrieveRoutineColumns;
  }

  public void setRetrieveRoutineInformation(final boolean retrieveRoutineInformation)
  {
    this.retrieveRoutineInformation = retrieveRoutineInformation;
  }

  public void setRetrieveRoutines(final boolean retrieveRoutines)
  {
    this.retrieveRoutines = retrieveRoutines;
  }

  public void setRetrieveSequenceInformation(final boolean retrieveSequenceInformation)
  {
    this.retrieveSequenceInformation = retrieveSequenceInformation;
  }

  public void setRetrieveSynonymInformation(final boolean retrieveSynonymInformation)
  {
    this.retrieveSynonymInformation = retrieveSynonymInformation;
  }

  public void setRetrieveTableColumnPrivileges(final boolean retrieveTableColumnPrivileges)
  {
    this.retrieveTableColumnPrivileges = retrieveTableColumnPrivileges;
  }

  public void setRetrieveTableColumns(final boolean retrieveTableColumns)
  {
    this.retrieveTableColumns = retrieveTableColumns;
  }

  public void setRetrieveTableConstraintInformation(final boolean retrieveTableConstraintInformation)
  {
    this.retrieveTableConstraintInformation = retrieveTableConstraintInformation;
  }

  public void setRetrieveTableDefinitionsInformation(final boolean retrieveTableDefinitionsInformation)
  {
    this.retrieveTableDefinitionsInformation = retrieveTableDefinitionsInformation;
  }

  public void setRetrieveTablePrivileges(final boolean retrieveTablePrivileges)
  {
    this.retrieveTablePrivileges = retrieveTablePrivileges;
  }

  public void setRetrieveTables(final boolean retrieveTables)
  {
    this.retrieveTables = retrieveTables;
  }

  public void setRetrieveTriggerInformation(final boolean retrieveTriggerInformation)
  {
    this.retrieveTriggerInformation = retrieveTriggerInformation;
  }

  public void setRetrieveUserDefinedColumnDataTypes(final boolean retrieveUserDefinedColumnDataTypes)
  {
    this.retrieveUserDefinedColumnDataTypes = retrieveUserDefinedColumnDataTypes;
  }

  public void setRetrieveViewInformation(final boolean retrieveViewInformation)
  {
    this.retrieveViewInformation = retrieveViewInformation;
  }

  public void setTag(final String tag)
  {
    this.tag = tag;
  }

  @Override
  public String toString()
  {
    return tag == null? "": tag;
  }

}
