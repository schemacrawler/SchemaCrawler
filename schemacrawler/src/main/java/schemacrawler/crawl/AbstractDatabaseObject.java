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


import schemacrawler.schema.DatabaseObject;

/**
 * Represents a database object.
 * 
 * @author sfatehi
 */
abstract class AbstractDatabaseObject
  extends AbstractNamedObject
  implements DatabaseObject
{

  private String schemaName;
  private String catalogName;

  AbstractDatabaseObject(String schemaName, String catalogName, String name)
  {
    super(name);
    this.schemaName = schemaName;
    this.catalogName = catalogName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    String toString = "";
    if (schemaName != null)
    {
      toString = schemaName + ".";
    }
    toString = toString + getName();
    return toString;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseObject#getSchemaName()
   */
  public final String getSchemaName()
  {
    return schemaName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.DatabaseObject#getCatalogName()
   */
  public final String getCatalogName()
  {
    return catalogName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String getFullName()
  {
    final StringBuffer buffer = new StringBuffer();
    if (schemaName != null && schemaName.length() > 0)
    {
      buffer.append(schemaName).append(".");
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
   * @see Object#equals(java.lang.Object)
   */
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof DatabaseObject))
    {
      return false;
    }

    final DatabaseObject databaseObject = (DatabaseObject) o;

    if (!super.equals(databaseObject))
    {
      return false;
    }
    if (catalogName == null
        || !catalogName.equals(databaseObject.getCatalogName()))
    {
      return false;
    }
    if (schemaName == null
        || !schemaName.equals(databaseObject.getSchemaName()))
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
    if (schemaName != null)
    {
      result = 29 * result + schemaName.hashCode();
    }
    if (catalogName != null)
    {
      result = 29 * result + catalogName.hashCode();
    }
    return result;
  }

}
