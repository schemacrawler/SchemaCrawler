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

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.ListExclusionRule;

public class ListExclusionRuleTest {

  final InclusionRule exclusionRule =
      new ListExclusionRule(Arrays.asList("ORDDATA", "\"SYSTEM\"", "APEX_123456", "FLOWS_12345"));

  @Test
  public void closeEnoughs() {

    final String[] closeEnoughs = new String[] {"ORDDAT", "SYSTEM", "APEX_12345", "FLOWS_1234567"};
    for (final String closeEnough : closeEnoughs) {
      assertThat(
          String.format(
              "<%s> - exclude close enough strings - inclusion rule should evaluate to true",
              closeEnough),
          exclusionRule.test(closeEnough),
          is(true));
    }
  }

  @Test
  public void empties() {

    final String[] empties = new String[] {null, "", "\t", "  "};
    for (final String empty : empties) {
      assertThat(
          String.format("<%s> - exclude empties - inclusion rule should evaluate to false", empty),
          exclusionRule.test(empty),
          is(false));
    }
  }

  @Test
  public void valid() {

    final String[] valids = new String[] {"ORDDATA", "\"SYSTEM\"", "APEX_123456", "FLOWS_12345"};
    for (final String valid : valids) {
      assertThat(
          String.format(
              "<%s> - include valid schemas - inclusion rule should evaluate to false", valid),
          exclusionRule.test(valid),
          is(false));
    }
  }
}
