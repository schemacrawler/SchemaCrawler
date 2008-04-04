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
    result = prime * result + (productName == null? 0: productName.hashCode());
    result = prime * result
             + (productVersion == null? 0: productVersion.hashCode());
    result = prime * result
             + (schemaPattern == null? 0: schemaPattern.hashCode());
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

    final StringBuffer info = new StringBuffer();

    info.append("-- database: ").append(getProductName()).append(" ")
      .append(getProductVersion()).append(NEWLINE)
      .append("-- schema pattern: ").append(getSchemaPattern());

    return info.toString();

  }

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

  void setCatalog(final String catalog)
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
