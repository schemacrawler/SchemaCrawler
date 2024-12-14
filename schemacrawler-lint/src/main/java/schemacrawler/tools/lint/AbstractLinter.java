/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.lint;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/**
 * Evaluates a catalog and creates lints. This base class has core functionality for maintaining
 * state, but not for visiting a catalog. Includes code for dispatching a linter.
 */
public abstract class AbstractLinter implements Linter {

  private static final Logger LOGGER = Logger.getLogger(AbstractLinter.class.getName());

  private final PropertyName linterName;
  private final UUID linterInstanceId;
  private final LintCollector lintCollector;
  private LintSeverity severity;
  private int threshold;
  private int lintCount;

  protected AbstractLinter(final PropertyName linterName, final LintCollector lintCollector) {
    this.linterName = requireNonNull(linterName, "Linter name cannot be null");
    linterInstanceId = UUID.randomUUID();
    this.lintCollector = requireNonNull(lintCollector, "Lint collector cannot be null");

    severity = LintSeverity.medium; // default value
    threshold = Integer.MAX_VALUE; // default value
  }

  @Override
  public void configure(final LinterConfig linterConfig) {
    if (linterConfig != null) {
      setSeverity(linterConfig.getSeverity());
      threshold = linterConfig.getThreshold();
      configure(linterConfig.getConfig());
    }
  }

  /**
   * @{@inheritDoc}
   */
  @Override
  public final boolean exceedsThreshold() {
    return lintCount > threshold;
  }

  /**
   * @{@inheritDoc}
   */
  @Override
  public String getDescription() {
    return linterName.getDescription();
  }

  /**
   * @{@inheritDoc}
   */
  @Override
  public final int getLintCount() {
    return lintCount;
  }

  /**
   * @{@inheritDoc}
   */
  @Override
  public String getLinterId() {
    return linterName.getName();
  }

  /**
   * @{@inheritDoc}
   */
  @Override
  public final String getLinterInstanceId() {
    return linterInstanceId.toString();
  }

  @Override
  public final String getName() {
    return getLinterId();
  }

  /**
   * @{@inheritDoc}
   */
  @Override
  public final LintSeverity getSeverity() {
    return severity;
  }

  /**
   * Gets a brief summary of this linter. Needs to be overridden.
   *
   * @return Brief summary of this linter
   */
  @Override
  public abstract String getSummary();

  /**
   * @{@inheritDoc}
   */
  @Override
  public final String toString() {
    return String.format("%s [%s] - %s", getLinterInstanceId(), getSeverity(), getSummary());
  }

  protected final <N extends NamedObject & AttributedObject, V extends Serializable> void addLint(
      final LintObjectType objectType, final N namedObject, final String message, final V value) {
    LOGGER.log(
        Level.FINE, new StringFormat("Found lint for %s: %s --> %s", namedObject, message, value));
    if (lintCollector != null) {
      final Lint<V> lint =
          new Lint<>(
              getLinterId(),
              getLinterInstanceId(),
              objectType,
              namedObject,
              getSeverity(),
              message,
              value);
      lintCollector.addLint(namedObject, lint);
      lintCount = lintCount + 1;
    }
  }

  /**
   * Allows subclasses to configure themselves with custom parameters. Can be overridden.
   *
   * @param config Custom configuration
   */
  protected void configure(final Config config) {
    // To be overridden by subclass if they need custom parameters
  }

  /**
   * Set the severity of the lints created by this linter.
   *
   * @param severity Severity to set. No changes are made if the parameter is null.
   */
  protected final void setSeverity(final LintSeverity severity) {
    if (severity != null) {
      this.severity = severity;
    }
  }
}
