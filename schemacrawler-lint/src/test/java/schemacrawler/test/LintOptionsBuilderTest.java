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
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.options.Config;

public class LintOptionsBuilderTest {

  @Test
  public void fromConfig() {

    // Build non-standard options
    final LintDispatch lintDispatch = LintDispatch.throw_exception;
    final String linterConfigs = "some_linter_configs";
    final Config config =
        LintOptionsBuilder.builder()
            .withLintDispatch(lintDispatch)
            .withLinterConfigs(linterConfigs)
            .runAllLinters(false)
            .toConfig();

    // Rebuild options
    final LintOptions lintOptions2 = LintOptionsBuilder.builder().fromConfig(config).build();
    assertThat(lintOptions2.getLintDispatch(), is(lintDispatch));
    assertThat(lintOptions2.getLinterConfigs(), is(linterConfigs));
    assertThat(lintOptions2.isRunAllLinters(), is(false));
    assertThat(lintOptions2.getConfig().size(), is(13));
  }

  @Test
  public void fromOptions() {

    // Build non-standard options
    final LintDispatch lintDispatch = LintDispatch.throw_exception;
    final String linterConfigs = "some_linter_configs";
    final LintOptions lintOptions =
        LintOptionsBuilder.builder()
            .withLintDispatch(lintDispatch)
            .withLinterConfigs(linterConfigs)
            .runAllLinters(false)
            .build();

    assertThat(lintOptions.getLintDispatch(), is(lintDispatch));
    assertThat(lintOptions.getLinterConfigs(), is(linterConfigs));
    assertThat(lintOptions.isRunAllLinters(), is(false));
    assertThat(lintOptions.getConfig().size(), is(13));

    // Rebuild options
    final LintOptions lintOptions2 = LintOptionsBuilder.builder().fromOptions(lintOptions).build();
    assertThat(lintOptions2.getLintDispatch(), is(lintDispatch));
    assertThat(lintOptions2.getLinterConfigs(), is(linterConfigs));
    assertThat(lintOptions2.isRunAllLinters(), is(false));
    assertThat(lintOptions2.getConfig().size(), is(13));
  }

  @Test
  public void withNullDispatch() {

    final LintOptions lintOptions = LintOptionsBuilder.builder().withLintDispatch(null).build();

    assertThat(lintOptions.getLintDispatch(), is(LintDispatch.none));
    assertThat(lintOptions.getLinterConfigs(), is(""));
    assertThat(lintOptions.isRunAllLinters(), is(true));
    assertThat(lintOptions.getConfig().size(), is(13));
  }
}
