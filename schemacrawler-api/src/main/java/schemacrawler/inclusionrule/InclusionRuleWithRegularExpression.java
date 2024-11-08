/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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
