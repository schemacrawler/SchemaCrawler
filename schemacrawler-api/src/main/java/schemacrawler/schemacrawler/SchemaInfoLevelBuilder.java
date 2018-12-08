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


public final class SchemaInfoLevelBuilder
  implements OptionsBuilder<SchemaInfoLevelBuilder, SchemaInfoLevel>
{

  /**
   * Creates a new SchemaInfoLevel for verbose schema information.
   *
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevelBuilder detailed()
  {
    final SchemaInfoLevelBuilder detailed = standard();
    detailed.setRetrieveUserDefinedColumnDataTypes(true);
    detailed.setRetrieveTriggerInformation(true);
    detailed.setRetrieveTableConstraintInformation(true);
    detailed.setRetrieveTableConstraintDefinitions(true);
    detailed.setRetrieveViewInformation(true);
    detailed.setRetrieveRoutineInformation(true);
    detailed.setTag("detailed");
    return detailed;
  }

  /**
   * Creates a new SchemaInfoLevel for maximum schema information.
   *
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevelBuilder maximum()
  {
    final SchemaInfoLevelBuilder maximum = detailed();
    maximum.setRetrieveAdditionalDatabaseInfo(true);
    maximum.setRetrieveServerInfo(true);
    maximum.setRetrieveAdditionalJdbcDriverInfo(true);
    maximum.setRetrieveTablePrivileges(true);
    maximum.setRetrieveTableColumnPrivileges(true);
    maximum.setRetrieveTableDefinitionsInformation(true);
    maximum.setRetrieveForeignKeyDefinitions(true);
    maximum.setRetrievePrimaryKeyDefinitions(true);
    maximum.setRetrieveAdditionalTableAttributes(true);
    maximum.setRetrieveAdditionalColumnAttributes(true);
    maximum.setRetrieveIndexInformation(true);
    maximum.setRetrieveIndexColumnInformation(true);
    maximum.setRetrieveSequenceInformation(true);
    maximum.setRetrieveSynonymInformation(true);
    maximum.setTag("maximum");
    return maximum;
  }

  /**
   * Creates a new SchemaInfoLevel for minimum schema information.
   *
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevelBuilder minimum()
  {
    final SchemaInfoLevelBuilder minimum = new SchemaInfoLevelBuilder();
    minimum.setRetrieveDatabaseInfo(true);
    minimum.setRetrieveTables(true);
    minimum.setRetrieveRoutines(true);
    minimum.setTag("minimum");
    return minimum;
  }

  /**
   * Creates a new SchemaInfoLevel for standard schema information.
   *
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevelBuilder standard()
  {
    final SchemaInfoLevelBuilder standard = minimum();
    standard.setRetrieveColumnDataTypes(true);
    standard.setRetrieveTableColumns(true);
    standard.setRetrieveForeignKeys(true);
    standard.setRetrieveIndexes(true);
    standard.setRetrieveRoutineColumns(true);
    standard.setTag("standard");
    return standard;
  }

  private String tag;

  private boolean retrieveTables;
  private boolean retrieveRoutines;
  private boolean retrieveColumnDataTypes;
  private boolean retrieveDatabaseInfo;
  private boolean retrieveAdditionalDatabaseInfo;
  private boolean retrieveServerInfo;
  private boolean retrieveAdditionalJdbcDriverInfo;
  private boolean retrieveUserDefinedColumnDataTypes;
  private boolean retrieveRoutineColumns;
  private boolean retrieveRoutineInformation;
  private boolean retrieveTableConstraintInformation;
  private boolean retrieveTableConstraintDefinitions;
  private boolean retrieveViewInformation;
  private boolean retrieveIndexInformation;
  private boolean retrieveIndexColumnInformation;
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

  private SchemaInfoLevelBuilder()
  {
    // Retrieve nothing
  }

  @Override
  public SchemaInfoLevelBuilder fromConfig(final Config config)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SchemaInfoLevelBuilder fromOptions(final SchemaInfoLevel schemaInfoLevel)
  {
    if (schemaInfoLevel == null)
    {
      return this;
    }

    tag = schemaInfoLevel.getTag();

    retrieveTables = schemaInfoLevel.isRetrieveTables();
    retrieveRoutines = schemaInfoLevel.isRetrieveRoutines();
    retrieveColumnDataTypes = schemaInfoLevel.isRetrieveColumnDataTypes();
    retrieveDatabaseInfo = schemaInfoLevel.isRetrieveDatabaseInfo();
    retrieveAdditionalDatabaseInfo = schemaInfoLevel
      .isRetrieveAdditionalDatabaseInfo();
    retrieveServerInfo = schemaInfoLevel.isRetrieveServerInfo();
    retrieveAdditionalJdbcDriverInfo = schemaInfoLevel
      .isRetrieveAdditionalJdbcDriverInfo();
    retrieveUserDefinedColumnDataTypes = schemaInfoLevel
      .isRetrieveUserDefinedColumnDataTypes();
    retrieveRoutineColumns = schemaInfoLevel.isRetrieveRoutineColumns();
    retrieveRoutineInformation = schemaInfoLevel.isRetrieveRoutineInformation();
    retrieveTableConstraintInformation = schemaInfoLevel
      .isRetrieveTableConstraintInformation();
    retrieveTableConstraintDefinitions = schemaInfoLevel
      .isRetrieveTableConstraintDefinitions();
    retrieveViewInformation = schemaInfoLevel.isRetrieveViewInformation();
    retrieveIndexInformation = schemaInfoLevel.isRetrieveIndexInformation();
    retrieveIndexColumnInformation = schemaInfoLevel
      .isRetrieveIndexColumnInformation();
    retrievePrimaryKeyDefinitions = schemaInfoLevel
      .isRetrievePrimaryKeyDefinitions();
    retrieveForeignKeys = schemaInfoLevel.isRetrieveForeignKeys();
    retrieveForeignKeyDefinitions = schemaInfoLevel
      .isRetrieveForeignKeyDefinitions();
    retrieveIndexes = schemaInfoLevel.isRetrieveIndexes();
    retrieveTablePrivileges = schemaInfoLevel.isRetrieveTablePrivileges();
    retrieveTableColumnPrivileges = schemaInfoLevel
      .isRetrieveTableColumnPrivileges();
    retrieveTriggerInformation = schemaInfoLevel.isRetrieveTriggerInformation();
    retrieveSynonymInformation = schemaInfoLevel.isRetrieveSynonymInformation();
    retrieveSequenceInformation = schemaInfoLevel
      .isRetrieveSequenceInformation();
    retrieveTableColumns = schemaInfoLevel.isRetrieveTableColumns();
    retrieveAdditionalTableAttributes = schemaInfoLevel
      .isRetrieveAdditionalTableAttributes();
    retrieveAdditionalColumnAttributes = schemaInfoLevel
      .isRetrieveAdditionalColumnAttributes();
    retrieveTableDefinitionsInformation = schemaInfoLevel
      .isRetrieveTableDefinitionsInformation();

    return this;
  }

  public String getTag()
  {
    return tag;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalColumnAttributes(final boolean retrieveAdditionalColumnAttributes)
  {
    this.retrieveAdditionalColumnAttributes = retrieveAdditionalColumnAttributes;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalDatabaseInfo(final boolean retrieveAdditionalDatabaseInfo)
  {
    this.retrieveAdditionalDatabaseInfo = retrieveAdditionalDatabaseInfo;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalJdbcDriverInfo(final boolean retrieveAdditionalJdbcDriverInfo)
  {
    this.retrieveAdditionalJdbcDriverInfo = retrieveAdditionalJdbcDriverInfo;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalTableAttributes(final boolean retrieveAdditionalTableAttributes)
  {
    this.retrieveAdditionalTableAttributes = retrieveAdditionalTableAttributes;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveColumnDataTypes(final boolean retrieveColumnDataTypes)
  {
    this.retrieveColumnDataTypes = retrieveColumnDataTypes;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveDatabaseInfo(final boolean retrieveDatabaseInfo)
  {
    this.retrieveDatabaseInfo = retrieveDatabaseInfo;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveForeignKeyDefinitions(final boolean retrieveForeignKeyDefinitions)
  {
    this.retrieveForeignKeyDefinitions = retrieveForeignKeyDefinitions;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveForeignKeys(final boolean retrieveForeignKeys)
  {
    this.retrieveForeignKeys = retrieveForeignKeys;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveIndexColumnInformation(final boolean retrieveIndexColumnInformation)
  {
    this.retrieveIndexColumnInformation = retrieveIndexColumnInformation;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveIndexes(final boolean retrieveIndexes)
  {
    this.retrieveIndexes = retrieveIndexes;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveIndexInformation(final boolean retrieveIndexInformation)
  {
    this.retrieveIndexInformation = retrieveIndexInformation;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrievePrimaryKeyDefinitions(final boolean retrievePrimaryKeyDefinitions)
  {
    this.retrievePrimaryKeyDefinitions = retrievePrimaryKeyDefinitions;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveRoutineColumns(final boolean retrieveRoutineColumns)
  {
    this.retrieveRoutineColumns = retrieveRoutineColumns;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveRoutineInformation(final boolean retrieveRoutineInformation)
  {
    this.retrieveRoutineInformation = retrieveRoutineInformation;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveRoutines(final boolean retrieveRoutines)
  {
    this.retrieveRoutines = retrieveRoutines;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveSequenceInformation(final boolean retrieveSequenceInformation)
  {
    this.retrieveSequenceInformation = retrieveSequenceInformation;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveServerInfo(final boolean retrieveServerInfo)
  {
    this.retrieveServerInfo = retrieveServerInfo;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveSynonymInformation(final boolean retrieveSynonymInformation)
  {
    this.retrieveSynonymInformation = retrieveSynonymInformation;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableColumnPrivileges(final boolean retrieveTableColumnPrivileges)
  {
    this.retrieveTableColumnPrivileges = retrieveTableColumnPrivileges;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableColumns(final boolean retrieveTableColumns)
  {
    this.retrieveTableColumns = retrieveTableColumns;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableConstraintDefinitions(final boolean retrieveTableConstraintDefinitions)
  {
    this.retrieveTableConstraintDefinitions = retrieveTableConstraintDefinitions;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableConstraintInformation(final boolean retrieveTableConstraintInformation)
  {
    this.retrieveTableConstraintInformation = retrieveTableConstraintInformation;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableDefinitionsInformation(final boolean retrieveTableDefinitionsInformation)
  {
    this.retrieveTableDefinitionsInformation = retrieveTableDefinitionsInformation;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTablePrivileges(final boolean retrieveTablePrivileges)
  {
    this.retrieveTablePrivileges = retrieveTablePrivileges;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTables(final boolean retrieveTables)
  {
    this.retrieveTables = retrieveTables;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTriggerInformation(final boolean retrieveTriggerInformation)
  {
    this.retrieveTriggerInformation = retrieveTriggerInformation;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveUserDefinedColumnDataTypes(final boolean retrieveUserDefinedColumnDataTypes)
  {
    this.retrieveUserDefinedColumnDataTypes = retrieveUserDefinedColumnDataTypes;
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveViewInformation(final boolean retrieveViewInformation)
  {
    this.retrieveViewInformation = retrieveViewInformation;
    return this;
  }

  public SchemaInfoLevelBuilder setTag(final String tag)
  {
    this.tag = tag;
    return this;
  }

  @Override
  public Config toConfig()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SchemaInfoLevel toOptions()
  {
    return new SchemaInfoLevel(tag,
                               retrieveTables,
                               retrieveRoutines,
                               retrieveColumnDataTypes,
                               retrieveDatabaseInfo,
                               retrieveAdditionalDatabaseInfo,
                               retrieveServerInfo,
                               retrieveAdditionalJdbcDriverInfo,
                               retrieveUserDefinedColumnDataTypes,
                               retrieveRoutineColumns,
                               retrieveRoutineInformation,
                               retrieveTableConstraintInformation,
                               retrieveTableConstraintDefinitions,
                               retrieveViewInformation,
                               retrieveIndexInformation,
                               retrieveIndexColumnInformation,
                               retrievePrimaryKeyDefinitions,
                               retrieveForeignKeys,
                               retrieveForeignKeyDefinitions,
                               retrieveIndexes,
                               retrieveTablePrivileges,
                               retrieveTableColumnPrivileges,
                               retrieveTriggerInformation,
                               retrieveSynonymInformation,
                               retrieveSequenceInformation,
                               retrieveTableColumns,
                               retrieveAdditionalTableAttributes,
                               retrieveAdditionalColumnAttributes,
                               retrieveTableDefinitionsInformation);
  }

  @Override
  public String toString()
  {
    return tag == null? "": tag;
  }

}
