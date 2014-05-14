/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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
import schemacrawler.utility.NamedObjectSort;

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

  AbstractNamedObject(final String name)
  {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    if (obj == null)
    {
      return -1;
    }

    return NamedObjectSort.alphabetical.compare(this, obj);
  }

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
    if (!(obj instanceof AbstractNamedObject))
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
   * @see schemacrawler.schema.NamedObjectWithAttributes#getFullName()
   */
  @Override
  public String getFullName()
  {
    return getName();
  }

  @Override
  public String getLookupKey()
  {
    return getFullName();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.NamedObjectWithAttributes#getName()
   */
  @Override
  public final String getName()
  {
    return name;
  }

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
   * @see Object#toString()
   */
  @Override
  public final String toString()
  {
    return getFullName();
  }

}
