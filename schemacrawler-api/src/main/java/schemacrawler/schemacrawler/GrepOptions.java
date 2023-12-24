/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.Optional;

import schemacrawler.inclusionrule.InclusionRule;
import us.fatehi.utility.ObjectToString;

/** SchemaCrawler options. */
public final class GrepOptions implements Options {

  private final InclusionRule grepTableInclusionRule;
  private final InclusionRule grepColumnInclusionRule;
  private final InclusionRule grepDefinitionInclusionRule;
  private final boolean grepInvertMatch;
  private final InclusionRule grepRoutineParameterInclusionRule;

  GrepOptions(
      final InclusionRule grepTableInclusionRule,
      final InclusionRule grepColumnInclusionRule,
      final InclusionRule grepRoutineParameterInclusionRule,
      final InclusionRule grepDefinitionInclusionRule,
      final boolean grepInvertMatch) {
    this.grepTableInclusionRule = grepTableInclusionRule;
    this.grepColumnInclusionRule = grepColumnInclusionRule;
    this.grepRoutineParameterInclusionRule = grepRoutineParameterInclusionRule;
    this.grepDefinitionInclusionRule = grepDefinitionInclusionRule;
    this.grepInvertMatch = grepInvertMatch;
  }

  /**
   * Gets the column inclusion rule for grep.
   *
   * @return Column inclusion rule for grep.
   */
  public Optional<InclusionRule> getGrepColumnInclusionRule() {
    return Optional.ofNullable(grepColumnInclusionRule);
  }

  /**
   * Gets the definitions inclusion rule for grep.
   *
   * @return Definitions inclusion rule for grep.
   */
  public Optional<InclusionRule> getGrepDefinitionInclusionRule() {
    return Optional.ofNullable(grepDefinitionInclusionRule);
  }

  /**
   * Gets the routine column rule for grep.
   *
   * @return Routine column rule for grep.
   */
  public Optional<InclusionRule> getGrepRoutineParameterInclusionRule() {
    return Optional.ofNullable(grepRoutineParameterInclusionRule);
  }

  /**
   * Gets the table inclusion rule for grep.
   *
   * @return Table inclusion rule for grep.
   */
  public Optional<InclusionRule> getGrepTableInclusionRule() {
    return Optional.ofNullable(grepTableInclusionRule);
  }

  public boolean isGrepColumns() {
    return grepColumnInclusionRule != null;
  }

  public boolean isGrepDefinitions() {
    return grepDefinitionInclusionRule != null;
  }

  /**
   * Whether to invert matches.
   *
   * @return Whether to invert matches.
   */
  public boolean isGrepInvertMatch() {
    return grepInvertMatch;
  }

  public boolean isGrepRoutineParameters() {
    return grepRoutineParameterInclusionRule != null;
  }

  public boolean isGrepTables() {
    return grepTableInclusionRule != null;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
