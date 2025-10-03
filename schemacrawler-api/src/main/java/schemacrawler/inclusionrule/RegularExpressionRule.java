/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.inclusionrule;

import static java.util.regex.Pattern.DOTALL;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Serial;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import us.fatehi.utility.string.StringFormat;

/**
 * Specifies inclusion and exclusion patterns that can be applied to the names, definitions, and
 * other attributes of named objects.
 */
public final class RegularExpressionRule implements InclusionRuleWithRegularExpression {

  @Serial private static final long serialVersionUID = 3443758881974362293L;

  private static final Logger LOGGER = Logger.getLogger(RegularExpressionRule.class.getName());

  private final Pattern patternExclude;
  private final Pattern patternInclude;

  /**
   * Set include and exclude patterns.
   *
   * @param patternInclude Inclusion pattern. If null, includes everything.
   * @param patternExclude Exclusion pattern. If null, excludes nothing.
   */
  public RegularExpressionRule(final Pattern patternInclude, final Pattern patternExclude) {
    if (patternInclude == null) {
      this.patternInclude = InclusionRuleWithRegularExpression.super.getInclusionPattern();
    } else {
      this.patternInclude = patternInclude;
    }

    if (patternExclude == null) {
      this.patternExclude = InclusionRuleWithRegularExpression.super.getExclusionPattern();
    } else {
      this.patternExclude = patternExclude;
    }
  }

  /**
   * Set include and exclude patterns.
   *
   * @param patternInclude Inclusion pattern. If null, includes everything.
   * @param patternExclude Exclusion pattern. If null, excludes nothing.
   */
  public RegularExpressionRule(final String patternInclude, final String patternExclude) {
    this(
        patternInclude == null ? null : Pattern.compile(patternInclude, DOTALL),
        patternExclude == null ? null : Pattern.compile(patternExclude, DOTALL));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof InclusionRuleWithRegularExpression)) {
      return false;
    }
    final InclusionRuleWithRegularExpression other = (InclusionRuleWithRegularExpression) obj;
    return getExclusionPattern().pattern().equals(other.getExclusionPattern().pattern())
        && getInclusionPattern().pattern().equals(other.getInclusionPattern().pattern());
  }

  @Override
  public Pattern getExclusionPattern() {
    return patternExclude;
  }

  @Override
  public Pattern getInclusionPattern() {
    return patternInclude;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getExclusionPattern().pattern(), getInclusionPattern().pattern());
  }

  /** {@inheritDoc} */
  @Override
  public boolean test(final String text) {

    final Supplier<String> actionMessage;
    boolean include = false;
    if (!isBlank(text)) {
      if (!patternInclude.matcher(text).matches()) {
        actionMessage =
            new StringFormat(
                "Excluding <%s> since it does not match /%s/", text, patternInclude.pattern());
      } else if (patternExclude.matcher(text).matches()) {
        actionMessage =
            new StringFormat(
                "Excluding <%s> since it matches /%s/", text, patternExclude.pattern());
      } else {
        actionMessage =
            new StringFormat(
                "Including <%s> since it matches /%s/", text, patternInclude.pattern());
        include = true;
      }
    } else {
      actionMessage = new StringFormat("Excluding, since text is blank");
    }

    // Log caller
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, actionMessage.get());
    }

    return include;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "%s@%h {+/%s/ -/%s/}"
        .formatted(
            getClass().getSimpleName(),
            System.identityHashCode(this),
            patternInclude.pattern(),
            patternExclude.pattern());
  }
}
