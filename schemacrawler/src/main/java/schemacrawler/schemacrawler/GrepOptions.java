/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import java.util.Properties;

/**
 * SchemaCrawler grep options.
 * 
 * @author Sualeh Fatehi
 */
public final class GrepOptions
  implements Options
{

  private static final long serialVersionUID = -3557794862382066029L;

  private static final String SC_GREP_COLUMN_PATTERN_EXCLUDE = "schemacrawler.grep.column.pattern.exclude";
  private static final String SC_GREP_COLUMN_PATTERN_INCLUDE = "schemacrawler.grep.column.pattern.include";
  private static final String SC_GREP_PROCEDURE_COLUMN_PATTERN_EXCLUDE = "schemacrawler.grep.procedure.column.pattern.exclude";
  private static final String SC_GREP_PROCEDURE_COLUMN_PATTERN_INCLUDE = "schemacrawler.grep.procedure.column.pattern.include";

  private static final String SC_GREP_INVERT_MATCH = "schemacrawler.grep.invert-match";

  private InclusionRule grepColumnInclusionRule;
  private InclusionRule grepProcedureColumnInclusionRule;
  private boolean grepInvertMatch;

  /**
   * Default options.
   */
  public GrepOptions()
  {
    this(new Config());
  }

  /**
   * Options from properties.
   * 
   * @param config
   *        Configuration properties
   */
  public GrepOptions(final Config config)
  {
    final Config configProperties;
    if (config == null)
    {
      configProperties = new Config();
    }
    else
    {
      configProperties = config;
    }

    grepColumnInclusionRule = new InclusionRule(configProperties
                                                  .getStringValue(SC_GREP_COLUMN_PATTERN_INCLUDE,
                                                                  InclusionRule.ALL),
                                                configProperties
                                                  .getStringValue(SC_GREP_COLUMN_PATTERN_EXCLUDE,
                                                                  InclusionRule.NONE));
    grepProcedureColumnInclusionRule = new InclusionRule(configProperties
      .getStringValue(SC_GREP_PROCEDURE_COLUMN_PATTERN_INCLUDE,
                      InclusionRule.ALL), configProperties
      .getStringValue(SC_GREP_PROCEDURE_COLUMN_PATTERN_EXCLUDE,
                      InclusionRule.NONE));
    grepInvertMatch = configProperties.getBooleanValue(SC_GREP_INVERT_MATCH);

  }

  /**
   * Options from properties.
   * 
   * @param properties
   *        Configuration properties
   */
  public GrepOptions(final Properties properties)
  {
    this(new Config(properties));
  }

  /**
   * Gets the column inclusion rule for grep.
   * 
   * @return Column inclusion rule for grep.
   */
  public InclusionRule getGrepColumnInclusionRule()
  {
    return grepColumnInclusionRule;
  }

  /**
   * Gets the procedure column rule for grep.
   * 
   * @return Procedure column rule for grep.
   */
  public InclusionRule getGrepProcedureColumnInclusionRule()
  {
    return grepProcedureColumnInclusionRule;
  }

  /**
   * Whether to invert matches.
   * 
   * @return Whether to invert matches.
   */
  public boolean isGrepInvertMatch()
  {
    return grepInvertMatch;
  }

  /**
   * Sets the column inclusion rule for grep.
   * 
   * @param columnInclusionRule
   *        Column inclusion rule for grep
   */
  public void setGrepColumnInclusionRule(final InclusionRule grepColumnInclusionRule)
  {
    if (grepColumnInclusionRule == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.grepColumnInclusionRule = grepColumnInclusionRule;
  }

  /**
   * Set whether to invert matches.
   * 
   * @param invertMatch
   *        Whether to invert matches.
   */
  public void setGrepInvertMatch(boolean grepInvertMatch)
  {
    this.grepInvertMatch = grepInvertMatch;
  }

  /**
   * Sets the procedure column inclusion rule for grep.
   * 
   * @param procedureColumnInclusionRule
   *        Procedure column inclusion rule for grep
   */
  public void setGrepProcedureColumnInclusionRule(final InclusionRule grepProcedureColumnInclusionRule)
  {
    if (grepProcedureColumnInclusionRule == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.grepProcedureColumnInclusionRule = grepProcedureColumnInclusionRule;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuilder buffer = new StringBuilder();
    buffer.append("GrepOptions[");
    buffer.append("grepColumnInclusionRule=").append(grepColumnInclusionRule);
    buffer.append(", grepProcedureColumnInclusionRule=")
      .append(grepProcedureColumnInclusionRule);
    buffer.append(", grepInvertMatch=").append(grepInvertMatch);
    buffer.append("]");
    return buffer.toString();
  }

}
