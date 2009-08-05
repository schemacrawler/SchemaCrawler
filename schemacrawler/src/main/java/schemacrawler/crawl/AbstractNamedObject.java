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
import java.util.LinkedHashMap;
import java.util.Map;

import schemacrawler.schema.NamedObject;
import schemacrawler.utility.Utility;

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
  private final Map<String, Object> attributeMap = new LinkedHashMap<String, Object>();
  private final NamedObjectSort comparator = NamedObjectSort.alphabetical;

  private transient int hashCode;

  AbstractNamedObject(final String name)
  {
    this.name = name;
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
   * @see schemacrawler.schema.NamedObject#getAttributes()
   */
  public Map<String, Object> getAttributes()
  {
    return Collections.unmodifiableMap(attributeMap);
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
    buildHashCode();
    return hashCode;
  }

  /**
   * {@inheritDoc}
   * 
   * @see NamedObject#setAttribute(String, Object)
   */
  public final void setAttribute(final String name, final Object value)
  {
    if (!Utility.isBlank(name))
    {
      if (value == null)
      {
        attributeMap.remove(name);
      }
      else
      {
        attributeMap.put(name, value);
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
    return name;
  }

  final void addAttributes(final Map<String, Object> values)
  {
    if (values != null)
    {
      attributeMap.putAll(values);
    }
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

  private void buildHashCode()
  {
    if (hashCode == 0)
    {
      hashCode = (name == null? super.hashCode(): name.hashCode());
    }
  }

}
