/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static sf.util.Utility.isBlank;

import java.io.Serializable;
import java.util.Arrays;

import schemacrawler.schema.Property;

abstract class AbstractProperty
  implements Property
{

  private static final long serialVersionUID = -7150431683440256142L;

  private final String name;
  private final Serializable value;

  AbstractProperty(final String name, final Serializable value)
  {
    if (isBlank(name))
    {
      throw new IllegalArgumentException("No property name provided");
    }
    this.name = name.trim();
    if (value != null && value.getClass().isArray())
    {
      this.value = (Serializable) Arrays.asList((Object[]) value);
    }
    else
    {
      this.value = value;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof AbstractProperty))
    {
      return false;
    }
    final AbstractProperty other = (AbstractProperty) obj;
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
    if (value == null)
    {
      if (other.value != null)
      {
        return false;
      }
    }
    else if (!value.equals(other.value))
    {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getName()
  {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Serializable getValue()
  {
    return value;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public final int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (name == null? 0: name.hashCode());
    result = prime * result + (value == null? 0: value.hashCode());
    return result;
  }

}
