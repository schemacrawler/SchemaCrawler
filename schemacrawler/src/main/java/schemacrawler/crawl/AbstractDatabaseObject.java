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


import schemacrawler.schema.DatabaseObject;

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

  private final String schemaName;
  private final String catalogName;

  AbstractDatabaseObject(final String catalogName,
                         final String schemaName,
                         final String name)
  {
    super(name);
    this.catalogName = catalogName;
    this.schemaName = schemaName;
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
    final AbstractDatabaseObject other = (AbstractDatabaseObject) obj;
    if (catalogName == null)
    {
      if (other.catalogName != null)
      {
        return false;
      }
    }
    else if (!catalogName.equals(other.catalogName))
    {
      return false;
    }
    if (schemaName == null)
    {
      if (other.schemaName != null)
      {
        return false;
      }
    }
    else if (!schemaName.equals(other.schemaName))
    {
      return false;
    }
    return super.equals(obj);
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
   * @see schemacrawler.schema.DatabaseObject#getSchemaName()
   */
  public final String getSchemaName()
  {
    return schemaName;
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
    result = prime * result + (catalogName == null? 0: catalogName.hashCode());
    result = prime * result + (schemaName == null? 0: schemaName.hashCode());
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
    String toString = "";
    if (!(schemaName == null || schemaName.trim().length() == 0))
    {
      toString = schemaName + ".";
    }
    toString = toString + getName();
    return toString;
  }

}
