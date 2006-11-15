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

  private String name;
  private String remarks;
  private final SerializableComparator comparator = new AlphabeticalSortComparator();

  AbstractNamedObject(final String name)
  {
    super();
    this.name = name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    return name;
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

  /**
   * {@inheritDoc}
   * 
   * @see Object#equals(java.lang.Object)
   */
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
   * @see Object#hashCode()
   */
  public int hashCode()
  {
    int result = super.hashCode();
    if (name != null)
    {
      result = name.hashCode();
    }
    return result;
  }

}
