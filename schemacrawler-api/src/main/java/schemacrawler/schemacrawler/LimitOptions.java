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


import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.RoutineType;
import sf.util.ObjectToString;

/**
 * SchemaCrawler options.
 *
 * @author Sualeh Fatehi
 */
public final class LimitOptions
  implements Options
{

  private final Map<DatabaseObjectRuleForInclusion, InclusionRule>
    inclusionRules;
  private final Collection<RoutineType> routineTypes;
  private final String tableNamePattern;
  private final Collection<String> tableTypes;

  LimitOptions(final Map<DatabaseObjectRuleForInclusion, InclusionRule> inclusionRules,
               final Collection<String> tableTypes,
               final String tableNamePattern,
               final Collection<RoutineType> routineTypes)
  {
    // NOTE: No defensive copies of collections are made since this is a protected method
    // only called from the builder
    // Table types and routines types may be null, indicating that all table types or
    // routine types should be considered

    this.inclusionRules =
      requireNonNull(inclusionRules, "No inclusion rules provided");

    this.tableTypes = tableTypes;
    this.tableNamePattern = tableNamePattern;

    this.routineTypes = routineTypes;
  }

  public Collection<RoutineType> getRoutineTypes()
  {
    if (routineTypes == null)
    {
      return null;
    }
    else
    {
      return new HashSet<>(routineTypes);
    }
  }

  /**
   * Gets the table name pattern. A null value indicates do not take table
   * pattern into account.
   *
   * @return Table name pattern
   */
  public String getTableNamePattern()
  {
    return tableNamePattern;
  }

  /**
   * Returns the table types requested for output. This can be null, if all
   * supported table types are required in the output.
   *
   * @return All table types requested for output
   */
  public Collection<String> getTableTypes()
  {
    if (tableTypes == null)
    {
      return null;
    }
    else
    {
      return new HashSet<>(tableTypes);
    }
  }

  /**
   * Gets the inclusion rule.
   *
   * @return Inclusion rule.
   */
  public InclusionRule get(final DatabaseObjectRuleForInclusion inclusionRuleKey)
  {
    final InclusionRule defaultInclusionRule;
    if (inclusionRuleKey.isExcludeByDefault())
    {
      defaultInclusionRule = new ExcludeAll();
    }
    else
    {
      defaultInclusionRule = new IncludeAll();
    }
    return inclusionRules.getOrDefault(inclusionRuleKey, defaultInclusionRule);
  }

  public boolean isIncludeAll(final DatabaseObjectRuleForInclusion inclusionRuleKey)
  {
    return get(inclusionRuleKey).equals(new IncludeAll());
  }

  public boolean isExcludeAll(final DatabaseObjectRuleForInclusion inclusionRuleKey)
  {
    return get(inclusionRuleKey).equals(new ExcludeAll());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

}
