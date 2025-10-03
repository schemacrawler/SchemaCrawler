/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.inclusionrule;

import java.io.Serial;
import java.util.Objects;
import java.util.regex.Pattern;

/** Include all names, definitions, and other attributes of named objects. */
public final class ExcludeAll implements InclusionRuleWithRegularExpression {

  @Serial private static final long serialVersionUID = -2992724018349021861L;

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
    return InclusionRuleWithRegularExpression.super.getInclusionPattern();
  }

  @Override
  public Pattern getInclusionPattern() {
    return InclusionRuleWithRegularExpression.super.getExclusionPattern();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getExclusionPattern().pattern(), getInclusionPattern().pattern());
  }

  @Override
  public boolean test(final String text) {
    return false;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
