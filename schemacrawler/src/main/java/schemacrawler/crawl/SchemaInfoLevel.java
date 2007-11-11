/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.crawl;


/**
 * Enumeration for level of schema detail.
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
   * @return the retrieveDatabaseInfo
   */
  public boolean isRetrieveDatabaseInfo()
  {
    return retrieveDatabaseInfo;
  }

  /**
   * @param retrieveDatabaseInfo
   *        the retrieveDatabaseInfo to set
   */
  public void setRetrieveDatabaseInfo(boolean retrieveDatabaseInfo)
  {
    this.retrieveDatabaseInfo = retrieveDatabaseInfo;
  }

  /**
   * @return the retrieveTables
   */
  public boolean isRetrieveTables()
  {
    return retrieveTables;
  }

  /**
   * @param retrieveTables
   *        the retrieveTables to set
   */
  public void setRetrieveTables(boolean retrieveTables)
  {
    this.retrieveTables = retrieveTables;
  }

  /**
   * @return the retrieveProcedures
   */
  public boolean isRetrieveProcedures()
  {
    return retrieveProcedures;
  }

  /**
   * @param retrieveProcedures
   *        the retrieveProcedures to set
   */
  public void setRetrieveProcedures(boolean retrieveProcedures)
  {
    this.retrieveProcedures = retrieveProcedures;
  }

  /**
   * @return the retrieveColumnDataTypes
   */
  public boolean isRetrieveColumnDataTypes()
  {
    return retrieveColumnDataTypes;
  }

  /**
   * @param retrieveColumnDataTypes
   *        the retrieveColumnDataTypes to set
   */
  public void setRetrieveColumnDataTypes(boolean retrieveColumnDataTypes)
  {
    this.retrieveColumnDataTypes = retrieveColumnDataTypes;
  }

  /**
   * @return the retrieveAdditionalDatabaseInfo
   */
  public boolean isRetrieveAdditionalDatabaseInfo()
  {
    return retrieveAdditionalDatabaseInfo;
  }

  /**
   * @param retrieveAdditionalDatabaseInfo
   *        the retrieveAdditionalDatabaseInfo to set
   */
  public void setRetrieveAdditionalDatabaseInfo(boolean retrieveAdditionalDatabaseInfo)
  {
    this.retrieveAdditionalDatabaseInfo = retrieveAdditionalDatabaseInfo;
  }

  /**
   * @return the retrieveUserDefinedColumnDataTypes
   */
  public boolean isRetrieveUserDefinedColumnDataTypes()
  {
    return retrieveUserDefinedColumnDataTypes;
  }

  /**
   * @param retrieveUserDefinedColumnDataTypes
   *        the retrieveUserDefinedColumnDataTypes to set
   */
  public void setRetrieveUserDefinedColumnDataTypes(boolean retrieveUserDefinedColumnDataTypes)
  {
    this.retrieveUserDefinedColumnDataTypes = retrieveUserDefinedColumnDataTypes;
  }

  /**
   * @return the retrieveProcedureColumns
   */
  public boolean isRetrieveProcedureColumns()
  {
    return retrieveProcedureColumns;
  }

  /**
   * @param retrieveProcedureColumns
   *        the retrieveProcedureColumns to set
   */
  public void setRetrieveProcedureColumns(boolean retrieveProcedureColumns)
  {
    this.retrieveProcedureColumns = retrieveProcedureColumns;
  }

  /**
   * @return the retrieveProcedureInformation
   */
  public boolean isRetrieveProcedureInformation()
  {
    return retrieveProcedureInformation;
  }

  /**
   * @param retrieveProcedureInformation
   *        the retrieveProcedureInformation to set
   */
  public void setRetrieveProcedureInformation(boolean retrieveProcedureInformation)
  {
    this.retrieveProcedureInformation = retrieveProcedureInformation;
  }

  /**
   * @return the retrieveCheckConstraintInformation
   */
  public boolean isRetrieveCheckConstraintInformation()
  {
    return retrieveCheckConstraintInformation;
  }

  /**
   * @param retrieveCheckConstraintInformation
   *        the retrieveCheckConstraintInformation to set
   */
  public void setRetrieveCheckConstraintInformation(boolean retrieveCheckConstraintInformation)
  {
    this.retrieveCheckConstraintInformation = retrieveCheckConstraintInformation;
  }

  /**
   * @return the retrieveViewInformation
   */
  public boolean isRetrieveViewInformation()
  {
    return retrieveViewInformation;
  }

  /**
   * @param retrieveViewInformation
   *        the retrieveViewInformation to set
   */
  public void setRetrieveViewInformation(boolean retrieveViewInformation)
  {
    this.retrieveViewInformation = retrieveViewInformation;
  }

  /**
   * @return the retrieveForeignKeys
   */
  public boolean isRetrieveForeignKeys()
  {
    return retrieveForeignKeys;
  }

  /**
   * @param retrieveForeignKeys
   *        the retrieveForeignKeys to set
   */
  public void setRetrieveForeignKeys(boolean retrieveForeignKeys)
  {
    this.retrieveForeignKeys = retrieveForeignKeys;
  }

  /**
   * @return the retrieveIndices
   */
  public boolean isRetrieveIndices()
  {
    return retrieveIndices;
  }

  /**
   * @param retrieveIndices
   *        the retrieveIndices to set
   */
  public void setRetrieveIndices(boolean retrieveIndices)
  {
    this.retrieveIndices = retrieveIndices;
  }

  /**
   * @return the retrieveTablePrivileges
   */
  public boolean isRetrieveTablePrivileges()
  {
    return retrieveTablePrivileges;
  }

  /**
   * @param retrieveTablePrivileges
   *        the retrieveTablePrivileges to set
   */
  public void setRetrieveTablePrivileges(boolean retrieveTablePrivileges)
  {
    this.retrieveTablePrivileges = retrieveTablePrivileges;
  }

  /**
   * @return the retrieveTableColumnPrivileges
   */
  public boolean isRetrieveTableColumnPrivileges()
  {
    return retrieveTableColumnPrivileges;
  }

  /**
   * @param retrieveTableColumnPrivileges
   *        the retrieveTableColumnPrivileges to set
   */
  public void setRetrieveTableColumnPrivileges(boolean retrieveTableColumnPrivileges)
  {
    this.retrieveTableColumnPrivileges = retrieveTableColumnPrivileges;
  }

  /**
   * @return the retrieveTriggerInformation
   */
  public boolean isRetrieveTriggerInformation()
  {
    return retrieveTriggerInformation;
  }

  /**
   * @param retrieveTriggerInformation
   *        the retrieveTriggerInformation to set
   */
  public void setRetrieveTriggerInformation(boolean retrieveTriggerInformation)
  {
    this.retrieveTriggerInformation = retrieveTriggerInformation;
  }

  /**
   * @return the retrieveTableColumns
   */
  public boolean isRetrieveTableColumns()
  {
    return retrieveTableColumns;
  }

  /**
   * @param retrieveTableColumns
   *        the retrieveTableColumns to set
   */
  public void setRetrieveTableColumns(boolean retrieveTableColumns)
  {
    this.retrieveTableColumns = retrieveTableColumns;
  }

}
