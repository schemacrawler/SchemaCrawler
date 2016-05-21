/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
  private boolean retrieveTableConstraintDefinitions;
  private boolean retrieveViewInformation;
  private boolean retrieveIndexInformation;
  private boolean retrievePrimaryKeyDefinitions;
  private boolean retrieveForeignKeys;
  private boolean retrieveForeignKeyDefinitions;
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
  private boolean retrieveHiddenTableColumns;

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

  public boolean isRetrieveForeignKeyDefinitions()
  {
    return retrieveForeignKeyDefinitions;
  }

  public boolean isRetrieveForeignKeys()
  {
    return retrieveForeignKeys;
  }

  public boolean isRetrieveHiddenTableColumns()
  {
    return retrieveHiddenTableColumns;
  }

  public boolean isRetrieveIndexes()
  {
    return retrieveIndexes;
  }

  public boolean isRetrieveIndexInformation()
  {
    return retrieveIndexInformation;
  }

  public boolean isRetrievePrimaryKeyDefinitions()
  {
    return retrievePrimaryKeyDefinitions;
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

  public boolean isRetrieveTableConstraintDefinitions()
  {
    return retrieveTableConstraintDefinitions;
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

  public void setRetrieveForeignKeyDefinitions(final boolean retrieveForeignKeyDefinitions)
  {
    this.retrieveForeignKeyDefinitions = retrieveForeignKeyDefinitions;
  }

  public void setRetrieveForeignKeys(final boolean retrieveForeignKeys)
  {
    this.retrieveForeignKeys = retrieveForeignKeys;
  }

  public void setRetrieveHiddenTableColumns(final boolean retrieveHiddenTableColumns)
  {
    this.retrieveHiddenTableColumns = retrieveHiddenTableColumns;
  }

  public void setRetrieveIndexes(final boolean retrieveIndexes)
  {
    this.retrieveIndexes = retrieveIndexes;
  }

  public void setRetrieveIndexInformation(final boolean retrieveIndexInformation)
  {
    this.retrieveIndexInformation = retrieveIndexInformation;
  }

  public void setRetrievePrimaryKeyDefinitions(final boolean retrievePrimaryKeyDefinitions)
  {
    this.retrievePrimaryKeyDefinitions = retrievePrimaryKeyDefinitions;
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

  public void setRetrieveTableConstraintDefinitions(final boolean retrieveTableConstraintDefinitions)
  {
    this.retrieveTableConstraintDefinitions = retrieveTableConstraintDefinitions;
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
