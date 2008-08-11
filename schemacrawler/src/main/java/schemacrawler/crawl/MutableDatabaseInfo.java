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


import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import schemacrawler.schema.ColumnDataType;
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
  private final NamedObjectList<MutableColumnDataType> systemColumnDataTypes = new NamedObjectList<MutableColumnDataType>(NamedObjectSort.alphabetical);

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
    if (systemColumnDataTypes == null)
    {
      if (other.systemColumnDataTypes != null)
      {
        return false;
      }
    }
    else if (!systemColumnDataTypes.equals(other.systemColumnDataTypes))
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
    return true;
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
   * @see schemacrawler.schema.DatabaseInfo#getSystemColumnDataType(java.lang.String)
   */
  public ColumnDataType getSystemColumnDataType(final String name)
  {
    return systemColumnDataTypes.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseInfo#getSystemColumnDataTypes()
   */
  public ColumnDataType[] getSystemColumnDataTypes()
  {
    return systemColumnDataTypes.getAll()
      .toArray(new ColumnDataType[systemColumnDataTypes.size()]);
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime
             * result
             + (systemColumnDataTypes == null? 0: systemColumnDataTypes
               .hashCode());
    result = prime * result
             + (dbProperties == null? 0: dbProperties.hashCode());
    result = prime * result + (productName == null? 0: productName.hashCode());
    result = prime * result
             + (productVersion == null? 0: productVersion.hashCode());
    return result;
  }

  void putProperty(final String name, final Object value)
  {
    dbProperties.put(name, value);
  }

  void setProductName(final String productName)
  {
    this.productName = productName;
  }

  void setProductVersion(final String productVersion)
  {
    this.productVersion = productVersion;
  }

  void setSystemColumnDataTypes(final Set<MutableColumnDataType> columnDataTypes)
  {
    if (columnDataTypes != null)
    {
      for (final MutableColumnDataType columnDataType: columnDataTypes)
      {
        systemColumnDataTypes.add(columnDataType);
      }
    }
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
    info.append("-- database: ").append(getProductName()).append(" ")
      .append(getProductVersion()).append(NEWLINE);
    return info.toString();
  }

}
