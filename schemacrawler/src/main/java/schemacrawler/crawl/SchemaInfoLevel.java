/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


/**
 * Descriptor for level of schema detail.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaInfoLevel
  implements Options
{

  private static final long serialVersionUID = -6721986729175552425L;

  /**
   * Creates a new SchemaInfoLevel for basic schema information.
   * 
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel basic()
  {
    final SchemaInfoLevel basic = minimum();
    basic.setRetrieveColumnDataTypes(true);
    basic.setRetrieveProcedureColumns(true);
    basic.setRetrieveTableColumns(true);
    return basic;
  }

  /**
   * Creates a new SchemaInfoLevel for maximum schema information.
   * 
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel maximum()
  {
    final SchemaInfoLevel maximum = verbose();
    maximum.setRetrieveJdbcDriverInfo(true);
    maximum.setRetrieveTablePrivileges(true);
    maximum.setRetrieveTableColumnPrivileges(true);
    maximum.setRetrieveTriggerInformation(true);
    return maximum;
  }

  /**
   * Creates a new SchemaInfoLevel for minimum schema information.
   * 
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel minimum()
  {
    final SchemaInfoLevel minimum = new SchemaInfoLevel();
    minimum.setRetrieveDatabaseInfo(true);
    minimum.setRetrieveTables(true);
    minimum.setRetrieveProcedures(true);
    return minimum;
  }

  /**
   * Creates a new SchemaInfoLevel for verbose schema information.
   * 
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel verbose()
  {
    final SchemaInfoLevel verbose = basic();
    verbose.setRetrieveAdditionalDatabaseInfo(true);
    verbose.setRetrieveUserDefinedColumnDataTypes(true);
    verbose.setRetrieveProcedureInformation(true);
    verbose.setRetrieveCheckConstraintInformation(true);
    verbose.setRetrieveViewInformation(true);
    verbose.setRetrieveForeignKeys(true);
    verbose.setRetrieveIndices(true);
    return verbose;
  }

  private boolean retrieveJdbcDriverInfo;
  private boolean retrieveDatabaseInfo;
  private boolean retrieveTables;
  private boolean retrieveProcedures;
  private boolean retrieveColumnDataTypes;
  private boolean retrieveAdditionalDatabaseInfo;
  private boolean retrieveUserDefinedColumnDataTypes;
  private boolean retrieveProcedureColumns;
  private boolean retrieveProcedureInformation;
  private boolean retrieveCheckConstraintInformation;
  private boolean retrieveViewInformation;
  private boolean retrieveForeignKeys;
  private boolean retrieveIndices;
  private boolean retrieveTablePrivileges;
  private boolean retrieveTableColumnPrivileges;
  private boolean retrieveTriggerInformation;
  private boolean retrieveTableColumns;

  /**
   * @return the retrieveAdditionalDatabaseInfo
   */
  public boolean isRetrieveAdditionalDatabaseInfo()
  {
    return retrieveAdditionalDatabaseInfo;
  }

  /**
   * @return the retrieveCheckConstraintInformation
   */
  public boolean isRetrieveCheckConstraintInformation()
  {
    return retrieveCheckConstraintInformation;
  }

  /**
   * @return the retrieveColumnDataTypes
   */
  public boolean isRetrieveColumnDataTypes()
  {
    return retrieveColumnDataTypes;
  }

  /**
   * @return the retrieveDatabaseInfo
   */
  public boolean isRetrieveDatabaseInfo()
  {
    return retrieveDatabaseInfo;
  }

  /**
   * @return the retrieveForeignKeys
   */
  public boolean isRetrieveForeignKeys()
  {
    return retrieveForeignKeys;
  }

  /**
   * @return the retrieveIndices
   */
  public boolean isRetrieveIndices()
  {
    return retrieveIndices;
  }

  /**
   * @return the retrieveJdbcDriverInfo
   */
  public boolean isRetrieveJdbcDriverInfo()
  {
    return retrieveJdbcDriverInfo;
  }

  /**
   * @return the retrieveProcedureColumns
   */
  public boolean isRetrieveProcedureColumns()
  {
    return retrieveProcedureColumns;
  }

  /**
   * @return the retrieveProcedureInformation
   */
  public boolean isRetrieveProcedureInformation()
  {
    return retrieveProcedureInformation;
  }

  /**
   * @return the retrieveProcedures
   */
  public boolean isRetrieveProcedures()
  {
    return retrieveProcedures;
  }

  /**
   * @return the retrieveTableColumnPrivileges
   */
  public boolean isRetrieveTableColumnPrivileges()
  {
    return retrieveTableColumnPrivileges;
  }

  /**
   * @return the retrieveTableColumns
   */
  public boolean isRetrieveTableColumns()
  {
    return retrieveTableColumns;
  }

  /**
   * @return the retrieveTablePrivileges
   */
  public boolean isRetrieveTablePrivileges()
  {
    return retrieveTablePrivileges;
  }

  /**
   * @return the retrieveTables
   */
  public boolean isRetrieveTables()
  {
    return retrieveTables;
  }

  /**
   * @return the retrieveTriggerInformation
   */
  public boolean isRetrieveTriggerInformation()
  {
    return retrieveTriggerInformation;
  }

  /**
   * @return the retrieveUserDefinedColumnDataTypes
   */
  public boolean isRetrieveUserDefinedColumnDataTypes()
  {
    return retrieveUserDefinedColumnDataTypes;
  }

  /**
   * @return the retrieveViewInformation
   */
  public boolean isRetrieveViewInformation()
  {
    return retrieveViewInformation;
  }

  /**
   * @param retrieveAdditionalDatabaseInfo
   *        the retrieveAdditionalDatabaseInfo to set
   */
  public void setRetrieveAdditionalDatabaseInfo(final boolean retrieveAdditionalDatabaseInfo)
  {
    this.retrieveAdditionalDatabaseInfo = retrieveAdditionalDatabaseInfo;
  }

  /**
   * @param retrieveCheckConstraintInformation
   *        the retrieveCheckConstraintInformation to set
   */
  public void setRetrieveCheckConstraintInformation(final boolean retrieveCheckConstraintInformation)
  {
    this.retrieveCheckConstraintInformation = retrieveCheckConstraintInformation;
  }

  /**
   * @param retrieveColumnDataTypes
   *        the retrieveColumnDataTypes to set
   */
  public void setRetrieveColumnDataTypes(final boolean retrieveColumnDataTypes)
  {
    this.retrieveColumnDataTypes = retrieveColumnDataTypes;
  }

  /**
   * @param retrieveDatabaseInfo
   *        the retrieveDatabaseInfo to set
   */
  public void setRetrieveDatabaseInfo(final boolean retrieveDatabaseInfo)
  {
    this.retrieveDatabaseInfo = retrieveDatabaseInfo;
  }

  /**
   * @param retrieveForeignKeys
   *        the retrieveForeignKeys to set
   */
  public void setRetrieveForeignKeys(final boolean retrieveForeignKeys)
  {
    this.retrieveForeignKeys = retrieveForeignKeys;
  }

  /**
   * @param retrieveIndices
   *        the retrieveIndices to set
   */
  public void setRetrieveIndices(final boolean retrieveIndices)
  {
    this.retrieveIndices = retrieveIndices;
  }

  /**
   * @param retrieveJdbcDriverInfo
   *        the retrieveJdbcDriverInfo to set
   */
  public void setRetrieveJdbcDriverInfo(final boolean retrieveJdbcDriverInfo)
  {
    this.retrieveJdbcDriverInfo = retrieveJdbcDriverInfo;
  }

  /**
   * @param retrieveProcedureColumns
   *        the retrieveProcedureColumns to set
   */
  public void setRetrieveProcedureColumns(final boolean retrieveProcedureColumns)
  {
    this.retrieveProcedureColumns = retrieveProcedureColumns;
  }

  /**
   * @param retrieveProcedureInformation
   *        the retrieveProcedureInformation to set
   */
  public void setRetrieveProcedureInformation(final boolean retrieveProcedureInformation)
  {
    this.retrieveProcedureInformation = retrieveProcedureInformation;
  }

  /**
   * @param retrieveProcedures
   *        the retrieveProcedures to set
   */
  public void setRetrieveProcedures(final boolean retrieveProcedures)
  {
    this.retrieveProcedures = retrieveProcedures;
  }

  /**
   * @param retrieveTableColumnPrivileges
   *        the retrieveTableColumnPrivileges to set
   */
  public void setRetrieveTableColumnPrivileges(final boolean retrieveTableColumnPrivileges)
  {
    this.retrieveTableColumnPrivileges = retrieveTableColumnPrivileges;
  }

  /**
   * @param retrieveTableColumns
   *        the retrieveTableColumns to set
   */
  public void setRetrieveTableColumns(final boolean retrieveTableColumns)
  {
    this.retrieveTableColumns = retrieveTableColumns;
  }

  /**
   * @param retrieveTablePrivileges
   *        the retrieveTablePrivileges to set
   */
  public void setRetrieveTablePrivileges(final boolean retrieveTablePrivileges)
  {
    this.retrieveTablePrivileges = retrieveTablePrivileges;
  }

  /**
   * @param retrieveTables
   *        the retrieveTables to set
   */
  public void setRetrieveTables(final boolean retrieveTables)
  {
    this.retrieveTables = retrieveTables;
  }

  /**
   * @param retrieveTriggerInformation
   *        the retrieveTriggerInformation to set
   */
  public void setRetrieveTriggerInformation(final boolean retrieveTriggerInformation)
  {
    this.retrieveTriggerInformation = retrieveTriggerInformation;
  }

  /**
   * @param retrieveUserDefinedColumnDataTypes
   *        the retrieveUserDefinedColumnDataTypes to set
   */
  public void setRetrieveUserDefinedColumnDataTypes(final boolean retrieveUserDefinedColumnDataTypes)
  {
    this.retrieveUserDefinedColumnDataTypes = retrieveUserDefinedColumnDataTypes;
  }

  /**
   * @param retrieveViewInformation
   *        the retrieveViewInformation to set
   */
  public void setRetrieveViewInformation(final boolean retrieveViewInformation)
  {
    this.retrieveViewInformation = retrieveViewInformation;
  }

}
