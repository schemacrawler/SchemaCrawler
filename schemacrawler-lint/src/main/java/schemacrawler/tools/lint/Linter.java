/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint;

import schemacrawler.tools.executable.Command;
import schemacrawler.tools.lint.config.LinterConfig;

public interface Linter extends Command<LinterConfig, Void> {

  boolean exceedsThreshold();

  /**
   * @{@inheritDoc}
   */
  String getDescription();

  /**
   * Gets the number of lints produced by this linter.
   *
   * @return Lint counts
   */
  int getLintCount();

  /**
   * Gets the identification of the linter. A linter can be instantiated multiple times with
   * different configuration.
   *
   * @return Identification of the linter.
   */
  String getLinterId();

  /**
   * Gets the identification of this linter instance.
   *
   * @return Identification of this linter instance
   */
  String getLinterInstanceId();

  /**
   * Gets the severity of the lints produced by this linter.
   *
   * @return Severity of the lints produced by this linter
   */
  LintSeverity getSeverity();

  /**
   * Gets a brief summary of this linter. Needs to be overridden.
   *
   * @return Brief summary of this linter
   */
  String getSummary();
}
