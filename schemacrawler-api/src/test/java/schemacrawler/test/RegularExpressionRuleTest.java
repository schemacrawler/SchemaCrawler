/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.RegularExpressionRule;

public class RegularExpressionRuleTest {

  @Test
  public void checkHashCode() {
    assertThat(
        new RegularExpressionRule((String) null, (String) null).hashCode(),
        is(new RegularExpressionRule((String) null, (String) null).hashCode()));

    assertThat(
        new RegularExpressionRule((String) null, ".*").hashCode(),
        is(new RegularExpressionRule((String) null, ".*").hashCode()));

    assertThat(
        new RegularExpressionRule(".*", (String) null).hashCode(),
        is(new RegularExpressionRule(".*", (String) null).hashCode()));

    final RegularExpressionRule regExpRule = new RegularExpressionRule(".*", "exc");
    assertThat(regExpRule.hashCode(), is(regExpRule.hashCode()));

    assertThat(
        Objects.equals(regExpRule.hashCode(), new RegularExpressionRule(".*", "exc1").hashCode()),
        is(false));
  }

  @Test
  public void equals() {
    assertThat(
        new RegularExpressionRule((String) null, (String) null),
        is(new RegularExpressionRule((String) null, (String) null)));

    assertThat(
        new RegularExpressionRule((String) null, ".*"),
        is(new RegularExpressionRule((String) null, ".*")));

    assertThat(
        new RegularExpressionRule(".*", (String) null),
        is(new RegularExpressionRule(".*", (String) null)));

    final RegularExpressionRule regExpRule = new RegularExpressionRule(".*", "exc");
    assertThat(regExpRule, is(regExpRule));
    assertThat(regExpRule, is(new RegularExpressionRule(".*", "exc")));

    assertThat(regExpRule.equals(new RegularExpressionRule(".*", "exc1")), is(false));
    assertThat(regExpRule.equals(new RegularExpressionRule("inc", "exc")), is(false));

    assertThat(new RegularExpressionRule(".*", null).equals(null), is(false));
  }

  @Test
  public void test() {
    final RegularExpressionRule rule1 = new RegularExpressionRule((String) null, (String) null);
    assertThat(rule1.getInclusionPattern().pattern(), is(".*"));
    assertThat(rule1.getExclusionPattern().pattern(), is(""));
    assertThat(rule1.toString(), endsWith("{+/.*/ -//}"));
    assertThat(rule1.test(null), is(false));
    assertThat(rule1.test(""), is(false));
    assertThat(rule1.test("inc"), is(true));
    assertThat(rule1.test("exc"), is(true));
    assertThat(rule1.test("abc"), is(true));

    final RegularExpressionRule rule2 = new RegularExpressionRule((String) null, "exc");
    assertThat(rule2.getInclusionPattern().pattern(), is(".*"));
    assertThat(rule2.getExclusionPattern().pattern(), is("exc"));
    assertThat(rule2.toString(), endsWith("{+/.*/ -/exc/}"));
    assertThat(rule2.test(null), is(false));
    assertThat(rule2.test(""), is(false));
    assertThat(rule2.test("inc"), is(true));
    assertThat(rule2.test("exc"), is(false));
    assertThat(rule2.test("abc"), is(true));

    final RegularExpressionRule rule3 = new RegularExpressionRule("inc", (String) null);
    assertThat(rule3.getInclusionPattern().pattern(), is("inc"));
    assertThat(rule3.getExclusionPattern().pattern(), is(""));
    assertThat(rule3.toString(), endsWith("{+/inc/ -//}"));
    assertThat(rule3.test(null), is(false));
    assertThat(rule3.test(""), is(false));
    assertThat(rule3.test("inc"), is(true));
    assertThat(rule3.test("exc"), is(false));
    assertThat(rule3.test("abc"), is(false));

    final RegularExpressionRule rule4 = new RegularExpressionRule("inc", "exc");
    assertThat(rule4.getInclusionPattern().pattern(), is("inc"));
    assertThat(rule4.getExclusionPattern().pattern(), is("exc"));
    assertThat(rule4.toString(), endsWith("{+/inc/ -/exc/}"));
    assertThat(rule4.test(null), is(false));
    assertThat(rule4.test(""), is(false));
    assertThat(rule4.test("inc"), is(true));
    assertThat(rule4.test("exc"), is(false));
    assertThat(rule4.test("abc"), is(false));
  }

  @Test
  public void testMultiline() {
    final String multilineText = "line 1: hello world\nline 2: inc\nline 3: exc";

    final RegularExpressionRule rule1 = new RegularExpressionRule(".*inc.*", (String) null);
    assertThat(rule1.getInclusionPattern().pattern(), is(".*inc.*"));
    assertThat(rule1.getExclusionPattern().pattern(), is(""));
    assertThat(rule1.toString(), endsWith("{+/.*inc.*/ -//}"));
    assertThat(rule1.test(null), is(false));
    assertThat(rule1.test(""), is(false));
    assertThat(rule1.test("inc"), is(true));
    assertThat(rule1.test(multilineText), is(true));

    final RegularExpressionRule rule2 = new RegularExpressionRule((String) null, ".*exc.*");
    assertThat(rule2.getInclusionPattern().pattern(), is(".*"));
    assertThat(rule2.getExclusionPattern().pattern(), is(".*exc.*"));
    assertThat(rule2.toString(), endsWith("{+/.*/ -/.*exc.*/}"));
    assertThat(rule2.test(null), is(false));
    assertThat(rule2.test(""), is(false));
    assertThat(rule2.test("inc"), is(true));
    assertThat(rule2.test("exc"), is(false));
    assertThat(rule2.test(multilineText), is(false));
  }
}
