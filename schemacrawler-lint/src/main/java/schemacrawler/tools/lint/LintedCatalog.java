/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.util.Optional;
import java.util.logging.Level;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.BaseCatalogDecorator;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import sf.util.SchemaCrawlerLogger;

public final class LintedCatalog
  extends BaseCatalogDecorator
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(LintedCatalog.class.getName());

  private static final long serialVersionUID = -3953296149824921463L;

  private final LintCollector collector;

  public LintedCatalog(final Catalog catalog,
                       final Connection connection,
                       final Linters linters)
    throws SchemaCrawlerException
  {
    super(catalog);

    try
    {
      checkConnection(connection);
    }
    catch (final SchemaCrawlerSQLException e)
    {
      // The offline snapshot executable may not have a live connection,
      // so we cannot fail with an exception. Log and continue.
      LOGGER.log(Level.WARNING, "No connection provided", e);
    }

    requireNonNull(linters, "No linters provided");
    linters.lint(catalog, connection);
    collector = linters.getCollector();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final <T> T getAttribute(final String name)
  {
    return getAttribute(name, (T) null);
  }

  public LintCollector getCollector()
  {
    return collector;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final <T> Optional<T> lookupAttribute(final String name)
  {
    return Optional.of(getAttribute(name));
  }

}
