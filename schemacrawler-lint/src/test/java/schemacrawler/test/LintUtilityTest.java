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

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.lint.LintUtility;

public class LintUtilityTest {

  @Test
  public void lintUtility() {

    assertThat(LintUtility.listStartsWith(null, new ArrayList<>()), is(false));
    assertThat(LintUtility.listStartsWith(new ArrayList<>(), null), is(false));
    assertThat(LintUtility.listStartsWith(null, null), is(false));

    assertThat(LintUtility.listStartsWith(new ArrayList<>(), new ArrayList<>()), is(true));
    assertThat(LintUtility.listStartsWith(new ArrayList<>(), List.of("1", "2")), is(false));
    assertThat(LintUtility.listStartsWith(List.of("1"), List.of("1", "2")), is(false));
    assertThat(LintUtility.listStartsWith(List.of("1", "2"), List.of("1")), is(true));

    assertThat(LintUtility.listStartsWith(List.of("3", "4"), List.of("1")), is(false));
  }
}
