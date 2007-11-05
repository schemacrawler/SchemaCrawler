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


import java.io.Serializable;

/**
 * Enumeration for level of schema detail.
 */
public final class SchemaInfoLevel
  implements Serializable
{

  /** No schema detail. */
  public final static SchemaInfoLevel minimum;

  /** Basic schema detail. */
  public final static SchemaInfoLevel basic;
  /** Verbose schema detail. */
  public final static SchemaInfoLevel verbose;
  /** Maximum schema detail. */
  public final static SchemaInfoLevel maximum;
  /**
   * <pre>
   *   DatabaseInfo
   *   &gt; basic
   *       retrieveColumnDataTypes
   *   &gt; verbose
   *       retrieveAdditionalDatabaseInfo
   *       retrieveUserDefinedColumnDataTypes
   *       
   *   Procedures    
   *   &gt; minimum
   *       retrieveProcedureColumns  
   *   &gt;= verbose
   *       retrieveProcedureInformation     
   *       
   *   Tables
   *   &gt;= verbose
   *       retrieveCheckConstraintInformation
   *       retrieveViewInformation
   *       retrieveForeignKeys
   *       retrieveIndices
   *   = maximum      
   *       retrievePrivileges (table and columns)
   *       retrieveTriggerInformation      
   *   &gt; minimum      
   *       retrieveColumns
   * </pre>
   */

  private static final long serialVersionUID = -6721986729175552425L;

  private boolean isImmutable;

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

  public boolean isRetrieveAdditionalDatabaseInfo()
  {
    return retrieveAdditionalDatabaseInfo;
  }

  public boolean isRetrieveCheckConstraintInformation()
  {
    return retrieveCheckConstraintInformation;
  }

  public boolean isRetrieveColumnDataTypes()
  {
    return retrieveColumnDataTypes;
  }

  public boolean isRetrieveForeignKeys()
  {
    return retrieveForeignKeys;
  }

  public boolean isRetrieveIndices()
  {
    return retrieveIndices;
  }

  public boolean isRetrieveProcedureColumns()
  {
    return retrieveProcedureColumns;
  }

  public boolean isRetrieveProcedureInformation()
  {
    return retrieveProcedureInformation;
  }

  public boolean isRetrieveTableColumnPrivileges()
  {
    return retrieveTableColumnPrivileges;
  }

  public boolean isRetrieveTableColumns()
  {
    return retrieveTableColumns;
  }

  public boolean isRetrieveTablePrivileges()
  {
    return retrieveTablePrivileges;
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

  public void makeImmutable()
  {
    isImmutable = true;
  }

  public void setRetrieveAdditionalDatabaseInfo(final boolean retrieveAdditionalDatabaseInfo)
  {
    this.retrieveAdditionalDatabaseInfo = retrieveAdditionalDatabaseInfo;
  }

  public void setRetrieveCheckConstraintInformation(final boolean retrieveCheckConstraintInformation)
  {
    this.retrieveCheckConstraintInformation = retrieveCheckConstraintInformation;
  }

  public void setRetrieveColumnDataTypes(final boolean retrieveColumnDataTypes)
  {
    this.retrieveColumnDataTypes = retrieveColumnDataTypes;
  }

  public void setRetrieveForeignKeys(final boolean retrieveForeignKeys)
  {
    this.retrieveForeignKeys = retrieveForeignKeys;
  }

  public void setRetrieveIndices(final boolean retrieveIndices)
  {
    this.retrieveIndices = retrieveIndices;
  }

  public void setRetrieveProcedureColumns(final boolean retrieveProcedureColumns)
  {
    this.retrieveProcedureColumns = retrieveProcedureColumns;
  }

  public void setRetrieveProcedureInformation(final boolean retrieveProcedureInformation)
  {
    this.retrieveProcedureInformation = retrieveProcedureInformation;
  }

  public void setRetrieveTableColumnPrivileges(final boolean retrieveTableColumnPrivileges)
  {
    this.retrieveTableColumnPrivileges = retrieveTableColumnPrivileges;
  }

  public void setRetrieveTableColumns(final boolean retrieveTableColumns)
  {
    this.retrieveTableColumns = retrieveTableColumns;
  }

  public void setRetrieveTablePrivileges(final boolean retrieveTablePrivileges)
  {
    this.retrieveTablePrivileges = retrieveTablePrivileges;
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

}
