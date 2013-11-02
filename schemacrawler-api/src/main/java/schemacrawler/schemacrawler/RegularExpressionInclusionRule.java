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

package schemacrawler.schemacrawler;


import java.util.regex.Pattern;

/**
 * Specifies exclusion patterns that can be applied to the names,
 * definitions, and other attributes of named objects.
 * 
 * @author Sualeh Fatehi
 */
public final class RegularExpressionInclusionRule
  implements InclusionRule
{

  private static final long serialVersionUID = 6274652266761961575L;
  private final InclusionRule inclusionRule;

  /**
   * Set include pattern. Exclude nothing.
   * 
   * @param patternInclude
   *        Inclusion pattern. If null, includes everything.
   */
  public RegularExpressionInclusionRule(final Pattern patternInclude)
  {
    if (patternInclude == null)
    {
      inclusionRule = new IncludeAll();
    }
    else
    {
      inclusionRule = new RegularExpressionRule(patternInclude, null);
    }
  }

  /**
   * Set include pattern. Exclude nothing.
   * 
   * @param patternInclude
   *        Inclusion pattern. If null, includes everything.
   */
  public RegularExpressionInclusionRule(final String patternInclude)
  {
    this(patternInclude == null? null: Pattern.compile(patternInclude));
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
    final RegularExpressionInclusionRule other = (RegularExpressionInclusionRule) obj;
    if (inclusionRule == null)
    {
      if (other.inclusionRule != null)
      {
        return false;
      }
    }
    else if (!inclusionRule.equals(other.inclusionRule))
    {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + (inclusionRule == null? 0: inclusionRule.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.InclusionRule#include(java.lang.String)
   */
  @Override
  public boolean include(final String text)
  {
    return inclusionRule.include(text);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return inclusionRule.toString();
  }

}
