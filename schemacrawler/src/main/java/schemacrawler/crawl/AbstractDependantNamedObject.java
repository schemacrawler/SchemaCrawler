/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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


import schemacrawler.schema.DependantNamedObject;
import schemacrawler.schema.NamedObject;

/**
 * Represents a database object.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractDependantNamedObject
  extends AbstractNamedObject
  implements DependantNamedObject
{

  private final NamedObject parent;

  AbstractDependantNamedObject(final String name, final NamedObject parent)
  {
    super(name);
    if (parent == null)
    {
      throw new IllegalArgumentException("Parent object not specified");
    }
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
    final AbstractDependantNamedObject other = (AbstractDependantNamedObject) obj;
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
   * @see Object#toString()
   */
  public String getFullName()
  {
    final StringBuffer buffer = new StringBuffer();
    if (parent != null && parent.getName().length() > 0)
    {
      buffer.append(parent.getName()).append(".");
    }
    if (getName() != null)
    {
      buffer.append(getName());
    }
    return buffer.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DependantNamedObject#getParent()
   */
  public final NamedObject getParent()
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
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (parent == null? 0: parent.hashCode());
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
    return getFullName();
  }

}
