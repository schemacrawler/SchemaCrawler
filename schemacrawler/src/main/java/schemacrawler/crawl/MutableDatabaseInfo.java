/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.util.AlphabeticalSortComparator;
import sf.util.Utilities;

/**
 * Represents database metadata. Created from metadata returned by a JDBC call,
 * and other sources of information.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class MutableDatabaseInfo
  implements DatabaseInfo
{

  private static final long serialVersionUID = 4051323422934251828L;

  private String productName;
  private String productVersion;
  private String driverName;
  private String jdbcDriverClassName;
  private String driverVersion;
  private String connectionUrl;
  private String schemaPattern;
  private String catalog;
  private SortedMap dbProperties;
  private final NamedObjectList columnDataTypes = new NamedObjectList(
      new AlphabeticalSortComparator());

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getDriverName()
   */
  public String getDriverName()
  {
    return driverName;
  }

  /**
   * Sets name of the driver.
   * 
   * @param driverName
   *          Driver name
   */
  public void setDriverName(final String driverName)
  {
    this.driverName = driverName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getDriverVersion()
   */
  public String getDriverVersion()
  {
    return driverVersion;
  }

  void setDriverVersion(final String driverVersion)
  {
    this.driverVersion = driverVersion;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getJdbcDriverClassName()
   */
  public String getJdbcDriverClassName()
  {
    return jdbcDriverClassName;
  }

  void setJdbcDriverClassName(final String jdbcDriverClassName)
  {
    this.jdbcDriverClassName = jdbcDriverClassName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getProductName()
   */
  public String getProductName()
  {
    return productName;
  }

  void setProductName(final String productName)
  {
    this.productName = productName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getProductVersion()
   */
  public String getProductVersion()
  {
    return productVersion;
  }

  void setProductVersion(final String productVersion)
  {
    this.productVersion = productVersion;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getConnectionUrl()
   */
  public String getConnectionUrl()
  {
    return connectionUrl;
  }

  void setConnectionUrl(final String connectionUrl)
  {
    this.connectionUrl = connectionUrl;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getSchemaPattern()
   */
  public String getSchemaPattern()
  {
    return schemaPattern;
  }

  void setSchemaPattern(final String schemaPattern)
  {
    this.schemaPattern = schemaPattern;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getCatalog()
   */
  public String getCatalog()
  {
    return catalog;
  }

  /**
   * Sets the catalog.
   * 
   * @param catalog
   *          Catalog
   */
  public void setCatalog(final String catalog)
  {
    if (catalog == null)
    {
      this.catalog = "";
    }
    else
    {
      this.catalog = catalog;
    }
  }

  void setProperties(final SortedMap properties)
  {
    this.dbProperties = properties;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getProperties()
   */
  public Map getProperties()
  {
    if (dbProperties == null)
    {
      return Collections.EMPTY_MAP;
    }
    else
    {
      return Collections.unmodifiableSortedMap(dbProperties);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getProperty(java.lang.String)
   */
  public Object getProperty(final String name)
  {
    return dbProperties.get(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {

    final StringBuffer info = new StringBuffer();

    info.append("-- database product: ").append(getProductName()).append(" ")
      .append(getProductVersion()).append(Utilities.NEWLINE)
      .append("-- driver: ").append(getJdbcDriverClassName()).append(" - ")
      .append(getDriverName()).append(" ").append(getDriverVersion())
      .append(Utilities.NEWLINE).append("-- connection: ")
      .append(getConnectionUrl()).append(Utilities.NEWLINE)
      .append("-- schema pattern: ").append(getSchemaPattern());

    return info.toString();

  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getColumnDataTypes()
   */
  public ColumnDataType[] getColumnDataTypes()
  {
    final List allColumnDataTypes = columnDataTypes.getAll();
    return (ColumnDataType[]) allColumnDataTypes
      .toArray(new ColumnDataType[allColumnDataTypes.size()]);
  }

  NamedObjectList getColumnDataTypesList()
  {
    return columnDataTypes;
  }

  ColumnDataType lookupByType(final int type)
  {
    ColumnDataType columnDataType = null;
    List allColumnDataTypes = columnDataTypes.getAll();
    for (Iterator iter = allColumnDataTypes.iterator(); iter.hasNext();)
    {
      ColumnDataType currentColumnDataType = (ColumnDataType) iter.next();
      if (type == currentColumnDataType.getType())
      {
        columnDataType = currentColumnDataType;
        break;
      }
    }
    return columnDataType;
  }

  /**
   * Adds a table.
   * 
   * @param table
   *          Table
   */
  void addColumnDataType(final ColumnDataType columnDataType)
  {
    columnDataTypes.add(columnDataType);
  }

}
