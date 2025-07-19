/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.inclusionrule;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Specifies exclusion patterns that can be applied to the names, definitions, and other attributes
 * of named objects.
 */
public final class RegularExpressionExclusionRule implements InclusionRuleWithRegularExpression {

  private static final long serialVersionUID = 6274652266761961575L;

  private final InclusionRule inclusionRule;

  /**
   * Set exclude pattern. Include nothing.
   *
   * @param patternExclude Exclusion pattern. If null, excludes nothing.
   */
  public RegularExpressionExclusionRule(final Pattern patternExclude) {
    if (patternExclude == null) {
      inclusionRule = new IncludeAll();
    } else {
      inclusionRule = new RegularExpressionRule(null, patternExclude);
    }
  }

  /**
   * Set exclude pattern. Include nothing.
   *
   * @param patternExclude Exclusion pattern. If null, excludes nothing.
   */
  public RegularExpressionExclusionRule(final String patternExclude) {
    this(patternExclude == null ? null : Pattern.compile(patternExclude));
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
    if (inclusionRule instanceof InclusionRuleWithRegularExpression) {
      return ((InclusionRuleWithRegularExpression) inclusionRule).getExclusionPattern();
    } else {
      return InclusionRuleWithRegularExpression.super.getExclusionPattern();
    }
  }

  @Override
  public Pattern getInclusionPattern() {
    if (inclusionRule instanceof InclusionRuleWithRegularExpression) {
      return ((InclusionRuleWithRegularExpression) inclusionRule).getInclusionPattern();
    } else {
      return InclusionRuleWithRegularExpression.super.getInclusionPattern();
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(getExclusionPattern().pattern(), getInclusionPattern().pattern());
  }

  /** {@inheritDoc} */
  @Override
  public boolean test(final String text) {
    return inclusionRule.test(text);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return inclusionRule.toString();
  }
}
