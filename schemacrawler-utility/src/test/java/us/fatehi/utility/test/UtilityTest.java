/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static us.fatehi.utility.Utility.commonPrefix;
import static us.fatehi.utility.Utility.convertForComparison;
import static us.fatehi.utility.Utility.hasNoUpperCase;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.isClassAvailable;
import static us.fatehi.utility.Utility.isIntegral;
import static us.fatehi.utility.Utility.isRegularExpression;
import static us.fatehi.utility.Utility.join;
import static us.fatehi.utility.Utility.toSnakeCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.Test;

public class UtilityTest {

  @Test
  public void commonPrefixTest() {
    assertThat(commonPrefix("preTest", null), is(""));
    assertThat(commonPrefix(null, "preCompile"), is(""));
    assertThat(commonPrefix("preTest", "preCompile"), is("pre"));
    assertThat(commonPrefix("something", "nothing"), is(""));
    assertThat(commonPrefix("preTest", ""), is(""));
    assertThat(commonPrefix("12345", "12345"), is(""));
  }

  @Test
  public void convertForComparisonTest() {
    assertThat(convertForComparison(null), is(""));
    assertThat(convertForComparison(""), is(""));
    assertThat(convertForComparison("ABC123"), is("abc123"));
    assertThat(convertForComparison("ABC_123"), is("abc_123"));
    assertThat(convertForComparison("ABC.123"), is("abc.123"));
    assertThat(convertForComparison("ABC!@#123"), is("abc123"));
    assertThat(convertForComparison("ABC_123.DEF"), is("abc_123.def"));
    assertThat(convertForComparison("ABC 123"), is("abc123"));
  }

  @Test
  public void hasNoUpperCaseTest() {
    assertThat(hasNoUpperCase(null), is(false));
    assertThat(hasNoUpperCase("A"), is(false));
    assertThat(hasNoUpperCase("Aa"), is(false));
    assertThat(hasNoUpperCase("A a"), is(false));

    assertThat(hasNoUpperCase(""), is(true));
    assertThat(hasNoUpperCase(" "), is(true));
    assertThat(hasNoUpperCase("a"), is(true));
    assertThat(hasNoUpperCase("aa"), is(true));
    assertThat(hasNoUpperCase("a s"), is(true));
    assertThat(hasNoUpperCase("1.0"), is(true));
  }

  @Test
  public void isBlankTest() {
    assertThat(isBlank(null), is(true));
    assertThat(isBlank(""), is(true));
    assertThat(isBlank(" "), is(true));
    assertThat(isBlank("   "), is(true));
    assertThat(isBlank("\t"), is(true));
    assertThat(isBlank("\n"), is(true));
    assertThat(isBlank("\r"), is(true));
    assertThat(isBlank(" \t "), is(true));
    assertThat(isBlank("\t\t"), is(true));

    assertThat(!isBlank("a"), is(true));
    assertThat(!isBlank("©"), is(true));
    assertThat(!isBlank(" a"), is(true));
    assertThat(!isBlank("a "), is(true));
    assertThat(!isBlank("a b"), is(true));
  }

  @Test
  public void isClassAvailableTest() {
    assertThat(isClassAvailable("java.lang.String"), is(true));
    assertThat(isClassAvailable("com.example.Unknown"), is(false));
  }

  @Test
  public void isIntegralTest() {
    assertThat(isIntegral(null), is(false));
    assertThat(isIntegral(""), is(false));
    assertThat(isIntegral(" "), is(false));
    assertThat(isIntegral("1.0"), is(false));
    assertThat(isIntegral("-0.3"), is(false));
    assertThat(isIntegral("a"), is(false));

    assertThat(isIntegral("1"), is(true));
    assertThat(isIntegral("+1"), is(true));
    assertThat(isIntegral("-1"), is(true));
  }

  @Test
  public void joinCollectionTest() {
    assertThat(join((Collection) null, ","), nullValue());
    assertThat(join(new ArrayList<>(), ","), nullValue());

    assertThat(join(Arrays.asList("abc"), ","), is("abc"));
    assertThat(join(Arrays.asList(new String[] {null}), ","), is("null"));
    assertThat(join(Arrays.asList("abc", "bcd"), ","), is("abc,bcd"));
    assertThat(join(Arrays.asList("abc", null), ","), is("abc,null"));
  }

  @Test
  public void joinMapTest() {
    assertThat(join((Map) null, ","), nullValue());
    assertThat(join(new HashMap<>(), ","), nullValue());

    final String[][] map = {
      {"RED", null},
      {null, "#00FF00"},
      {"BLUE", "#0000FF"}
    };

    assertThat(
        join(MapUtils.putAll(new HashMap<>(), map), ","), is("RED=null,null=#00FF00,BLUE=#0000FF"));
  }

  @Test
  public void snakeCaseTest() {
    assertThat(toSnakeCase(null), nullValue());
    assertThat(toSnakeCase("a b"), equalTo("a_b"));
    assertThat(toSnakeCase("ab"), equalTo("ab"));
    assertThat(toSnakeCase("abI"), equalTo("ab_i"));
    assertThat(toSnakeCase("Ab"), equalTo("_ab"));
    assertThat(toSnakeCase("abIj"), equalTo("ab_ij"));
    assertThat(toSnakeCase("ABC"), equalTo("_a_b_c"));
  }

  @Test
  void testValidRegexPatterns() {
    assertThat(isRegularExpression("[a-z]+"), is(true));
    assertThat(isRegularExpression("^hello$"), is(true));

    assertThat(isRegularExpression("hello"), is(false));
    assertThat(isRegularExpression("*invalid"), is(false));
  }
}
