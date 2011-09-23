/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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


import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.DependantObject;
import sf.util.Utility;

/**
 * Represents the dependent of a database object, such as a column or an
 * index, which are dependents of a table.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractDependantObject<P extends DatabaseObject>
  extends AbstractDatabaseObject
  implements DependantObject<P>
{

  private static final long serialVersionUID = -4327208866052082457L;

  private final P parent;
  private transient String fullName;

  private transient int hashCode;

  AbstractDependantObject(final P parent, final String name)
  {
    super(parent.getSchema(), name);
    this.parent = parent;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    if (!super.equals(obj))
    {
      return false;
    }
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    final DependantObject<P> other = (DependantObject<P>) obj;
    if (parent == null)
    {
      if (other.getParent() != null)
      {
        return false;
      }
    }
    else if (!parent.equals(other.getParent()))
    {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.crawl.AbstractDatabaseObject#getFullName()
   */
  @Override
  public String getFullName()
  {
    buildFullName();
    return fullName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DependantObject#getParent()
   */
  @Override
  public final P getParent()
  {
    return parent;
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
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return getFullName();
  }

  private void buildFullName()
  {
    if (fullName == null)
    {
      final StringBuilder buffer = new StringBuilder();
      final String parentFullName = parent.getFullName();
      if (parent != null && !Utility.isBlank(parentFullName))
      {
        buffer.append(parentFullName).append('.');
      }
      final String quotedName = getName();
      if (!Utility.isBlank(quotedName))
      {
        buffer.append(quotedName);
      }
      fullName = buffer.toString();
    }
  }

  private void buildHashCode()
  {
    if (hashCode == 0)
    {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (parent == null? 0: parent.hashCode());
      result = prime * result + super.hashCode();
      hashCode = result;
    }
  }

}
