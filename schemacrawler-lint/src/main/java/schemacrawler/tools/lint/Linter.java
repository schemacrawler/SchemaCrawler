package schemacrawler.tools.lint;

import java.sql.Connection;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.config.LinterConfig;

public interface Linter {

  boolean exceedsThreshold();

  /**
   * Gets the identification of the linter. A linter can be instantiated multiple times with
   * different configuration.
   *
   * @return Identification of the linter.
   */
  String getLinterId();

  /**
   * Gets the number of lints produced by this linter.
   *
   * @return Lint counts
   */
  int getLintCount();

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

  void configure(final LinterConfig linterConfig);

  void lint(final Catalog catalog, final Connection connection);

  /**
   * @{@inheritDoc}
   */
  String getDescription();
}
