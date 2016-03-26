/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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
   * @see schemacrawler.schemacrawler.InclusionRule#test(java.lang.String)
   */
  @Override
  public boolean test(final String text)
  {
    return inclusionRule.test(text);
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
