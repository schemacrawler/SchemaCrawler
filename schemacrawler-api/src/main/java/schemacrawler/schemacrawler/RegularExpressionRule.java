/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static sf.util.Utility.isBlank;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.regex.Pattern;

import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * Specifies inclusion and exclusion patterns that can be applied to the names,
 * definitions, and other attributes of named objects.
 *
 * @author Sualeh Fatehi
 */
public final class RegularExpressionRule
  implements InclusionRuleWithRegularExpression
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(RegularExpressionRule.class.getName());

  private static final long serialVersionUID = 3443758881974362293L;
  private final Pattern patternExclude;
  private final Pattern patternInclude;

  /**
   * Set include and exclude patterns.
   *
   * @param patternInclude
   *   Inclusion pattern. If null, includes everything.
   * @param patternExclude
   *   Exclusion pattern. If null, excludes nothing.
   */
  public RegularExpressionRule(final Pattern patternInclude,
                               final Pattern patternExclude)
  {
    if (patternInclude == null)
    {
      this.patternInclude =
        InclusionRuleWithRegularExpression.super.getInclusionPattern();
    }
    else
    {
      this.patternInclude = patternInclude;
    }

    if (patternExclude == null)
    {
      this.patternExclude =
        InclusionRuleWithRegularExpression.super.getExclusionPattern();
    }
    else
    {
      this.patternExclude = patternExclude;
    }
  }

  /**
   * Set include and exclude patterns.
   *
   * @param patternInclude
   *   Inclusion pattern. If null, includes everything.
   * @param patternExclude
   *   Exclusion pattern. If null, excludes nothing.
   */
  public RegularExpressionRule(final String patternInclude,
                               final String patternExclude)
  {
    this(patternInclude == null? null: Pattern.compile(patternInclude),
         patternExclude == null? null: Pattern.compile(patternExclude));
  }

  @Override
  public Pattern getExclusionPattern()
  {
    return patternExclude;
  }

  @Override
  public Pattern getInclusionPattern()
  {
    return patternInclude;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
      prime * result + (patternExclude == null? 0: patternExclude.hashCode());
    result =
      prime * result + (patternInclude == null? 0: patternInclude.hashCode());
    return result;
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
    final RegularExpressionRule other = (RegularExpressionRule) obj;
    if (patternExclude == null)
    {
      if (other.patternExclude != null)
      {
        return false;
      }
    }
    else if (!patternExclude
      .pattern()
      .equals(other.patternExclude.pattern()))
    {
      return false;
    }
    if (patternInclude == null)
    {
      return other.patternInclude == null;
    }
    else
    {
      return patternInclude
        .pattern()
        .equals(other.patternInclude.pattern());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return String.format("%s@%h {+/%s/ -/%s/}",
                         getClass().getSimpleName(),
                         System.identityHashCode(this),
                         patternInclude.pattern(),
                         patternExclude.pattern());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean test(final String text)
  {

    final Supplier<String> actionMessage;
    boolean include = false;
    if (!isBlank(text))
    {
      if (!patternInclude
        .matcher(text)
        .matches())
      {
        actionMessage = new StringFormat(
          "Excluding <%s> since it does not match /%s/",
          text,
          patternInclude.pattern());
      }
      else if (patternExclude
        .matcher(text)
        .matches())
      {
        actionMessage = new StringFormat("Excluding <%s> since it matches /%s/",
                                         text,
                                         patternExclude.pattern());
      }
      else
      {
        actionMessage = new StringFormat("Including <%s> since it matches /%s/",
                                         text,
                                         patternInclude.pattern());
        include = true;
      }
    }
    else
    {
      actionMessage = new StringFormat("Excluding, since text is blank");
    }

    // Log caller
    if (LOGGER.isLoggable(Level.FINE))
    {
      LOGGER.log(Level.FINE, 5, actionMessage.get(), null);
    }

    return include;
  }

}
