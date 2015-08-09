/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.InclusionRule;

public abstract class BaseLinterCatalog
  implements Linter
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseLinterCatalog.class.getName());

  private LintCollector collector;
  private boolean isRunLinter;
  private LintSeverity severity;

  protected BaseLinterCatalog()
  {
    isRunLinter = true;
    severity = LintSeverity.medium;
  }

  @Override
  public void configure(final LinterConfig linterConfig)
  {
    if (linterConfig != null)
    {
      setRunLinter(linterConfig.isRunLinter());
      setSeverity(linterConfig.getSeverity());
      configure(linterConfig.getConfig());
    }
  }

  @Override
  public String getDescription()
  {
    return getSummary();
  }

  @Override
  public String getId()
  {
    return getClass().getName();
  }

  @Override
  public final LintSeverity getSeverity()
  {
    return severity;
  }

  @Override
  public InclusionRule getTableInclusionRule()
  {
    return new IncludeAll();
  }

  @Override
  public final boolean isRunLinter()
  {
    return isRunLinter;
  }

  @Override
  public final void setLintCollector(final LintCollector lintCollector)
  {
    collector = lintCollector;
  }

  protected final <
    N extends NamedObject & AttributedObject, V extends Serializable> void
    addLint(final N namedObject, final String message, final V value)
  {
    LOGGER.log(Level.FINE, String.format("Found lint for %s: %s --> %s",
                                         namedObject, message, value));
    if (collector != null)
    {
      final Lint<V> lint = newLint(namedObject.getFullName(), message, value);
      collector.addLint(namedObject, lint);
    }
  }

  protected void configure(final Config config)
  {

  };

  protected final void setRunLinter(final boolean isRunLinter)
  {
    this.isRunLinter = isRunLinter;
  }

  protected final void setSeverity(final LintSeverity severity)
  {
    if (severity != null)
    {
      this.severity = severity;
    }
  }

  private <V extends Serializable> Lint<V>
    newLint(final String objectName, final String message, final V value)
  {
    return new SimpleLint<>(getId(), objectName, getSeverity(), message, value);
  }

}
