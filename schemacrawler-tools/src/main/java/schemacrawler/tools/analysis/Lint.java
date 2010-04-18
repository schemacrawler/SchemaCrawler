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
package schemacrawler.tools.analysis;


import java.io.Serializable;

public abstract class Lint
  implements Serializable, Comparable<Lint>
{

  private static final long serialVersionUID = -8627082144974643415L;

  public static final String LINT_KEY = "schemacrawler.lint";

  private final String description;
  private final Object lintValue;

  Lint(final String description, final Serializable lintValue)
  {
    this.description = description;
    this.lintValue = lintValue;
  }

  public final int compareTo(final Lint lint)
  {
    if (description == null || lint == null)
    {
      return -1;
    }
    else
    {
      return description.compareTo(lint.description);
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
    if (!(obj instanceof Lint))
    {
      return false;
    }
    final Lint other = (Lint) obj;
    if (description == null)
    {
      if (other.description != null)
      {
        return false;
      }
    }
    else if (!description.equals(other.description))
    {
      return false;
    }
    if (lintValue == null)
    {
      if (other.lintValue != null)
      {
        return false;
      }
    }
    else if (!lintValue.equals(other.lintValue))
    {
      return false;
    }
    return true;
  }

  public final String getDescription()
  {
    return description;
  }

  public final Object getLintValue()
  {
    return lintValue;
  }

  public abstract String getLintValueAsString();

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (description == null? 0: description.hashCode());
    result = prime * result + (lintValue == null? 0: lintValue.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    return description + "=" + getLintValueAsString();
  }

}
