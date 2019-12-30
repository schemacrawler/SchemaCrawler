/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.lint.executable;


import static sf.util.Utility.isBlank;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.text.base.BaseTextOptionsBuilder;

public final class LintOptionsBuilder
  extends BaseTextOptionsBuilder<LintOptionsBuilder, LintOptions>
{

  private static final String CLI_LINTER_CONFIGS = "linter-configs";
  private static final String CLI_LINT_DISPATCH = "lint-dispatch";
  private static final String CLI_RUN_ALL_LINTERS = "run-all-linters";
  private static final String SCHEMACRAWLER_LINT_PREFIX = "schemacrawler.lint.";
  private static final String LINTER_CONFIGS =
    SCHEMACRAWLER_LINT_PREFIX + CLI_LINTER_CONFIGS;
  private static final String LINT_DISPATCH =
    SCHEMACRAWLER_LINT_PREFIX + CLI_LINT_DISPATCH;
  private static final String RUN_ALL_LINTERS =
    SCHEMACRAWLER_LINT_PREFIX + CLI_RUN_ALL_LINTERS;

  public static LintOptionsBuilder builder()
  {
    return new LintOptionsBuilder();
  }

  public static LintOptionsBuilder builder(final LintOptions options)
  {
    return new LintOptionsBuilder().fromOptions(options);
  }

  public static LintOptions newLintOptions()
  {
    return new LintOptionsBuilder().toOptions();
  }

  public static LintOptions newLintOptions(final Config config)
  {
    return new LintOptionsBuilder()
      .fromConfig(config)
      .toOptions();
  }

  LintDispatch lintDispatch;
  String linterConfigs;
  boolean runAllLinters;

  private LintOptionsBuilder()
  {
    linterConfigs = "";
    lintDispatch = LintDispatch.none;
    runAllLinters = true;
  }

  @Override
  public LintOptionsBuilder fromConfig(final Config map)
  {
    if (map == null)
    {
      return this;
    }
    super.fromConfig(map);

    final Config config = new Config(map);

    final String linterConfigsKey;
    if (config.containsKey(CLI_LINTER_CONFIGS))
    {
      // Honor command-line option first
      linterConfigsKey = CLI_LINTER_CONFIGS;
    }
    else
    {
      // Otherwise, take option from SchemaCrawler configuration file
      linterConfigsKey = LINTER_CONFIGS;
    }
    linterConfigs = config.getStringValue(linterConfigsKey, "");

    final String lintDispatchKey;
    if (config.containsKey(CLI_LINT_DISPATCH))
    {
      // Honor command-line option first
      lintDispatchKey = CLI_LINT_DISPATCH;
    }
    else
    {
      // Otherwise, take option from SchemaCrawler configuration file
      lintDispatchKey = LINT_DISPATCH;
    }
    lintDispatch = config.getEnumValue(lintDispatchKey, LintDispatch.none);

    final String runAllLintersKey;
    if (config.containsKey(CLI_RUN_ALL_LINTERS))
    {
      // Honor command-line option first
      runAllLintersKey = CLI_RUN_ALL_LINTERS;
    }
    else
    {
      // Otherwise, take option from SchemaCrawler configuration file
      runAllLintersKey = RUN_ALL_LINTERS;
    }
    runAllLinters = config.getBooleanValue(runAllLintersKey, true);

    return this;
  }

  @Override
  public LintOptionsBuilder fromOptions(final LintOptions options)
  {
    if (options == null)
    {
      return this;
    }
    super.fromOptions(options);

    linterConfigs = options.getLinterConfigs();
    lintDispatch = options.getLintDispatch();
    runAllLinters = options.isRunAllLinters();

    return this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = super.toConfig();
    config.setStringValue(LINTER_CONFIGS, linterConfigs);
    config.setEnumValue(LINT_DISPATCH, lintDispatch);
    config.setBooleanValue(RUN_ALL_LINTERS, runAllLinters);
    // Lint report output format is not written to the config
    return config;
  }

  @Override
  public LintOptions toOptions()
  {
    return new LintOptions(this);
  }

  /**
   * With the name of a linter configs file.
   */
  public LintOptionsBuilder withLinterConfigs(final String linterConfigs)
  {
    if (isBlank(linterConfigs))
    {
      this.linterConfigs = "";
    }
    else
    {
      this.linterConfigs = linterConfigs;
    }
    return this;
  }

  /**
   * With a lint dispatch strategy.
   */
  public LintOptionsBuilder withLintDispatch(final LintDispatch lintDispatch)
  {
    if (lintDispatch == null)
    {
      this.lintDispatch = LintDispatch.none;
    }
    else
    {
      this.lintDispatch = lintDispatch;
    }
    return this;
  }

  /**
   * With value for running all linters.
   */
  public LintOptionsBuilder runAllLinters(final boolean runAllLinters)
  {
    this.runAllLinters = runAllLinters;

    return this;
  }

}
