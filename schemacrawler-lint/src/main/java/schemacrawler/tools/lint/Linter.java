/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.lint;


import static sf.util.Utility.readResourceFully;

import java.io.Serializable;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.NamedObject;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.StringFormat;

/**
 * Evaluates a catalog and creates lints. This base class has core
 * functionality for maintaining state, but not for visiting a catalog.
 * Includes code for dispatching a linter.
 *
 * @author Sualeh Fatehi
 */
abstract class Linter
  implements Comparable<Linter>
{

  private static final Logger LOGGER = Logger.getLogger(Linter.class.getName());

  private LintCollector collector;
  private LintSeverity severity;
  private LintDispatch dispatch;
  private int dispatchThreshold;
  private int lintCount;

  protected Linter()
  {
    severity = LintSeverity.medium; // default value
  }

  @Override
  public int compareTo(final Linter otherLinter)
  {
    if (otherLinter == null)
    {
      return -1;
    }

    int comparison = 0;

    if (comparison == 0)
    {
      comparison = severity.compareTo(otherLinter.severity);
    }

    if (comparison == 0)
    {
      comparison = getLinterId().compareTo(otherLinter.getLinterId());
    }

    if (comparison == 0)
    {
      comparison = lintCount - otherLinter.lintCount;
    }

    if (comparison == 0)
    {
      comparison = dispatch.compareTo(otherLinter.dispatch);
    }

    if (comparison == 0)
    {
      comparison = dispatchThreshold - otherLinter.dispatchThreshold;
    }

    return comparison;
  }

  /**
   * Gets a lengthy description of the linter. By default, reads a
   * resource file called /help/<class-name>.txt and if that is not
   * present, returns the summary. Can be overridden.
   *
   * @return Lengthy description of the linter
   */
  public String getDescription()
  {
    final String descriptionResource = String
      .format("/help/%s.txt", this.getClass().getName().replace(".", "/"));

    final String descriptionText;
    if (Linter.class.getResource(descriptionResource) == null)
    {
      return getSummary();
    }
    else
    {
      descriptionText = readResourceFully(descriptionResource);
    }
    return descriptionText;
  }

  /**
   * Gets the number of lints produced by this linter.
   *
   * @return Lint counts
   */
  public final int getLintCount()
  {
    return lintCount;
  }

  /**
   * Gets the identification of this linter.
   *
   * @return Identification of this linter
   */
  public String getLinterId()
  {
    return getClass().getName();
  }

  /**
   * Gets the identification of this linter instance.
   *
   * @return Identification of this linter instance
   */
  public final String getLinterInstanceId()
  {
    return super.toString();
  }

  /**
   * Gets the severity of the lints produced by this linter.
   *
   * @return Severity of the lints produced by this linter
   */
  public final LintSeverity getSeverity()
  {
    return severity;
  }

  /**
   * Gets a brief summary of this linter. Needs to be overridden.
   *
   * @return Brief summary of this linter
   */
  public abstract String getSummary();

  @Override
  public String toString()
  {
    return String.format("%s - %s [%s]",
                         getLinterInstanceId(),
                         getSummary(),
                         getSeverity());
  }

  protected final <N extends NamedObject & AttributedObject, V extends Serializable> void addLint(final N namedObject,
                                                                                                  final String message,
                                                                                                  final V value)
  {
    LOGGER.log(Level.FINE,
               new StringFormat("Found lint for %s: %s --> %s",
                                namedObject,
                                message,
                                value));
    if (collector != null)
    {
      final Lint<V> lint = new Lint<>(getLinterId(),
                                      getLinterInstanceId(),
                                      namedObject,
                                      getSeverity(),
                                      message,
                                      value);
      collector.addLint(namedObject, lint);
      lintCount = lintCount + 1;
    }
  }

  /**
   * Allows subclasses to configure themselves with custom parameters.
   * Can be overridden.
   *
   * @param config
   *        Custom configuration
   */
  protected void configure(final Config config)
  {

  }

  /**
   * Set the severity of the lints created by this linter.
   *
   * @param severity
   *        Severity to set. No changes are made if the parameter is
   *        null.
   */
  protected final void setSeverity(final LintSeverity severity)
  {
    if (severity != null)
    {
      this.severity = severity;
    }
  }

  void configure(final LinterConfig linterConfig)
  {
    if (linterConfig != null)
    {
      setSeverity(linterConfig.getSeverity());
      setDispatch(linterConfig.getDispatch());
      setDispatchThreshold(linterConfig.getDispatchThreshold());
      configure(linterConfig.getConfig());
    }
  }

  final void dispatch()
  {
    if (shouldDispatch())
    {
      dispatch.dispatch();
    }
  };

  abstract void lint(Catalog catalog, Connection connection)
    throws SchemaCrawlerException;

  final void setLintCollector(final LintCollector lintCollector)
  {
    collector = lintCollector;
  }

  final boolean shouldDispatch()
  {
    return dispatch != null && dispatch != LintDispatch.none
           && lintCount > dispatchThreshold;
  }

  private void setDispatch(final LintDispatch dispatch)
  {
    if (dispatch == null)
    {
      this.dispatch = LintDispatch.none;
    }
    else
    {
      this.dispatch = dispatch;
    }
  }

  private void setDispatchThreshold(final int dispatchThreshold)
  {
    this.dispatchThreshold = dispatchThreshold;
  }

}
