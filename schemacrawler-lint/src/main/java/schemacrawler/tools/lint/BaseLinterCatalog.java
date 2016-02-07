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
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schemacrawler.Config;
import sf.util.StringFormat;

public abstract class BaseLinterCatalog
  implements Linter
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseLinterCatalog.class.getName());

  private LintCollector collector;
  private LintSeverity severity;

  protected BaseLinterCatalog()
  {
    severity = LintSeverity.medium;
  }

  @Override
  public void configure(final LinterConfig linterConfig)
  {
    if (linterConfig != null)
    {
      setSeverity(linterConfig.getSeverity());
      configure(linterConfig.getConfig());
    }
  }

  @Override
  public String getDescription()
  {
    final String descriptionResource = String
      .format("/help/%s.txt", this.getClass().getName().replace(".", "/"));

    final String descriptionText;
    if (BaseLinterCatalog.class.getResource(descriptionResource) == null)
    {
      return getSummary();
    }
    else
    {
      descriptionText = readResourceFully(descriptionResource);
    }
    return descriptionText;
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
  public final void setLintCollector(final LintCollector lintCollector)
  {
    collector = lintCollector;
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
      final Lint<V> lint = new SimpleLint<>(getId(),
                                            namedObject,
                                            getSeverity(),
                                            message,
                                            value);
      collector.addLint(namedObject, lint);
    }
  }

  protected void configure(final Config config)
  {

  };

  protected final void setSeverity(final LintSeverity severity)
  {
    if (severity != null)
    {
      this.severity = severity;
    }
  }

}
