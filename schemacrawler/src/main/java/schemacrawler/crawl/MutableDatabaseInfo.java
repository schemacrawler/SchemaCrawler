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


import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import schemacrawler.crawl.NamedObjectList.NamedObjectSort;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import sf.util.Utilities;

/**
 * Represents database metadata. Created from metadata returned by a
 * JDBC call, and other sources of information.
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
  private final SortedMap<String, Object> dbProperties = new TreeMap<String, Object>();
  private final NamedObjectList<MutableColumnDataType> columnDataTypes = new NamedObjectList<MutableColumnDataType>(NamedObjectSort.alphabetical);

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final MutableDatabaseInfo other = (MutableDatabaseInfo) obj;
    if (catalog == null)
    {
      if (other.catalog != null)
      {
        return false;
      }
    }
    else if (!catalog.equals(other.catalog))
    {
      return false;
    }
    if (columnDataTypes == null)
    {
      if (other.columnDataTypes != null)
      {
        return false;
      }
    }
    else if (!columnDataTypes.equals(other.columnDataTypes))
    {
      return false;
    }
    if (dbProperties == null)
    {
      if (other.dbProperties != null)
      {
        return false;
      }
    }
    else if (!dbProperties.equals(other.dbProperties))
    {
      return false;
    }
    if (productName == null)
    {
      if (other.productName != null)
      {
        return false;
      }
    }
    else if (!productName.equals(other.productName))
    {
      return false;
    }
    if (productVersion == null)
    {
      if (other.productVersion != null)
      {
        return false;
      }
    }
    else if (!productVersion.equals(other.productVersion))
    {
      return false;
    }
    if (schemaPattern == null)
    {
      if (other.schemaPattern != null)
      {
        return false;
      }
    }
    else if (!schemaPattern.equals(other.schemaPattern))
    {
      return false;
    }
    return true;
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
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getColumnDataTypes()
   */
  public ColumnDataType[] getColumnDataTypes()
  {
    return columnDataTypes.getAll().toArray(new ColumnDataType[columnDataTypes
      .size()]);
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
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getDriverVersion()
   */
  public String getDriverVersion()
  {
    return driverVersion;
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

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getProductName()
   */
  public String getProductName()
  {
    return productName;
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

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getProperties()
   */
  public Map<String, Object> getProperties()
  {
    return Collections.unmodifiableSortedMap(dbProperties);
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
   * @see schemacrawler.schema.DatabaseInfo#getSchemaPattern()
   */
  public String getSchemaPattern()
  {
    return schemaPattern;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (catalog == null? 0: catalog.hashCode());
    result = prime * result
             + (columnDataTypes == null? 0: columnDataTypes.hashCode());
    result = prime * result
             + (dbProperties == null? 0: dbProperties.hashCode());
    result = prime * result + (productName == null? 0: productName.hashCode());
    result = prime * result
             + (productVersion == null? 0: productVersion.hashCode());
    result = prime * result
             + (schemaPattern == null? 0: schemaPattern.hashCode());
    return result;
  }

  /**
   * Sets the catalog.
   * 
   * @param catalog
   *        Catalog
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

  /**
   * Sets name of the driver.
   * 
   * @param driverName
   *        Driver name
   */
  public void setDriverName(final String driverName)
  {
    this.driverName = driverName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
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
   * Adds a table.
   * 
   * @param table
   *        Table
   */
  void addColumnDataType(final MutableColumnDataType columnDataType)
  {
    columnDataTypes.add(columnDataType);
  }

  NamedObjectList<MutableColumnDataType> getColumnDataTypesList()
  {
    return columnDataTypes;
  }

  MutableColumnDataType lookupByType(final int type)
  {
    MutableColumnDataType columnDataType = null;
    final MutableColumnDataType[] allColumnDataTypes = columnDataTypes.getAll()
      .toArray(new MutableColumnDataType[columnDataTypes.size()]);
    for (final MutableColumnDataType currentColumnDataType: allColumnDataTypes)
    {
      if (type == currentColumnDataType.getType())
      {
        columnDataType = currentColumnDataType;
        break;
      }
    }
    return columnDataType;
  }

  void putProperty(final String name, final Object value)
  {
    dbProperties.put(name, value);
  }

  void setConnectionUrl(final String connectionUrl)
  {
    this.connectionUrl = connectionUrl;
  }

  void setDriverVersion(final String driverVersion)
  {
    this.driverVersion = driverVersion;
  }

  void setJdbcDriverClassName(final String jdbcDriverClassName)
  {
    this.jdbcDriverClassName = jdbcDriverClassName;
  }

  void setProductName(final String productName)
  {
    this.productName = productName;
  }

  void setProductVersion(final String productVersion)
  {
    this.productVersion = productVersion;
  }

  void setSchemaPattern(final String schemaPattern)
  {
    this.schemaPattern = schemaPattern;
  }

}
