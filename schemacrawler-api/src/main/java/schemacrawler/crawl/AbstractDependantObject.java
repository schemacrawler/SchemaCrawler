/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import static sf.util.Utility.isBlank;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.DatabaseObjectReference;
import schemacrawler.schema.DependantObject;

/**
 * Represents the dependent of a database object, such as a column or an
 * index, which are dependents of a table.
 *
 * @author Sualeh Fatehi
 */
abstract class AbstractDependantObject<P extends DatabaseObjectReference & DatabaseObject>
  extends AbstractDatabaseObject
  implements DependantObject<P>
{

  private static final long serialVersionUID = -4327208866052082457L;

  private final P parent;

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
    final StringBuilder buffer = new StringBuilder();
    if (parent != null)
    {
      final String parentFullName = parent.getFullName();
      if (!isBlank(parentFullName))
      {
        buffer.append(parentFullName).append('.');
      }
    }
    final String quotedName = getName();
    if (!isBlank(quotedName))
    {
      buffer.append(quotedName);
    }
    return buffer.toString();
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

  @Override
  public final String getShortName()
  {
    final StringBuilder buffer = new StringBuilder();
    if (parent != null)
    {
      final String parentName = parent.getName();
      if (!isBlank(parentName))
      {
        buffer.append(parentName).append('.');
      }
    }
    final String quotedName = getName();
    if (!isBlank(quotedName))
    {
      buffer.append(quotedName);
    }
    return buffer.toString();
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
    result = prime * result + super.hashCode();
    return result;
  }

}
