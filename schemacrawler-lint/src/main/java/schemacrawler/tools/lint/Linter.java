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

abstract class Linter
{

  private static final Logger LOGGER = Logger.getLogger(Linter.class.getName());

  private LintCollector collector;
  private LintSeverity severity;
  private LintDispatch dispatch;
  private int dispatchThreshold;
  private int lintCount;

  protected Linter()
  {
    severity = LintSeverity.medium;
  }

  public void configure(final LinterConfig linterConfig)
  {
    if (linterConfig != null)
    {
      setSeverity(linterConfig.getSeverity());
      setDispatch(linterConfig.getDispatch());
      setDispatchThreshold(linterConfig.getDispatchThreshold());
      configure(linterConfig.getConfig());
    }
  }

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

  public final int getLintCount()
  {
    return lintCount;
  }

  public String getLinterId()
  {
    return getClass().getName();
  }

  public final String getLinterInstanceId()
  {
    return super.toString();
  }

  public final LintSeverity getSeverity()
  {
    return severity;
  }

  public abstract String getSummary();

  public void setLintCount(final int lintCount)
  {
    this.lintCount = lintCount;
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

  final void dispatch()
  {
    if (shouldDispatch())
    {
      dispatch.dispatch();
    }
  };

  abstract void lint(Catalog catalog, Connection connection)
    throws SchemaCrawlerException;;

  final void setCollector(final LintCollector collector)
  {
    this.collector = collector;
  }

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
