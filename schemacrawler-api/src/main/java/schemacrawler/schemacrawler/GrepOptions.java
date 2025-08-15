/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
