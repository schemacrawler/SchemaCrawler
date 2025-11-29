/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import schemacrawler.server.oracle.OracleSchemaExclusionRule;

public class OracleSchemaExclusionRuleTest {

  @Test
  public void closeEnoughs() {
    final OracleSchemaExclusionRule exclusionRule = new OracleSchemaExclusionRule();

    final String[] closeEnoughs = {"ORDDAT", "SYSTEM", "APEX_12345", "FLOWS_1234567"};
    for (final String closeEnough : closeEnoughs) {
      assertThat(
          "<%s> - exclude close enough strings - inclusion rule should evaluate to true"
              .formatted(closeEnough),
          exclusionRule.test(closeEnough),
          is(true));
    }
  }

  @Test
  public void empties() {
    final OracleSchemaExclusionRule exclusionRule = new OracleSchemaExclusionRule();

    final String[] empties = {null, "", "\t", "  "};
    for (final String empty : empties) {
      assertThat(
          "<%s> - exclude empties - inclusion rule should evaluate to false".formatted(empty),
          exclusionRule.test(empty),
          is(false));
    }
  }

  @Test
  public void valid() {
    final OracleSchemaExclusionRule exclusionRule = new OracleSchemaExclusionRule();

    final String[] valids = {"ORDDATA", "\"SYSTEM\"", "APEX_123456", "FLOWS_12345"};
    for (final String valid : valids) {
      assertThat(
          "<%s> - include valid schemas - inclusion rule should evaluate to false".formatted(valid),
          exclusionRule.test(valid),
          is(false));
    }
  }
}
