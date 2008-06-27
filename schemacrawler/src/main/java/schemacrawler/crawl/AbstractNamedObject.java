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


import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import schemacrawler.schema.NamedObject;

/**
 * Represents a named object.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractNamedObject
  implements NamedObject
{

  private static final long serialVersionUID = -1486322887991472729L;

  private final String name;
  private String remarks;
  private final SortedMap<String, Object> attributeMap = new TreeMap<String, Object>();
  private final NamedObjectSort comparator = NamedObjectSort.alphabetical;

  AbstractNamedObject(final String name)
  {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#addAttributes(java.util.Map)
   */
  public final void addAttributes(final Map<String, Object> values)
  {
    attributeMap.putAll(values);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(final NamedObject obj)
  {
    return comparator.compare(this, obj);
  }

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
    final AbstractNamedObject other = (AbstractNamedObject) obj;
    if (name == null)
    {
      if (other.name != null)
      {
        return false;
      }
    }
    else if (!name.equals(other.name))
    {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getAttribute(java.lang.String)
   */
  public final Object getAttribute(final String name)
  {
    return attributeMap.get(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getName()
   */
  public final String getName()
  {
    return name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseObject#getRemarks()
   */
  public final String getRemarks()
  {
    return remarks;
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
    result = prime * result + (name == null? 0: name.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see NamedObject#setAttribute(String, Object)
   */
  public final void setAttribute(final String name, final Object value)
  {
    attributeMap.put(name, value);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return name;
  }

  final void setRemarks(final String remarks)
  {
    if (remarks == null)
    {
      this.remarks = "";
    }
    else
    {
      this.remarks = remarks;
    }
  }

}
