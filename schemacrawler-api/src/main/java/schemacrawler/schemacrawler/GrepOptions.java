/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import org.jspecify.annotations.Nullable;
import schemacrawler.inclusionrule.InclusionRule;

/** SchemaCrawler options for grep-like filtering. */
public record GrepOptions(
    @Nullable InclusionRule grepTableInclusionRule,
    @Nullable InclusionRule grepColumnInclusionRule,
    @Nullable InclusionRule grepRoutineParameterInclusionRule,
    @Nullable InclusionRule grepDefinitionInclusionRule,
    boolean grepInvertMatch)
    implements Options {

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
}
