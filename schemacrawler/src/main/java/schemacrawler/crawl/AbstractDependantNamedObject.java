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


import schemacrawler.schema.DependantNamedObject;
import schemacrawler.schema.NamedObject;

/**
 * Represents a database object.
 * 
 * @author sfatehi
 */
abstract class AbstractDependantNamedObject
  extends AbstractNamedObject
  implements DependantNamedObject
{

  private NamedObject parent;

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
   * @see Object#toString()
   */
  public String toString()
  {
    return getFullName();
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
   * Sets the parent object.
   * 
   * @param parent
   *        Parent
   */
  void setParent(final NamedObject parent)
  {
    if (parent == null)
    {
      throw new IllegalArgumentException("Parent object not specified");
    }
    this.parent = parent;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#equals(java.lang.Object)
   */
  public boolean equals(final Object o)
  {
    if (o == null)
    {
      return false;
    }
    final DependantNamedObject childObject = (DependantNamedObject) o;
    if (!super.equals(childObject))
    {
      return false;
    }
    if (parent == null || !parent.equals(childObject.getParent()))
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
    int result;
    result = super.hashCode();
    if (parent != null)
    {
      result = 29 * result + parent.hashCode();
    }
    return result;
  }

}
