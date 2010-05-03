/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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
 * Represents the dependent of a named object.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractDependantNamedObject
  extends AbstractNamedObject
  implements DependantNamedObject
{

  private static final long serialVersionUID = -4327208866052082457L;

  private final NamedObject parent;

  private transient int hashCode;

  AbstractDependantNamedObject(final NamedObject parent,
                               final String name,
                               final String quoteCharacter)
  {
    super(name);
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
    buildHashCode();
    return hashCode;
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
