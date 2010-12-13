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


import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Schema;
import sf.util.Utility;

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

  private transient String fullName;
  private transient int hashCode;

  AbstractDatabaseObject(final Schema schema, final String name)
  {
    super(name);
    this.schema = schema;
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
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
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
      final String schemaFullName = schema.getFullName();
      if (schema != null && !Utility.isBlank(schemaFullName))
      {
        buffer.append(schemaFullName).append('.');
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
      result = prime * result + (schema == null? 0: schema.hashCode());
      result = prime * result + super.hashCode();
      hashCode = result;
    }
  }

}
