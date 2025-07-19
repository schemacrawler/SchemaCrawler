/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
