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
package schemacrawler.tools.analysis;


public abstract class BaseLint
  implements Lint
{

  private static final long serialVersionUID = -8627082144974643415L;

  private final String id;
  private final LintSeverity severity;
  private final String summary;
  private final Object value;

  public BaseLint(final String id,
                  final LintSeverity severity,
                  final String summary,
                  final Object lintValue)
  {
    this.id = id;
    this.severity = severity;
    this.summary = summary;
    value = lintValue;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.Lint#compareTo(schemacrawler.tools.analysis.BaseLint)
   */
  @Override
  public final int compareTo(final Lint lint)
  {
    if (summary == null || lint == null)
    {
      return -1;
    }
    else
    {
      return summary.compareTo(lint.getSummary());
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
    if (severity != other.severity)
    {
      return false;
    }
    if (summary == null)
    {
      if (other.summary != null)
      {
        return false;
      }
    }
    else if (!summary.equals(other.summary))
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

  @Override
  public LintSeverity getSeverity()
  {
    return severity;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.Lint#getSummary()
   */
  @Override
  public final String getSummary()
  {
    return summary;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.Lint#getValue()
   */
  @Override
  public final Object getValue()
  {
    return value;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.Lint#getValueAsString()
   */
  @Override
  public abstract String getValueAsString();

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (id == null? 0: id.hashCode());
    result = prime * result + (severity == null? 0: severity.hashCode());
    result = prime * result + (summary == null? 0: summary.hashCode());
    result = prime * result + (value == null? 0: value.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    return summary + "=" + getValueAsString();
  }

}
