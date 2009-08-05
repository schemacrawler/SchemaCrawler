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
import schemacrawler.schema.Schema;
import schemacrawler.utility.Utility;

/**
 * Represents a database object.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractDatabaseObject
  extends AbstractNamedObject
  implements DatabaseObject
{

  private static final long serialVersionUID = 3099561832386790624L;

  private final Schema schema;
  private final String fullName;

  AbstractDatabaseObject(final Schema schema, final String name)
  {
    super(name);
    this.schema = schema;
    this.fullName = buildFullName();
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
    final AbstractDatabaseObject other = (AbstractDatabaseObject) obj;
    if (schema == null)
    {
      if (other.schema != null)
      {
        return false;
      }
    }
    else if (!schema.equals(other.schema))
    {
      return false;
    }
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String getFullName()
  {
    return fullName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseObject#getSchema()
   */
  public final Schema getSchema()
  {
    return schema;
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
    result = prime * result + (schema == null? 0: schema.hashCode());
    result = prime * result + super.hashCode();
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

  private final String buildFullName()
  {
    final StringBuilder buffer = new StringBuilder();
    if (schema != null && !Utility.isBlank(schema.getFullName()))
    {
      buffer.append(schema.getFullName()).append(".");
    }
    if (!Utility.isBlank(getName()))
    {
      buffer.append(getName());
    }
    return buffer.toString();
  }

}
