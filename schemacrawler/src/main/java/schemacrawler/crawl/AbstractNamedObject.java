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


import java.util.HashMap;
import java.util.Map;

import schemacrawler.schema.NamedObject;
import schemacrawler.util.AlphabeticalSortComparator;
import schemacrawler.util.SerializableComparator;

/**
 * Represents a named object.
 * 
 * @author sfatehi
 */
abstract class AbstractNamedObject
  implements NamedObject
{

  private final String name;
  private String remarks;
  private final Map attributeMap = new HashMap();
  private final SerializableComparator comparator = new AlphabeticalSortComparator();

  AbstractNamedObject(final String name)
  {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  public int compareTo(final Object obj)
  {
    return comparator.compare(this, obj);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof NamedObject))
    {
      return false;
    }
    final NamedObject namedObject = (NamedObject) o;
    if (!name.equals(namedObject.getName()))
    {
      return false;
    }

    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see NamedObject#getAttribute(String)
   */
  public Object getAttribute(final String name)
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
   * @see Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    if (name != null)
    {
      result = name.hashCode();
    }
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see NamedObject#setAttribute(String, Object)
   */
  public void setAttribute(final String name, final Object value)
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

  /**
   * Setter for property remarks.
   * 
   * @param remarks
   *        New value of property remarks.
   */
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
