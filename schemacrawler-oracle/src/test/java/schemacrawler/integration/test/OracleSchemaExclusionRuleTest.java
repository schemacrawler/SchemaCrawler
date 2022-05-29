/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.integration.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import schemacrawler.server.oracle.OracleSchemaExclusionRule;

public class OracleSchemaExclusionRuleTest {

  @Test
  public void close() {
    final OracleSchemaExclusionRule exclusionRule = new OracleSchemaExclusionRule();

    final String[] closeEnoughs = new String[] {"ORDDAT", "SYSTEM", "APEX_12345", "FLOWS_1234567"};
    for (final String closeEnough : closeEnoughs) {
      assertThat(
          "Exclude close enough strings - exclusion rule should evaluate to true",
          exclusionRule.test(closeEnough),
          is(true));
    }
  }

  @Test
  public void empties() {
    final OracleSchemaExclusionRule exclusionRule = new OracleSchemaExclusionRule();

    final String[] empties = new String[] {null, "", "\t", "  "};
    for (final String empty : empties) {
      assertThat(
          "Exclude empties - exclusion rule should evaluate to true",
          exclusionRule.test(empty),
          is(true));
    }
  }

  @Test
  public void valid() {
    final OracleSchemaExclusionRule exclusionRule = new OracleSchemaExclusionRule();

    final String[] valids = new String[] {"ORDDATA", "\"SYSTEM\"", "APEX_123456", "FLOWS_12345"};
    for (final String valid : valids) {
      assertThat(
          "Include valid schemas - exclusion rule should evaluate to false",
          exclusionRule.test(valid),
          is(false));
    }
  }
}
