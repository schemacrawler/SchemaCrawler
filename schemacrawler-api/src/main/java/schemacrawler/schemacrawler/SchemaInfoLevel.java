/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

  private final String tag;

  private final boolean retrieveTables;
  private final boolean retrieveRoutines;
  private final boolean retrieveColumnDataTypes;
  private final boolean retrieveDatabaseInfo;
  private final boolean retrieveAdditionalDatabaseInfo;
  private final boolean retrieveServerInfo;
  private final boolean retrieveAdditionalJdbcDriverInfo;
  private final boolean retrieveUserDefinedColumnDataTypes;
  private final boolean retrieveRoutineColumns;
  private final boolean retrieveRoutineInformation;
  private final boolean retrieveTableConstraintInformation;
  private final boolean retrieveTableConstraintDefinitions;
  private final boolean retrieveViewInformation;
  private final boolean retrieveIndexInformation;
  private final boolean retrieveIndexColumnInformation;
  private final boolean retrievePrimaryKeyDefinitions;
  private final boolean retrieveForeignKeys;
  private final boolean retrieveForeignKeyDefinitions;
  private final boolean retrieveIndexes;
  private final boolean retrieveTablePrivileges;
  private final boolean retrieveTableColumnPrivileges;
  private final boolean retrieveTriggerInformation;
  private final boolean retrieveSynonymInformation;
  private final boolean retrieveSequenceInformation;
  private final boolean retrieveTableColumns;
  private final boolean retrieveAdditionalTableAttributes;
  private final boolean retrieveAdditionalColumnAttributes;
  private final boolean retrieveTableDefinitionsInformation;

  SchemaInfoLevel(final String tag,
                  final boolean retrieveTables,
                  final boolean retrieveRoutines,
                  final boolean retrieveColumnDataTypes,
                  final boolean retrieveDatabaseInfo,
                  final boolean retrieveAdditionalDatabaseInfo,
                  final boolean retrieveServerInfo,
                  final boolean retrieveAdditionalJdbcDriverInfo,
                  final boolean retrieveUserDefinedColumnDataTypes,
                  final boolean retrieveRoutineColumns,
                  final boolean retrieveRoutineInformation,
                  final boolean retrieveTableConstraintInformation,
                  final boolean retrieveTableConstraintDefinitions,
                  final boolean retrieveViewInformation,
                  final boolean retrieveIndexInformation,
                  final boolean retrieveIndexColumnInformation,
                  final boolean retrievePrimaryKeyDefinitions,
                  final boolean retrieveForeignKeys,
                  final boolean retrieveForeignKeyDefinitions,
                  final boolean retrieveIndexes,
                  final boolean retrieveTablePrivileges,
                  final boolean retrieveTableColumnPrivileges,
                  final boolean retrieveTriggerInformation,
                  final boolean retrieveSynonymInformation,
                  final boolean retrieveSequenceInformation,
                  final boolean retrieveTableColumns,
                  final boolean retrieveAdditionalTableAttributes,
                  final boolean retrieveAdditionalColumnAttributes,
                  final boolean retrieveTableDefinitionsInformation)
  {
    this.tag = tag;
    this.retrieveTables = retrieveTables;
    this.retrieveRoutines = retrieveRoutines;
    this.retrieveColumnDataTypes = retrieveColumnDataTypes;
    this.retrieveDatabaseInfo = retrieveDatabaseInfo;
    this.retrieveAdditionalDatabaseInfo = retrieveAdditionalDatabaseInfo;
    this.retrieveServerInfo = retrieveServerInfo;
    this.retrieveAdditionalJdbcDriverInfo = retrieveAdditionalJdbcDriverInfo;
    this.retrieveUserDefinedColumnDataTypes = retrieveUserDefinedColumnDataTypes;
    this.retrieveRoutineColumns = retrieveRoutineColumns;
    this.retrieveRoutineInformation = retrieveRoutineInformation;
    this.retrieveTableConstraintInformation = retrieveTableConstraintInformation;
    this.retrieveTableConstraintDefinitions = retrieveTableConstraintDefinitions;
    this.retrieveViewInformation = retrieveViewInformation;
    this.retrieveIndexInformation = retrieveIndexInformation;
    this.retrieveIndexColumnInformation = retrieveIndexColumnInformation;
    this.retrievePrimaryKeyDefinitions = retrievePrimaryKeyDefinitions;
    this.retrieveForeignKeys = retrieveForeignKeys;
    this.retrieveForeignKeyDefinitions = retrieveForeignKeyDefinitions;
    this.retrieveIndexes = retrieveIndexes;
    this.retrieveTablePrivileges = retrieveTablePrivileges;
    this.retrieveTableColumnPrivileges = retrieveTableColumnPrivileges;
    this.retrieveTriggerInformation = retrieveTriggerInformation;
    this.retrieveSynonymInformation = retrieveSynonymInformation;
    this.retrieveSequenceInformation = retrieveSequenceInformation;
    this.retrieveTableColumns = retrieveTableColumns;
    this.retrieveAdditionalTableAttributes = retrieveAdditionalTableAttributes;
    this.retrieveAdditionalColumnAttributes = retrieveAdditionalColumnAttributes;
    this.retrieveTableDefinitionsInformation = retrieveTableDefinitionsInformation;
  }

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

  public boolean isRetrieveDatabaseInfo()
  {
    return retrieveDatabaseInfo;
  }

  public boolean isRetrieveForeignKeyDefinitions()
  {
    return retrieveForeignKeyDefinitions;
  }

  public boolean isRetrieveForeignKeys()
  {
    return retrieveForeignKeys;
  }

  public boolean isRetrieveIndexColumnInformation()
  {
    return retrieveIndexColumnInformation;
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

  public boolean isRetrieveServerInfo()
  {
    return retrieveServerInfo;
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

  @Override
  public String toString()
  {
    return tag == null? "": tag;
  }

}
