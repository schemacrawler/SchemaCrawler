/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


import schemacrawler.schema.DependantNamedObject;
import schemacrawler.schema.NamedObject;

/**
 * Represents the dependent of a database object, such as a column or an
 * index, which are dependents of a table.
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
   * @see schemacrawler.schema.DependantNamedObject#getFullName()
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
