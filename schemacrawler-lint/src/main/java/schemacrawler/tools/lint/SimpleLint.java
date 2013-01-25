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
package schemacrawler.tools.lint;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import schemacrawler.schema.NamedObject;
import sf.util.ObjectToString;
import sf.util.Utility;

public final class SimpleLint<V extends Serializable>
  implements Lint<V>
{

  private static final long serialVersionUID = -8627082144974643415L;

  private final String id;
  private final String objectName;
  private final LintSeverity severity;
  private final String message;
  private final V value;

  public SimpleLint(final String id,
                    final String objectName,
                    final LintSeverity severity,
                    final String message,
                    final V value)
  {
    if (Utility.isBlank(id))
    {
      throw new IllegalArgumentException("Lint id not provided");
    }
    this.id = id;

    if (objectName == null)
    {
      throw new IllegalArgumentException("Object name not provided");
    }
    this.objectName = objectName;

    if (severity == null)
    {
      this.severity = LintSeverity.critical;
    }
    else
    {
      this.severity = severity;
    }

    if (Utility.isBlank(message))
    {
      throw new IllegalArgumentException("Lint message not provided");
    }
    this.message = message;

    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int compareTo(final Lint<?> lint)
  {
    if (lint == null)
    {
      return -1;
    }

    int compareTo = 0;
    compareTo = objectName.compareTo(lint.getObjectName());
    if (compareTo != 0)
    {
      return compareTo;
    }
    compareTo = severity.compareTo(lint.getSeverity());
    compareTo *= -1; // Reverse
    if (compareTo != 0)
    {
      return compareTo;
    }
    compareTo = id.compareTo(lint.getId());
    if (compareTo != 0)
    {
      return compareTo;
    }
    compareTo = message.compareTo(lint.getMessage());

    return compareTo;
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
    if (!(obj instanceof SimpleLint))
    {
      return false;
    }
    final SimpleLint<?> other = (SimpleLint<?>) obj;
    if (id == null)
    {
      if (other.id != null)
      {
        return false;
      }
    }
    else if (!id.equals(other.id))
    {
      return false;
    }
    if (message == null)
    {
      if (other.message != null)
      {
        return false;
      }
    }
    else if (!message.equals(other.message))
    {
      return false;
    }
    if (objectName == null)
    {
      if (other.objectName != null)
      {
        return false;
      }
    }
    else if (!objectName.equals(other.objectName))
    {
      return false;
    }
    if (severity != other.severity)
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

  @Override
  public String getId()
  {
    return id;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.lint.Lint#getMessage()
   */
  @Override
  public final String getMessage()
  {
    return message;
  }

  @Override
  public String getObjectName()
  {
    return objectName;
  }

  @Override
  public LintSeverity getSeverity()
  {
    return severity;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.lint.Lint#getValue()
   */
  @Override
  public final V getValue()
  {
    return value;
  }

  @Override
  public String getValueAsString()
  {
    if (value != null)
    {
      final Class<? extends Object> valueClass = value.getClass();
      Object valueObject = value;

      if (valueClass.isArray()
          && NamedObject.class.isAssignableFrom(valueClass.getComponentType()))
      {
        valueObject = Arrays.asList(Arrays.copyOf((Object[]) value,
                                                  ((Object[]) value).length,
                                                  NamedObject[].class));
      }

      if (Iterable.class.isAssignableFrom(valueObject.getClass()))
      {
        final List<String> list = new ArrayList<String>();
        for (final Object valuePart: (Iterable<?>) valueObject)
        {
          if (valuePart instanceof NamedObject)
          {
            list.add(((NamedObject) valuePart).getName());
          }
          else
          {
            list.add(valuePart.toString());
          }
        }
        valueObject = list;
      }
      else if (NamedObject.class.isAssignableFrom(valueClass))
      {
        valueObject = ((NamedObject) valueObject).getName();
      }
      else
      {
        valueObject = value;
      }
      return ObjectToString.toString(valueObject);
    }
    else
    {
      return "";
    }
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (id == null? 0: id.hashCode());
    result = prime * result + (message == null? 0: message.hashCode());
    result = prime * result + (objectName == null? 0: objectName.hashCode());
    result = prime * result + (severity == null? 0: severity.hashCode());
    result = prime * result + (value == null? 0: value.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    final String valueString;
    if (value != null && !(value instanceof Boolean))
    {
      valueString = ": " + getValueAsString();
    }
    else
    {
      valueString = "";
    }
    return String.format("[%s] %s%s", objectName, message, valueString);
  }

}
