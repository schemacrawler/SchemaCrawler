/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
package schemacrawler.schema;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import sf.util.Utility;

public final class SchemaReference
  implements Schema
{

  private static final long serialVersionUID = -5309848447599233878L;

  private final String catalogName;
  private final String schemaName;
  private transient String fullName;
  private final Map<String, Object> attributeMap = new HashMap<String, Object>();

  public SchemaReference()
  {
    this(null, null);
  }

  public SchemaReference(final String catalogName, final String schemaName)
  {
    this.catalogName = catalogName;
    this.schemaName = schemaName;
  }

  @Override
  public int compareTo(final NamedObject otherSchemaRef)
  {
    if (otherSchemaRef == null)
    {
      return -1;
    }
    else
    {
      return getFullName().replaceAll("\"", "").compareTo(otherSchemaRef
        .getFullName().replaceAll("\"", ""));
    }
  }

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
    final SchemaReference other = (SchemaReference) obj;
    if (attributeMap == null)
    {
      if (other.attributeMap != null)
      {
        return false;
      }
    }
    else if (!attributeMap.equals(other.attributeMap))
    {
      return false;
    }
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
   * @see schemacrawler.schema.NamedObject#getAttribute(java.lang.String)
   */
  @Override
  public final Object getAttribute(final String name)
  {
    return attributeMap.get(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getAttribute(java.lang.String,
   *      java.lang.Object)
   */
  @Override
  public final <T> T getAttribute(final String name, final T defaultValue)
  {
    final Object attributeValue = getAttribute(name);
    if (attributeValue == null)
    {
      return defaultValue;
    }
    else
    {
      try
      {
        return (T) attributeValue;
      }
      catch (final ClassCastException e)
      {
        return defaultValue;
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getAttributes()
   */
  @Override
  public final Map<String, Object> getAttributes()
  {
    return Collections.unmodifiableMap(attributeMap);
  }

  @Override
  public String getCatalogName()
  {
    return catalogName;
  }

  @Override
  public String getFullName()
  {
    buildFullName();
    return fullName;
  }

  @Override
  public String getLookupKey()
  {
    return getFullName();
  }

  @Override
  public String getName()
  {
    return schemaName;
  }

  @Override
  public String getRemarks()
  {
    return "";
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + (attributeMap == null? 0: attributeMap.hashCode());
    result = prime * result + (catalogName == null? 0: catalogName.hashCode());
    result = prime * result + (schemaName == null? 0: schemaName.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see NamedObject#setAttribute(String, Object)
   */
  @Override
  public final void setAttribute(final String name, final Object value)
  {
    if (!Utility.isBlank(name))
    {
      if (value == null)
      {
        attributeMap.remove(name);
      }
      else
      {
        attributeMap.put(name, value);
      }
    }
  }

  @Override
  public String toString()
  {
    return getFullName();
  }

  private void buildFullName()
  {
    if (fullName == null)
    {
      final boolean hasCatalogName = !Utility.isBlank(catalogName);
      final boolean hasSchemaName = !Utility.isBlank(getName());
      fullName = (hasCatalogName? catalogName: "")
                 + (hasCatalogName && hasSchemaName? ".": "")
                 + (hasSchemaName? getName(): "");
    }
  }

}
