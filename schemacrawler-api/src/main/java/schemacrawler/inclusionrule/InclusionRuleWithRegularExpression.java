/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.inclusionrule;

import java.util.regex.Pattern;

public interface InclusionRuleWithRegularExpression extends InclusionRule {

  /**
   * Returns the regular expression for the exclusion rule. Not all inclusion rules are based on
   * regular expressions, so this method indicates that no strings should be considered for
   * exclusion by default.
   *
   * @return Regular expression for the exclusion rule
   */
  default Pattern getExclusionPattern() {
    return Pattern.compile("");
  }

  /**
   * Returns the regular expression for the inclusion rule. Not all inclusion rules are based on
   * regular expressions, so this method indicates that all strings should be considered for
   * inclusion by default.
   *
   * @return Regular expression for the inclusion rule
   */
  default Pattern getInclusionPattern() {
    return Pattern.compile(".*");
  }
}
