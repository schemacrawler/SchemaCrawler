/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
    final SchemaInfoLevel detailed = standard();
    detailed.setRetrieveUserDefinedColumnDataTypes(true);
    detailed.setRetrieveRoutineInformation(true);
    detailed.setRetrieveCheckConstraintInformation(true);
    detailed.setRetrieveTriggerInformation(true);
    detailed.setRetrieveViewInformation(true);
    detailed.setTag("detailed");
    return detailed;
  }

  /**
   * Creates a new SchemaInfoLevel for maximum schema information.
   * 
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel maximum()
  {
    final SchemaInfoLevel maximum = detailed();
    maximum.setRetrieveSynonymInformation(true);
    maximum.setRetrieveAdditionalDatabaseInfo(true);
    maximum.setRetrieveAdditionalJdbcDriverInfo(true);
    maximum.setRetrieveTablePrivileges(true);
    maximum.setRetrieveTableColumnPrivileges(true);
    maximum.setRetrieveAdditionalTableAttributes(true);
    maximum.setRetrieveAdditionalColumnAttributes(true);
    maximum.setTag("maximum");
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
    minimum.setRetrieveRoutines(true);
    minimum.setTag("minimum");
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
    standard.setRetrieveTableColumns(true);
    standard.setRetrieveForeignKeys(true);
    standard.setRetrieveIndices(true);
    standard.setRetrieveRoutineColumns(true);
    standard.setTag("standard");
    return standard;
  }

  private String tag;
  private boolean retrieveSchemaCrawlerInfo = true;
  private boolean retrieveJdbcDriverInfo = true;
  private boolean retrieveDatabaseInfo = true;
  private boolean retrieveTables;
  private boolean retrieveRoutines;
  private boolean retrieveColumnDataTypes;
  private boolean retrieveAdditionalSchemaCrawlerInfo;
  private boolean retrieveAdditionalDatabaseInfo;
  private boolean retrieveAdditionalJdbcDriverInfo;
  private boolean retrieveUserDefinedColumnDataTypes;
  private boolean retrieveRoutineColumns;
  private boolean retrieveRoutineInformation;
  private boolean retrieveCheckConstraintInformation;
  private boolean retrieveViewInformation;
  private boolean retrieveForeignKeys;
  private boolean retrieveIndices;
  private boolean retrieveTablePrivileges;
  private boolean retrieveTableColumnPrivileges;
  private boolean retrieveTriggerInformation;
  private boolean retrieveSynonymInformation;
  private boolean retrieveTableColumns;
  private boolean retrieveAdditionalTableAttributes;
  private boolean retrieveAdditionalColumnAttributes;

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

  public boolean isRetrieveAdditionalSchemaCrawlerInfo()
  {
    return retrieveAdditionalSchemaCrawlerInfo;
  }

  public boolean isRetrieveAdditionalTableAttributes()
  {
    return retrieveAdditionalTableAttributes;
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

  public boolean isRetrieveSchemaCrawlerInfo()
  {
    return retrieveSchemaCrawlerInfo;
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

  public void setRetrieveAdditionalSchemaCrawlerInfo(final boolean retrieveAdditionalSchemaCrawlerInfo)
  {
    this.retrieveAdditionalSchemaCrawlerInfo = retrieveAdditionalSchemaCrawlerInfo;
  }

  public void setRetrieveAdditionalTableAttributes(final boolean retrieveAdditionalTableAttributes)
  {
    this.retrieveAdditionalTableAttributes = retrieveAdditionalTableAttributes;
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

  public void setRetrieveSchemaCrawlerInfo(final boolean retrieveSchemaCrawlerInfo)
  {
    this.retrieveSchemaCrawlerInfo = retrieveSchemaCrawlerInfo;
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
