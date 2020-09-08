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


import static schemacrawler.schema.RoutineType.function;
import static schemacrawler.schema.RoutineType.procedure;
import static schemacrawler.schema.RoutineType.unknown;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForColumnInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineParameterInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSequenceInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSynonymInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForTableInclusion;
import static schemacrawler.utility.EnumUtility.enumValue;
import static us.fatehi.utility.Utility.isBlank;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.TableTypes;

/**
 * SchemaCrawler options builder, to build the immutable options to crawl a
 * schema.
 */
public final class SchemaCrawlerOptionsConfig
{

  /**
   * Options from properties.
   *
   * @param config
   *   Configuration properties
   */
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
        builder.get(ruleForInclusion));

      builder.include(ruleForInclusion, inclusionRule);
    }

    return builder;
  }

}
