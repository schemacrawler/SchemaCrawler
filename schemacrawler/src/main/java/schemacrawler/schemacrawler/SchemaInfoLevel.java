/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

  /**
   * Creates a new SchemaInfoLevel for verbose schema information.
   * 
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel detailed()
  {
    final SchemaInfoLevel verbose = standard();
    verbose.setRetrieveUserDefinedColumnDataTypes(true);
    verbose.setRetrieveProcedureInformation(true);
    verbose.setRetrieveCheckConstraintInformation(true);
    verbose.setRetrieveViewInformation(true);
    return verbose;
  }

  /**
   * Creates a new SchemaInfoLevel for maximum schema information.
   * 
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel maximum()
  {
    final SchemaInfoLevel maximum = detailed();
    maximum.setRetrieveAdditionalDatabaseInfo(true);
    maximum.setRetrieveAdditionalJdbcDriverInfo(true);
    maximum.setRetrieveTablePrivileges(true);
    maximum.setRetrieveTableColumnPrivileges(true);
    maximum.setRetrieveTriggerInformation(true);
    maximum.setRetrieveWeakAssociations(true);
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
    minimum.setRetrieveSchemaCrawlerInfo(true);
    minimum.setRetrieveDatabaseInfo(true);
    minimum.setRetrieveJdbcDriverInfo(true);
    minimum.setRetrieveTables(true);
    minimum.setRetrieveProcedures(true);
    return minimum;
  }

  /**
   * Creates a new SchemaInfoLevel for standard schema information.
   * 
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel standard()
  {
    final SchemaInfoLevel standard = minimum();
    standard.setRetrieveColumnDataTypes(true);
    standard.setRetrieveProcedureColumns(true);
    standard.setRetrieveTableColumns(true);
    standard.setRetrieveForeignKeys(true);
    standard.setRetrieveIndices(true);
    return standard;
  }

  private boolean retrieveSchemaCrawlerInfo = true;
  private boolean retrieveJdbcDriverInfo = true;
  private boolean retrieveDatabaseInfo = true;
  private boolean retrieveTables;
  private boolean retrieveProcedures;
  private boolean retrieveColumnDataTypes;
  private boolean retrieveAdditionalSchemaCrawlerInfo;
  private boolean retrieveAdditionalDatabaseInfo;
  private boolean retrieveAdditionalJdbcDriverInfo;
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
  private boolean retrieveWeakAssociations;

  public boolean isRetrieveAdditionalDatabaseInfo()
  {
    return retrieveAdditionalDatabaseInfo;
  }

  public boolean isRetrieveAdditionalJdbcDriverInfo()
  {
    return retrieveAdditionalJdbcDriverInfo;
  }

  public boolean isRetrieveAdditionalSchemaCrawlerInfo()
  {
    return retrieveAdditionalSchemaCrawlerInfo;
  }

  public boolean isRetrieveCheckConstraintInformation()
  {
    return retrieveCheckConstraintInformation;
  }

  public boolean isRetrieveColumnDataTypes()
  {
    return retrieveColumnDataTypes;
  }

  public boolean isRetrieveDatabaseInfo()
  {
    return retrieveDatabaseInfo;
  }

  public boolean isRetrieveForeignKeys()
  {
    return retrieveForeignKeys;
  }

  public boolean isRetrieveIndices()
  {
    return retrieveIndices;
  }

  public boolean isRetrieveJdbcDriverInfo()
  {
    return retrieveJdbcDriverInfo;
  }

  public boolean isRetrieveProcedureColumns()
  {
    return retrieveProcedureColumns;
  }

  public boolean isRetrieveProcedureInformation()
  {
    return retrieveProcedureInformation;
  }

  public boolean isRetrieveProcedures()
  {
    return retrieveProcedures;
  }

  public boolean isRetrieveSchemaCrawlerInfo()
  {
    return retrieveSchemaCrawlerInfo;
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

  public boolean isRetrieveWeakAssociations()
  {
    return retrieveWeakAssociations;
  }

  public void setRetrieveAdditionalDatabaseInfo(final boolean retrieveAdditionalDatabaseInfo)
  {
    this.retrieveAdditionalDatabaseInfo = retrieveAdditionalDatabaseInfo;
  }

  public void setRetrieveAdditionalJdbcDriverInfo(final boolean retrieveAdditionalJdbcDriverInfo)
  {
    this.retrieveAdditionalJdbcDriverInfo = retrieveAdditionalJdbcDriverInfo;
  }

  public void setRetrieveAdditionalSchemaCrawlerInfo(final boolean retrieveAdditionalSchemaCrawlerInfo)
  {
    this.retrieveAdditionalSchemaCrawlerInfo = retrieveAdditionalSchemaCrawlerInfo;
  }

  public void setRetrieveCheckConstraintInformation(final boolean retrieveCheckConstraintInformation)
  {
    this.retrieveCheckConstraintInformation = retrieveCheckConstraintInformation;
  }

  public void setRetrieveColumnDataTypes(final boolean retrieveColumnDataTypes)
  {
    this.retrieveColumnDataTypes = retrieveColumnDataTypes;
  }

  public void setRetrieveDatabaseInfo(final boolean retrieveDatabaseInfo)
  {
    this.retrieveDatabaseInfo = retrieveDatabaseInfo;
  }

  public void setRetrieveForeignKeys(final boolean retrieveForeignKeys)
  {
    this.retrieveForeignKeys = retrieveForeignKeys;
  }

  public void setRetrieveIndices(final boolean retrieveIndices)
  {
    this.retrieveIndices = retrieveIndices;
  }

  public void setRetrieveJdbcDriverInfo(final boolean retrieveJdbcDriverInfo)
  {
    this.retrieveJdbcDriverInfo = retrieveJdbcDriverInfo;
  }

  public void setRetrieveProcedureColumns(final boolean retrieveProcedureColumns)
  {
    this.retrieveProcedureColumns = retrieveProcedureColumns;
  }

  public void setRetrieveProcedureInformation(final boolean retrieveProcedureInformation)
  {
    this.retrieveProcedureInformation = retrieveProcedureInformation;
  }

  public void setRetrieveProcedures(final boolean retrieveProcedures)
  {
    this.retrieveProcedures = retrieveProcedures;
  }

  public void setRetrieveSchemaCrawlerInfo(final boolean retrieveSchemaCrawlerInfo)
  {
    this.retrieveSchemaCrawlerInfo = retrieveSchemaCrawlerInfo;
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

  public void setRetrieveWeakAssociations(final boolean retrieveWeakAssociations)
  {
    this.retrieveWeakAssociations = retrieveWeakAssociations;
  }

}
