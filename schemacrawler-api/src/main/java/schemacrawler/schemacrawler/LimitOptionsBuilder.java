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


import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForColumnInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineParameterInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSequenceInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSynonymInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForTableInclusion;
import static sf.util.Utility.enumValue;
import static sf.util.Utility.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.RoutineType;

/**
 * SchemaCrawler options builder, to build the immutable options to crawl a
 * schema.
 */
public final class LimitOptionsBuilder
  implements OptionsBuilder<LimitOptionsBuilder, LimitOptions>
{

  private static Collection<RoutineType> allRoutineTypes()
  {
    return EnumSet.of(RoutineType.procedure, RoutineType.function);
  }

  public static LimitOptionsBuilder builder()
  {
    return new LimitOptionsBuilder();
  }

  private static Collection<String> defaultTableTypes()
  {
    return Arrays.asList("BASE TABLE", "TABLE", "VIEW");
  }

  public static LimitOptions newLimitOptions()
  {
    return builder().toOptions();
  }

  private final Map<DatabaseObjectRuleForInclusion, InclusionRule>
    inclusionRules;
  private String tableNamePattern;
  private Optional<Collection<String>> tableTypes;
  private Optional<Collection<RoutineType>> routineTypes;

  /**
   * Default options.
   */
  private LimitOptionsBuilder()
  {
    inclusionRules = new EnumMap<>(DatabaseObjectRuleForInclusion.class);

    for (DatabaseObjectRuleForInclusion ruleForInclusion : DatabaseObjectRuleForInclusion.values())
    {
      resetToDefault(ruleForInclusion);
    }

    tableTypes = Optional.of(defaultTableTypes());
    routineTypes = Optional.of(allRoutineTypes());

  }

  /**
   * Options from properties.
   *
   * @param config
   *   Configuration properties
   */
  @Override
  public LimitOptionsBuilder fromConfig(final Config config)
  {
    if (config == null)
    {
      return this;
    }

    for (DatabaseObjectRuleForInclusion ruleForInclusion : DatabaseObjectRuleForInclusion.values())
    {
      final InclusionRule inclusionRule = config.getInclusionRuleWithDefault(
        ruleForInclusion.getIncludePatternProperty(),
        ruleForInclusion.getExcludePatternProperty(),
        getDefaultInclusionRule(ruleForInclusion));

      inclusionRules.put(ruleForInclusion, inclusionRule);
    }

    return this;
  }

  @Override
  public LimitOptionsBuilder fromOptions(final LimitOptions options)
  {
    if (options == null)
    {
      return this;
    }

    for (DatabaseObjectRuleForInclusion ruleForInclusion : DatabaseObjectRuleForInclusion.values())
    {
      inclusionRules.put(ruleForInclusion, options.get(ruleForInclusion));
    }

    tableTypes = Optional.ofNullable(options.getTableTypes());
    tableNamePattern = options.getTableNamePattern();
    routineTypes = Optional.ofNullable(options.getRoutineTypes());

    return this;
  }

  @Override
  public Config toConfig()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public LimitOptions toOptions()
  {
    return new LimitOptions(new EnumMap<>(inclusionRules),
                            tableTypes
                              .map(types -> new ArrayList<>(types))
                              .orElse(null),
                            tableNamePattern,
                            routineTypes
                              .map(types -> EnumSet.copyOf(types))
                              .orElse(null));
  }

  public LimitOptionsBuilder includeAllRoutines()
  {
    includeRoutines(new IncludeAll());
    return this;
  }

  public LimitOptionsBuilder includeAllSequences()
  {
    includeSequences(new IncludeAll());
    return this;
  }

  public LimitOptionsBuilder includeAllSynonyms()
  {
    includeSynonyms(new IncludeAll());
    return this;
  }

  public LimitOptionsBuilder includeColumns(final InclusionRule columnInclusionRule)
  {
    return include(ruleForColumnInclusion, columnInclusionRule);
  }

  public LimitOptionsBuilder includeColumns(final Pattern columnPattern)
  {
    return include(ruleForColumnInclusion, columnPattern);
  }

  public LimitOptionsBuilder includeSchemas(final Pattern schemaPattern)
  {
    return include(ruleForSchemaInclusion, schemaPattern);
  }

  public LimitOptionsBuilder includeTables(final Pattern tablePattern)
  {
    return include(ruleForTableInclusion, tablePattern);
  }

  public LimitOptionsBuilder includeRoutines(final Pattern routinePattern)
  {
    return include(ruleForRoutineInclusion, routinePattern);
  }

  public LimitOptionsBuilder includeSequences(final Pattern sequencePattern)
  {
    return include(ruleForSequenceInclusion, sequencePattern);
  }

  public LimitOptionsBuilder includeSynonyms(final Pattern synonymPattern)
  {
    return include(ruleForSynonymInclusion, synonymPattern);
  }

  public LimitOptionsBuilder includeRoutineParameters(final InclusionRule routineParameterInclusionRule)
  {
    return include(ruleForRoutineParameterInclusion,
                   routineParameterInclusionRule);
  }

  public LimitOptionsBuilder includeRoutines(final InclusionRule routineInclusionRule)
  {
    return include(ruleForRoutineInclusion, routineInclusionRule);
  }

  public LimitOptionsBuilder includeSchemas(final InclusionRule schemaInclusionRule)
  {
    return include(ruleForSchemaInclusion, schemaInclusionRule);
  }

  public LimitOptionsBuilder includeSequences(final InclusionRule sequenceInclusionRule)
  {
    return include(ruleForSequenceInclusion, sequenceInclusionRule);
  }

  public LimitOptionsBuilder includeSynonyms(final InclusionRule synonymInclusionRule)
  {
    return include(ruleForSynonymInclusion, synonymInclusionRule);
  }

  public LimitOptionsBuilder includeTables(final InclusionRule tableInclusionRule)
  {
    return include(ruleForTableInclusion, tableInclusionRule);
  }

  public LimitOptionsBuilder routineTypes(final Collection<RoutineType> routineTypes)
  {
    if (routineTypes == null)
    {
      // null signifies include all routine types
      this.routineTypes = Optional.empty();
    }
    else if (routineTypes.isEmpty())
    {
      this.routineTypes = Optional.of(Collections.emptySet());
    }
    else
    {
      this.routineTypes = Optional.of(new HashSet<>(routineTypes));
    }
    return this;
  }

  /**
   * Sets routine types from a comma-separated list of routine types.
   *
   * @param routineTypesString
   *   Comma-separated list of routine types.
   */
  public LimitOptionsBuilder routineTypes(final String routineTypesString)
  {
    if (routineTypesString != null)
    {
      final Collection<RoutineType> routineTypes = new HashSet<>();
      final String[] routineTypeStrings = routineTypesString.split(",");
      if (routineTypeStrings != null && routineTypeStrings.length > 0)
      {
        for (final String routineTypeString : routineTypeStrings)
        {
          final RoutineType routineType =
            enumValue(routineTypeString.toLowerCase(Locale.ENGLISH),
                      RoutineType.unknown);
          routineTypes.add(routineType);
        }
      }
      this.routineTypes = Optional.of(routineTypes);
    }
    else
    {
      routineTypes = Optional.empty();
    }
    return this;
  }

  public LimitOptionsBuilder tableNamePattern(final String tableNamePattern)
  {
    if (isBlank(tableNamePattern))
    {
      this.tableNamePattern = null;
    }
    else
    {
      this.tableNamePattern = tableNamePattern;
    }
    return this;
  }

  public LimitOptionsBuilder tableTypes(final Collection<String> tableTypes)
  {
    if (tableTypes == null)
    {
      this.tableTypes = Optional.empty();
    }
    else if (tableTypes.isEmpty())
    {
      this.tableTypes = Optional.of(Collections.emptySet());
    }
    else
    {
      this.tableTypes = Optional.of(new HashSet<>(tableTypes));
    }
    return this;
  }

  /**
   * Sets table types requested for output from a comma-separated list of table
   * types. For example: TABLE,VIEW,SYSTEM_TABLE,GLOBAL TEMPORARY,ALIAS,SYNONYM
   *
   * @param tableTypesString
   *   Comma-separated list of table types. Can be null if all supported table
   *   types are requested.
   */
  public LimitOptionsBuilder tableTypes(final String tableTypesString)
  {
    if (tableTypesString != null)
    {
      final Collection<String> tableTypes;
      tableTypes = new HashSet<>();
      final String[] tableTypeStrings = tableTypesString.split(",");
      if (tableTypeStrings != null && tableTypeStrings.length > 0)
      {
        for (final String tableTypeString : tableTypeStrings)
        {
          tableTypes.add(tableTypeString.trim());
        }
      }
      this.tableTypes = Optional.of(tableTypes);
    }
    else
    {
      tableTypes = Optional.empty();
    }

    return this;
  }

  private InclusionRule getDefaultInclusionRule(final DatabaseObjectRuleForInclusion ruleForInclusion)
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

  private void resetToDefault(final DatabaseObjectRuleForInclusion ruleForInclusion)
  {
    inclusionRules.put(ruleForInclusion,
                       getDefaultInclusionRule(ruleForInclusion));
  }

  private LimitOptionsBuilder include(final DatabaseObjectRuleForInclusion ruleForInclusion,
                                      final Pattern pattern)
  {
    return include(ruleForInclusion,
                   new RegularExpressionInclusionRule(pattern));
  }

  private LimitOptionsBuilder include(final DatabaseObjectRuleForInclusion ruleForInclusion,
                                      final InclusionRule inclusionRule)
  {
    if (inclusionRule == null)
    {
      resetToDefault(ruleForInclusion);
    }
    else
    {
      inclusionRules.put(ruleForInclusion, inclusionRule);
    }
    return this;
  }

}
