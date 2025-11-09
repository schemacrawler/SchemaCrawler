/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.lint.options;

import static java.util.Objects.requireNonNull;

import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.text.options.BaseTextOptions;

public class LintOptions extends BaseTextOptions {

  private final String linterConfigs;
  private final LintDispatch lintDispatch;
  private final boolean runAllLinters;
  private final Config config;

  public LintOptions(final LintOptionsBuilder builder) {
    super(builder);
    linterConfigs = builder.linterConfigs;
    lintDispatch = requireNonNull(builder.lintDispatch, "No dispatch provided");
    runAllLinters = builder.runAllLinters;
    requireNonNull(builder.config, "No properties provided");
    config = ConfigUtility.fromConfig(builder.config);
  }

  /**
   * Get properties.
   *
   * @return Properties
   */
  public Config getConfig() {
    return ConfigUtility.fromConfig(config);
  }

  /**
   * Gets the dispatch strategy.
   *
   * @return Lint dispatch strategy.
   */
  public LintDispatch getLintDispatch() {
    return lintDispatch;
  }

  /**
   * Gets the path to the linter configs file.
   *
   * @return Path to the linter configs file.
   */
  public String getLinterConfigs() {
    return linterConfigs;
  }

  /**
   * Whether to run all linters, including the ones that are not explicitly configured.
   *
   * @return Whether to run all linters.
   */
  public boolean isRunAllLinters() {
    return runAllLinters;
  }
}
