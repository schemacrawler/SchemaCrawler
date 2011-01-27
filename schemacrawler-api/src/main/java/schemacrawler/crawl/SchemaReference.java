/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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


import java.io.Serializable;

import sf.util.Utility;

final class SchemaReference
  implements Serializable, Comparable<SchemaReference>
{

  private static final long serialVersionUID = -5309848447599233878L;

  private final String catalogName;
  private final String schemaName;
  private transient String fullName;

  SchemaReference(final String catalogName, final String schemaName)
  {
    this.catalogName = catalogName;
    this.schemaName = schemaName;
  }

  public int compareTo(final SchemaReference otherSchemaRef)
  {
    if (otherSchemaRef == null)
    {
      return -1;
    }
    else
    {
      return getFullName().compareTo(otherSchemaRef.getFullName());
    }
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
    if (!(obj instanceof SchemaReference))
    {
      return false;
    }
    final SchemaReference other = (SchemaReference) obj;
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
    return true;
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
    int result = 1;
    result = prime * result + (catalogName == null? 0: catalogName.hashCode());
    result = prime * result + (schemaName == null? 0: schemaName.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    return getFullName();
  }

  String getCatalogName()
  {
    return catalogName;
  }

  String getFullName()
  {
    buildFullName();
    return fullName;
  }

  String getSchemaName()
  {
    return schemaName;
  }

  private void buildFullName()
  {
    if (fullName == null)
    {
      final boolean hasCatalogName = !Utility.isBlank(catalogName);
      final boolean hasSchemaName = !Utility.isBlank(schemaName);
      fullName = (hasCatalogName? catalogName: "")
                 + (hasCatalogName && hasSchemaName? ".": "")
                 + (hasSchemaName? schemaName: "");
    }
  }

}
