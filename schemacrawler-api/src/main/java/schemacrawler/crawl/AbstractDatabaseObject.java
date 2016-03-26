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
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.TypedObject;

/**
 * Represents a database object.
 *
 * @author Sualeh Fatehi
 */
abstract class AbstractDatabaseObject
  extends AbstractNamedObjectWithAttributes
  implements DatabaseObject
{

  private static final long serialVersionUID = 3099561832386790624L;

  private final Schema schema;

  AbstractDatabaseObject(final Schema schema, final String name)
  {
    super(name);
    this.schema = requireNonNull(schema, "No schema provided");
  }

  @Override
  public int compareTo(final NamedObject obj)
  {
    if (obj == null)
    {
      return -1;
    }

    if (obj instanceof DatabaseObject)
    {
      final int schemaCompareTo = getSchema()
        .compareTo(((DatabaseObject) obj).getSchema());
      if (schemaCompareTo != 0)
      {
        return schemaCompareTo;
      }
      if (this instanceof TypedObject && obj instanceof TypedObject)
      {
        try
        {
          final int typeCompareTo = ((TypedObject) this).getType()
            .compareTo(((TypedObject) obj).getType());
          if (typeCompareTo != 0)
          {
            return typeCompareTo;
          }
        }
        catch (final Exception e)
        {
          // Ignore, since getType() may not be implemented by partial
          // database
          // objects
        }
      }
    }

    return super.compareTo(obj);
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
    final StringBuilder buffer = new StringBuilder(64);
    if (schema != null)
    {
      final String schemaFullName = schema.getFullName();
      if (!isBlank(schemaFullName))
      {
        buffer.append(schemaFullName).append('.');
      }
    }
    final String quotedName = getName();
    if (!isBlank(quotedName))
    {
      buffer.append(quotedName);
    }
    return buffer.toString();
  }

  @Override
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

}
