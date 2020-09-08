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


import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;

/**
 * SchemaCrawler options builder, to build the immutable options to crawl a
 * schema.
 */
public final class SchemaCrawlerOptionsConfig
{

  public static LimitOptionsBuilder fromConfig(final LimitOptionsBuilder providedBuilder, final Config config)
  {
    final LimitOptionsBuilder builder;
    if (providedBuilder == null)
    {
      builder = LimitOptionsBuilder.builder();
    }
    else 
    {
      builder = providedBuilder;
    }
    
    if (config == null)
    {
      return builder;
    }

    for (final DatabaseObjectRuleForInclusion ruleForInclusion : DatabaseObjectRuleForInclusion.values())
    {
      final InclusionRule inclusionRule = config.getInclusionRuleWithDefault(
        ruleForInclusion.getIncludePatternProperty(),
        ruleForInclusion.getExcludePatternProperty(),
        getDefaultInclusionRule(ruleForInclusion));

      builder.include(ruleForInclusion, inclusionRule);
    }

    return builder;
  }
  
  private static InclusionRule getDefaultInclusionRule(final DatabaseObjectRuleForInclusion ruleForInclusion)
  {
    final InclusionRule defaultInclusionRule;
    if (ruleForInclusion.isExcludeByDefault())
    {
      defaultInclusionRule = new ExcludeAll();
    }
    else
    {
      defaultInclusionRule = new IncludeAll();
    }
    return defaultInclusionRule;
  }

  public static GrepOptionsBuilder fromConfig(final GrepOptionsBuilder providedBuilder,
      final Config config)
  {

    final GrepOptionsBuilder builder;
    if (providedBuilder == null)
    {
      builder = GrepOptionsBuilder.builder();
    } else
    {
      builder = providedBuilder;
    }

    if (config == null)
    {
      return builder;
    }

    final String SC_GREP_COLUMN_PATTERN_EXCLUDE =
        "schemacrawler.grep.column.pattern.exclude";
    final String SC_GREP_COLUMN_PATTERN_INCLUDE =
        "schemacrawler.grep.column.pattern.include";
    final String SC_GREP_DEFINITION_PATTERN_EXCLUDE =
        "schemacrawler.grep.definition.pattern.exclude";
    final String SC_GREP_DEFINITION_PATTERN_INCLUDE =
        "schemacrawler.grep.definition.pattern.include";
    final String SC_GREP_ROUTINE_PARAMETER_PATTERN_EXCLUDE =
        "schemacrawler.grep.routine.inout.pattern.exclude";
    final String SC_GREP_ROUTINE_PARAMETER_PATTERN_INCLUDE =
        "schemacrawler.grep.routine.inout.pattern.include";

    builder.includeGreppedColumns(
        config.getOptionalInclusionRule(SC_GREP_COLUMN_PATTERN_INCLUDE,
            SC_GREP_COLUMN_PATTERN_EXCLUDE).orElse(null));
    builder.includeGreppedRoutineParameters(config
        .getOptionalInclusionRule(SC_GREP_ROUTINE_PARAMETER_PATTERN_INCLUDE,
            SC_GREP_ROUTINE_PARAMETER_PATTERN_EXCLUDE)
        .orElse(null));
    builder.includeGreppedDefinitions(
        config.getOptionalInclusionRule(SC_GREP_DEFINITION_PATTERN_INCLUDE,
            SC_GREP_DEFINITION_PATTERN_EXCLUDE).orElse(null));

    return builder;
  }
  
}
