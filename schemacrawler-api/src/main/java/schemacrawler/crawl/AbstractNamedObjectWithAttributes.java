/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static sf.util.Utility.isBlank;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.DescribedObject;

/**
 * Represents a named object.
 *
 * @author Sualeh Fatehi
 */
abstract class AbstractNamedObjectWithAttributes
  extends AbstractNamedObject
  implements AttributedObject, DescribedObject
{

  private static final long serialVersionUID = -1486322887991472729L;

  private String remarks;
  private final Map<String, Object> attributeMap;

  AbstractNamedObjectWithAttributes(final String name)
  {
    super(name);
    attributeMap = new HashMap<>();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.AttributedObject#getAttribute(java.lang.String,
   *      java.lang.Object)
   */
  @Override
  public final <T> T getAttribute(final String name, final T defaultValue)
  {
    final Object attributeValue = attributeMap.get(name);
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
   * @see schemacrawler.schema.NamedObjectWithAttributes#getAttributes()
   */
  @Override
  public final Map<String, Object> getAttributes()
  {
    return Collections.unmodifiableMap(attributeMap);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.DatabaseObject#getRemarks()
   */
  @Override
  public final String getRemarks()
  {
    return remarks;
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.AttributedObject#hasAttribute(java.lang.String)
   */
  @Override
  public boolean hasAttribute(final String name)
  {
    return attributeMap.containsKey(name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.NamedObjectWithAttributes#hasRemarks()
   */
  @Override
  public final boolean hasRemarks()
  {
    return remarks != null && !remarks.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void removeAttribute(final String name)
  {
    if (!isBlank(name))
    {
      attributeMap.remove(name);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see NamedObjectWithAttributes#setAttribute(String, Object)
   */
  @Override
  public final void setAttribute(final String name, final Object value)
  {
    if (!isBlank(name))
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

  protected final void addAttributes(final Map<String, Object> values)
  {
    if (values != null)
    {
      attributeMap.putAll(values);
    }
  }

  protected final void setRemarks(final String remarks)
  {
    if (remarks == null)
    {
      this.remarks = "";
    }
    else
    {
      this.remarks = remarks;
    }
  }

}
