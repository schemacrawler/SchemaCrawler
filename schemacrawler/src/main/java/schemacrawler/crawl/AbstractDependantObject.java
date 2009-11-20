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


import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.DependantObject;
import sf.util.Utility;

/**
 * Represents the dependent of a database object, such as a column or an
 * index, which are dependents of a table.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractDependantObject
  extends AbstractDatabaseObject
  implements DependantObject
{

  private static final long serialVersionUID = -4327208866052082457L;

  private final DatabaseObject parent;
  private transient String fullName;

  private transient int hashCode;

  AbstractDependantObject(final DatabaseObject parent, final String name)
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
    final AbstractDependantObject other = (AbstractDependantObject) obj;
    if (parent == null)
    {
      if (other.parent != null)
      {
        return false;
      }
    }
    else if (!parent.equals(other.parent))
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
  public final DatabaseObject getParent()
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

  private final void buildFullName()
  {
    if (fullName == null)
    {
      final StringBuilder buffer = new StringBuilder();
      if (parent != null && !Utility.isBlank(parent.getFullName()))
      {
        buffer.append(parent.getFullName()).append(".");
      }
      if (!Utility.isBlank(getName()))
      {
        buffer.append(getName());
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
