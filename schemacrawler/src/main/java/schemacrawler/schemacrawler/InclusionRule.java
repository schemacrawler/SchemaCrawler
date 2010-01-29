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

package schemacrawler.schemacrawler;


import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import sf.util.Utility;

/**
 * Specifies inclusion and exclusion patterns that can be applied to the
 * names of database objects.
 * 
 * @author Sualeh Fatehi
 */
public final class InclusionRule
  implements Serializable
{

  /**
   * Exclude nothing
   */
  public static final String NONE = "";
  /**
   * Include everything.
   */
  public static final String ALL = ".*";

  private static final Logger LOGGER = Logger.getLogger(InclusionRule.class
    .getName());

  private static final long serialVersionUID = 3443758881974362293L;

  private final Pattern patternInclude;
  private final Pattern patternExclude;

  /**
   * Set include and exclude patterns.
   * 
   * @param patternInclude
   *        Inclusion pattern
   * @param patternExclude
   *        Exclusion pattern
   */
  public InclusionRule(final Pattern patternInclude,
                       final Pattern patternExclude)
  {
    this.patternInclude = patternInclude;
    this.patternExclude = patternExclude;
  }

  /**
   * Set include and exclude patterns.
   * 
   * @param patternInclude
   *        Inclusion pattern
   * @param patternExclude
   *        Exclusion pattern
   */
  public InclusionRule(final String patternInclude, final String patternExclude)
  {
    this(Pattern.compile(patternInclude), Pattern.compile(patternExclude));
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
    final InclusionRule other = (InclusionRule) obj;
    if (patternExclude == null)
    {
      if (other.patternExclude != null)
      {
        return false;
      }
    }
    else if (!patternExclude.equals(other.patternExclude))
    {
      return false;
    }
    if (patternInclude == null)
    {
      if (other.patternInclude != null)
      {
        return false;
      }
    }
    else if (!patternInclude.equals(other.patternInclude))
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
             + (patternExclude == null? 0: patternExclude.hashCode());
    result = prime * result
             + (patternInclude == null? 0: patternInclude.hashCode());
    return result;
  }

  /**
   * Checks whether to add a named object after considering the include
   * and exclude patterns.
   * 
   * @param name
   *        Name to check
   * @return Whether the name should be included or not
   */
  public boolean include(final String name)
  {

    final String actionMessage;
    boolean include = false;
    if (!Utility.isBlank(name))
    {
      if (!patternInclude.matcher(name).matches())
      {
        actionMessage = "Excluding " + name
                        + " since it does not match the include pattern";
      }
      else if (patternExclude.matcher(name).matches())
      {
        actionMessage = "Excluding " + name
                        + " since it matches the exclude pattern";
      }
      else
      {
        actionMessage = "Including " + name;
        include = true;
      }
    }
    else
    {
      actionMessage = "Excluding, since name is bank";
    }

    if (LOGGER.isLoggable(Level.FINE))
    {
      final StackTraceElement caller = new Exception().getStackTrace()[1];
      LOGGER.logp(Level.FINE,
                  caller.getClassName(),
                  caller.getMethodName(),
                  actionMessage);
    }

    return include;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return String.format("%s@%h-[:include:%s:exclude:%s:]",
                         getClass().getSimpleName(),
                         System.identityHashCode(this),
                         patternInclude.pattern(),
                         patternExclude.pattern());
  }

}
