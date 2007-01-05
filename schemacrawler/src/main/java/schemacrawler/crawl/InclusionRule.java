/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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


import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Specifies inclusion and exclusion patterns that can be applied to the
 * names of database objects.
 * 
 * @author sfatehi
 */
public final class InclusionRule
  implements Serializable
{

  public static final String EXCLUDE_NONE = "";
  public static final String INCLUDE_ALL = ".*";
  public static final Pattern EXCLUDE_NONE_PATTERN = Pattern
    .compile(EXCLUDE_NONE);
  public static final Pattern INCLUDE_ALL_PATTERN = Pattern
    .compile(INCLUDE_ALL);

  private static final Logger LOGGER = Logger.getLogger(InclusionRule.class
    .getName());

  private static final long serialVersionUID = 3443758881974362293L;

  private final Pattern patternInclude;
  private final Pattern patternExclude;

  /**
   * Include all, exclude none.
   */
  public InclusionRule()
  {
    this(INCLUDE_ALL_PATTERN, EXCLUDE_NONE_PATTERN);
  }

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

  /**
   * Adds a named object after considering the include and exclude
   * patterns.
   * 
   * @param name
   *        Name to check
   * @return Whether the name should be included or not
   */
  public boolean include(final String name)
  {
    boolean include = false;
    if (!patternInclude.matcher(name).matches())
    {
      LOGGER.log(Level.FINE, "Excluding " + name
                             + " since it does not match the include pattern");
    }
    else if (patternExclude.matcher(name).matches())
    {
      LOGGER.log(Level.FINE, "Excluding " + name
                             + " since it matches the exclude pattern");
    }
    else
    {
      LOGGER.log(Level.FINE, "Including " + name);
      include = true;
    }
    return include;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("InclusionRule[");
    buffer.append("patternInclude=").append(patternInclude.pattern());
    buffer.append(", patternExclude=").append(patternExclude.pattern());
    buffer.append("]");
    return buffer.toString();
  }

}
