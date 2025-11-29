/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.lint.options;

import static us.fatehi.utility.Utility.trimToEmpty;

import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.text.options.BaseTextOptionsBuilder;

public final class LintOptionsBuilder
    extends BaseTextOptionsBuilder<LintOptionsBuilder, LintOptions> {

  private static final String CLI_LINTER_CONFIGS = "linter-configs";
  private static final String CLI_LINT_DISPATCH = "lint-dispatch";
  private static final String CLI_RUN_ALL_LINTERS = "run-all-linters";
  private static final String SCHEMACRAWLER_LINT_PREFIX = "schemacrawler.lint.";
  private static final String LINTER_CONFIGS = SCHEMACRAWLER_LINT_PREFIX + CLI_LINTER_CONFIGS;
  private static final String LINT_DISPATCH = SCHEMACRAWLER_LINT_PREFIX + CLI_LINT_DISPATCH;
  private static final String RUN_ALL_LINTERS = SCHEMACRAWLER_LINT_PREFIX + CLI_RUN_ALL_LINTERS;

  public static LintOptionsBuilder builder() {
    return new LintOptionsBuilder();
  }

  LintDispatch lintDispatch;
  String linterConfigs;
  boolean runAllLinters;
  Config config;

  private LintOptionsBuilder() {
    linterConfigs = "";
    lintDispatch = LintDispatch.none;
    runAllLinters = true;
    config = toConfig();
  }

  @Override
  public LintOptionsBuilder fromConfig(final Config config) {
    if (config == null) {
      return this;
    }
    super.fromConfig(config);

    final String linterConfigsKey;
    if (config.containsKey(CLI_LINTER_CONFIGS)) {
      // Honor command-line option first
      linterConfigsKey = CLI_LINTER_CONFIGS;
    } else {
      // Otherwise, take option from SchemaCrawler configuration file
      linterConfigsKey = LINTER_CONFIGS;
    }
    linterConfigs = config.getStringValue(linterConfigsKey);

    final String lintDispatchKey;
    if (config.containsKey(CLI_LINT_DISPATCH)) {
      // Honor command-line option first
      lintDispatchKey = CLI_LINT_DISPATCH;
    } else {
      // Otherwise, take option from SchemaCrawler configuration file
      lintDispatchKey = LINT_DISPATCH;
    }
    lintDispatch = config.getEnumValue(lintDispatchKey, LintDispatch.none);

    final String runAllLintersKey;
    if (config.containsKey(CLI_RUN_ALL_LINTERS)) {
      // Honor command-line option first
      runAllLintersKey = CLI_RUN_ALL_LINTERS;
    } else {
      // Otherwise, take option from SchemaCrawler configuration file
      runAllLintersKey = RUN_ALL_LINTERS;
    }
    runAllLinters = config.getBooleanValue(runAllLintersKey, true);

    // Save config
    this.config.merge(config);

    return this;
  }

  @Override
  public LintOptionsBuilder fromOptions(final LintOptions options) {
    if (options == null) {
      return this;
    }
    super.fromOptions(options);

    linterConfigs = options.getLinterConfigs();
    lintDispatch = options.getLintDispatch();
    runAllLinters = options.isRunAllLinters();

    return this;
  }

  /** With value for running all linters. */
  public LintOptionsBuilder runAllLinters(final boolean runAllLinters) {
    this.runAllLinters = runAllLinters;

    return this;
  }

  @Override
  public Config toConfig() {
    final Config config = super.toConfig();
    config.put(LINTER_CONFIGS, linterConfigs);
    config.put(LINT_DISPATCH, lintDispatch);
    config.put(RUN_ALL_LINTERS, runAllLinters);
    // Lint report output format is not written to the config
    return config;
  }

  @Override
  public LintOptions toOptions() {
    return new LintOptions(this);
  }

  /** With a lint dispatch strategy. */
  public LintOptionsBuilder withLintDispatch(final LintDispatch lintDispatch) {
    if (lintDispatch == null) {
      this.lintDispatch = LintDispatch.none;
    } else {
      this.lintDispatch = lintDispatch;
    }
    return this;
  }

  /** With the name of a linter configs file. */
  public LintOptionsBuilder withLinterConfigs(final String linterConfigs) {
    this.linterConfigs = trimToEmpty(linterConfigs);
    return this;
  }
}
