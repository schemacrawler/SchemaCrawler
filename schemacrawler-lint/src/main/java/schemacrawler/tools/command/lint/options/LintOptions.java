/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.command.lint.options;

import static java.util.Objects.requireNonNull;

import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.options.Config;
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
    this.config = new Config(builder.config);
  }

  /**
   * Get properties.
   *
   * @return Properties
   */
  public Config getConfig() {
    return new Config(config);
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
