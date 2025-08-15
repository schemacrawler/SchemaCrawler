/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import java.util.Optional;
import java.util.regex.Pattern;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;

/** grep options builder, to build the immutable options to crawl a schema. */
public final class GrepOptionsBuilder implements OptionsBuilder<GrepOptionsBuilder, GrepOptions> {

  public static GrepOptionsBuilder builder() {
    return new GrepOptionsBuilder();
  }

  public static GrepOptions newGrepOptions() {
    return builder().toOptions();
  }

  private Optional<InclusionRule> grepTableInclusionRule;
  private Optional<InclusionRule> grepColumnInclusionRule;
  private Optional<InclusionRule> grepDefinitionInclusionRule;
  private boolean grepInvertMatch;
  private Optional<InclusionRule> grepRoutineParameterInclusionRule;

  /** Default options. */
  private GrepOptionsBuilder() {
    grepTableInclusionRule = Optional.empty();
    grepColumnInclusionRule = Optional.empty();
    grepRoutineParameterInclusionRule = Optional.empty();
    grepDefinitionInclusionRule = Optional.empty();
  }

  @Override
  public GrepOptionsBuilder fromOptions(final GrepOptions options) {
    if (options == null) {
      return this;
    }

    grepTableInclusionRule = options.getGrepTableInclusionRule();
    grepColumnInclusionRule = options.getGrepColumnInclusionRule();
    grepRoutineParameterInclusionRule =
        Optional.ofNullable(options.getGrepRoutineParameterInclusionRule()).orElse(null);
    grepDefinitionInclusionRule =
        Optional.ofNullable(options.getGrepDefinitionInclusionRule()).orElse(null);
    grepInvertMatch = options.isGrepInvertMatch();

    return this;
  }

  public GrepOptionsBuilder includeGreppedColumns(final InclusionRule grepColumnInclusionRule) {
    this.grepColumnInclusionRule = Optional.ofNullable(grepColumnInclusionRule);
    return this;
  }

  public GrepOptionsBuilder includeGreppedColumns(final Pattern grepColumnPattern) {
    if (grepColumnPattern == null) {
      grepColumnInclusionRule = Optional.empty();
    } else {
      grepColumnInclusionRule = Optional.of(new RegularExpressionInclusionRule(grepColumnPattern));
    }
    return this;
  }

  public GrepOptionsBuilder includeGreppedDefinitions(
      final InclusionRule grepDefinitionInclusionRule) {
    this.grepDefinitionInclusionRule = Optional.ofNullable(grepDefinitionInclusionRule);
    return this;
  }

  public GrepOptionsBuilder includeGreppedDefinitions(final Pattern grepDefinitionPattern) {
    if (grepDefinitionPattern == null) {
      grepDefinitionInclusionRule = Optional.empty();
    } else {
      grepDefinitionInclusionRule =
          Optional.of(new RegularExpressionInclusionRule(grepDefinitionPattern));
    }
    return this;
  }

  public GrepOptionsBuilder includeGreppedRoutineParameters(
      final InclusionRule grepRoutineParameterInclusionRule) {
    this.grepRoutineParameterInclusionRule = Optional.ofNullable(grepRoutineParameterInclusionRule);
    return this;
  }

  public GrepOptionsBuilder includeGreppedRoutineParameters(
      final Pattern grepRoutineParametersPattern) {
    if (grepRoutineParametersPattern == null) {
      grepRoutineParameterInclusionRule = Optional.empty();
    } else {
      grepRoutineParameterInclusionRule =
          Optional.of(new RegularExpressionInclusionRule(grepRoutineParametersPattern));
    }
    return this;
  }

  public GrepOptionsBuilder includeGreppedTables(final InclusionRule grepTableInclusionRule) {
    this.grepTableInclusionRule = Optional.ofNullable(grepTableInclusionRule);
    return this;
  }

  public GrepOptionsBuilder includeGreppedTables(final Pattern grepTablePattern) {
    if (grepTablePattern == null) {
      grepTableInclusionRule = Optional.empty();
    } else {
      grepTableInclusionRule = Optional.of(new RegularExpressionInclusionRule(grepTablePattern));
    }
    return this;
  }

  public GrepOptionsBuilder invertGrepMatch(final boolean grepInvertMatch) {
    this.grepInvertMatch = grepInvertMatch;
    return this;
  }

  @Override
  public GrepOptions toOptions() {
    final GrepOptions grepOptions =
        new GrepOptions(
            grepTableInclusionRule.orElse(null),
            grepColumnInclusionRule.orElse(null),
            grepRoutineParameterInclusionRule.orElse(null),
            grepDefinitionInclusionRule.orElse(null),
            grepInvertMatch);

    return grepOptions;
  }
}
