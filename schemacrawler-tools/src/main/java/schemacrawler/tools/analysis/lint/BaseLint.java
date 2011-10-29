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
package schemacrawler.tools.analysis.lint;


import sf.util.Utility;

public abstract class BaseLint
  implements Lint
{

  private static final long serialVersionUID = -8627082144974643415L;

  private final String id;
  private final String objectName;
  private final LintSeverity severity;
  private final String message;
  private final Object value;

  public BaseLint(final String id,
                  final String objectName,
                  final LintSeverity severity,
                  final String message,
                  final Object value)
  {
    if (Utility.isBlank(id))
    {
      throw new IllegalArgumentException("Lint id not provided");
    }
    this.id = id;

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
   * 
   * @see schemacrawler.tools.analysis.lint.Lint#compareTo(schemacrawler.tools.analysis.lint.BaseLint)
   */
  @Override
  public final int compareTo(final Lint lint)
  {
    if (lint == null)
    {
      return -1;
    }
    else
    {
      return id.compareTo(lint.getId());
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
    if (!(obj instanceof BaseLint))
    {
      return false;
    }
    final BaseLint other = (BaseLint) obj;
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
   * @see schemacrawler.tools.analysis.lint.Lint#getMessage()
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
   * @see schemacrawler.tools.analysis.lint.Lint#getValue()
   */
  @Override
  public final Object getValue()
  {
    return value;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.lint.Lint#getValueAsString()
   */
  @Override
  public abstract String getValueAsString();

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (id == null? 0: id.hashCode());
    result = prime * result + (message == null? 0: message.hashCode());
    result = prime * result + (objectName == null? 0: objectName.hashCode());
    result = prime * result + (severity == null? 0: severity.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    return message + "=" + getValueAsString();
  }

}
