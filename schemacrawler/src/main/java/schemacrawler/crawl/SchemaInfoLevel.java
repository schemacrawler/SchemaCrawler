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

  private static final long serialVersionUID = -6721986729175552425L;

  static
  {
    minimum = new SchemaInfoLevel();
    basic = new SchemaInfoLevel();
    verbose = new SchemaInfoLevel();
    maximum = new SchemaInfoLevel();
    try
    {
      minimum.makeImmutable();

      basic.setRetrieveColumnDataTypes(true);
      basic.setRetrieveProcedureColumns(true);
      basic.setRetrieveTableColumns(true);
      basic.makeImmutable();

      verbose.setRetrieveColumnDataTypes(true);
      verbose.setRetrieveProcedureColumns(true);
      verbose.setRetrieveTableColumns(true);
      verbose.setRetrieveAdditionalDatabaseInfo(true);
      verbose.setRetrieveUserDefinedColumnDataTypes(true);
      verbose.setRetrieveProcedureInformation(true);
      verbose.setRetrieveCheckConstraintInformation(true);
      verbose.setRetrieveViewInformation(true);
      verbose.setRetrieveForeignKeys(true);
      verbose.setRetrieveIndices(true);
      verbose.makeImmutable();

      maximum.setRetrieveColumnDataTypes(true);
      maximum.setRetrieveProcedureColumns(true);
      maximum.setRetrieveTableColumns(true);
      maximum.setRetrieveAdditionalDatabaseInfo(true);
      maximum.setRetrieveUserDefinedColumnDataTypes(true);
      maximum.setRetrieveProcedureInformation(true);
      maximum.setRetrieveCheckConstraintInformation(true);
      maximum.setRetrieveViewInformation(true);
      maximum.setRetrieveForeignKeys(true);
      maximum.setRetrieveIndices(true);
      maximum.setRetrieveTablePrivileges(true);
      maximum.setRetrieveTableColumnPrivileges(true);
      maximum.setRetrieveTriggerInformation(true);
      maximum.makeImmutable();
    }
    catch (final SchemaCrawlerException e)
    {
      // Ignore
    }
  }

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

  public boolean isImmutable()
  {
    return isImmutable;
  }

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
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveAdditionalDatabaseInfo = retrieveAdditionalDatabaseInfo;
  }

  public void setRetrieveCheckConstraintInformation(final boolean retrieveCheckConstraintInformation)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveCheckConstraintInformation = retrieveCheckConstraintInformation;
  }

  public void setRetrieveColumnDataTypes(final boolean retrieveColumnDataTypes)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveColumnDataTypes = retrieveColumnDataTypes;
  }

  public void setRetrieveForeignKeys(final boolean retrieveForeignKeys)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveForeignKeys = retrieveForeignKeys;
  }

  public void setRetrieveIndices(final boolean retrieveIndices)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveIndices = retrieveIndices;
  }

  public void setRetrieveProcedureColumns(final boolean retrieveProcedureColumns)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveProcedureColumns = retrieveProcedureColumns;
  }

  public void setRetrieveProcedureInformation(final boolean retrieveProcedureInformation)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveProcedureInformation = retrieveProcedureInformation;
  }

  public void setRetrieveTableColumnPrivileges(final boolean retrieveTableColumnPrivileges)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveTableColumnPrivileges = retrieveTableColumnPrivileges;
  }

  public void setRetrieveTableColumns(final boolean retrieveTableColumns)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveTableColumns = retrieveTableColumns;
  }

  public void setRetrieveTablePrivileges(final boolean retrieveTablePrivileges)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveTablePrivileges = retrieveTablePrivileges;
  }

  public void setRetrieveTriggerInformation(final boolean retrieveTriggerInformation)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveTriggerInformation = retrieveTriggerInformation;
  }

  public void setRetrieveUserDefinedColumnDataTypes(final boolean retrieveUserDefinedColumnDataTypes)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveUserDefinedColumnDataTypes = retrieveUserDefinedColumnDataTypes;
  }

  public void setRetrieveViewInformation(final boolean retrieveViewInformation)
    throws SchemaCrawlerException
  {
    checkMutability();
    this.retrieveViewInformation = retrieveViewInformation;
  }

  private void checkMutability()
    throws SchemaCrawlerException
  {
    if (isImmutable)
    {
      throw new SchemaCrawlerException("Cannot modify the SchemaInfoLevel");
    }
  }

}
