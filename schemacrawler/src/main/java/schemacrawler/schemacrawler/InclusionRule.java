/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.schemacrawler;


import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Specifies inclusion and exclusion patterns that can be applied to the
 * names of database objects.
 * 
 * @author Sualeh Fatehi
 */
public final class InclusionRule
  implements Serializable
{

  /** Exclude nothing */
  public static final String EXCLUDE_NONE = "";
  /** Include everything. */
  public static final String INCLUDE_ALL = ".*";

  /** Exclude nothing */
  public static final Pattern EXCLUDE_NONE_PATTERN = Pattern
    .compile(EXCLUDE_NONE);
  /** Include everything. */
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
   * Checks whether to add a named object after considering the include
   * and exclude patterns.
   * 
   * @param name
   *        Name to check
   * @return Whether the name should be included or not
   */
  public boolean include(final String name)
  {
    boolean include = false;
    if (!(name == null || name.trim().length() == 0))
    {
      if (!patternInclude.matcher(name).matches())
      {
        LOGGER
          .log(Level.FINE, "Excluding " + name
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
    final StringBuffer buffer = new StringBuffer();
    buffer.append("InclusionRule[");
    buffer.append("patternInclude=").append(patternInclude.pattern());
    buffer.append(", patternExclude=").append(patternExclude.pattern());
    buffer.append("]");
    return buffer.toString();
  }

}
