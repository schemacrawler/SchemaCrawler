/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.DependantObject;

/**
 * Represents the dependent of a database object, such as a column or an
 * index, which are dependents of a table.
 *
 * @author Sualeh Fatehi
 */
abstract class AbstractDependantObject<D extends DatabaseObject>
  extends AbstractDatabaseObject
  implements DependantObject<D>
{

  private static final long serialVersionUID = -4327208866052082457L;

  private final DatabaseObjectReference<D> parent;

  AbstractDependantObject(final DatabaseObjectReference<D> parent,
                          final String name)
  {
    super(requireNonNull(parent, "Parent of dependent object not provided")
      .get().getSchema(), name);
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
    final DependantObject<D> other = (DependantObject<D>) obj;
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
    final StringBuilder buffer = new StringBuilder(64);
    if (parent != null)
    {
      final String parentFullName = parent.get().getFullName();
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
  public final D getParent()
  {
    return parent.get();
  }

  @Override
  public final String getShortName()
  {
    final StringBuilder buffer = new StringBuilder(64);
    if (parent != null)
    {
      final String parentName = parent.get().getName();
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

  @Override
  public boolean isParentPartial()
  {
    return parent.isPartialDatabaseObjectReference();
  }

}
