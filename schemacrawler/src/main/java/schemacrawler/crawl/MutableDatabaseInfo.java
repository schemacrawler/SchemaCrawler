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

package schemacrawler.crawl;


import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import schemacrawler.schema.DatabaseInfo;

/**
 * Database and connection information. Created from metadata returned
 * by a JDBC call, and other sources of information.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class MutableDatabaseInfo
  implements DatabaseInfo
{

  private static final long serialVersionUID = 4051323422934251828L;

  private static final String NEWLINE = System.getProperty("line.separator");

  private String productName;
  private String productVersion;
  private final SortedMap<String, Object> dbProperties = new TreeMap<String, Object>();
  private MutableJdbcDriverInfo driverInfo;

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
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Catalog#getJdbcDriverInfo()
   */
  public MutableJdbcDriverInfo getJdbcDriverInfo()
  {
    return driverInfo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getProductName()
   */
  public String getProductName()
  {
    return productName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getProductVersion()
   */
  public String getProductVersion()
  {
    return productVersion;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getProperties()
   */
  public Map<String, Object> getProperties()
  {
    return Collections.unmodifiableMap(dbProperties);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getProperty(java.lang.String)
   */
  public Object getProperty(final String name)
  {
    return dbProperties.get(name);
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (productName == null? 0: productName.hashCode());
    result = prime * result
             + (productVersion == null? 0: productVersion.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuilder info = new StringBuilder();
    info.append("-- database: ").append(getProductName()).append(" ")
      .append(getProductVersion()).append(NEWLINE);
    if (driverInfo != null)
    {
      info.append(driverInfo);
    }
    return info.toString();
  }

  void putProperty(final String name, final Object value)
  {
    dbProperties.put(name, value);
  }

  void setJdbcDriverInfo(final MutableJdbcDriverInfo driverInfo)
  {
    this.driverInfo = driverInfo;
  }

  void setProductName(final String productName)
  {
    this.productName = productName;
  }

  void setProductVersion(final String productVersion)
  {
    this.productVersion = productVersion;
  }

}
