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


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.BaseCatalogDecorator;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public final class LintedCatalog
  extends BaseCatalogDecorator
{

  private static final Logger LOGGER = Logger
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
    catch (final SchemaCrawlerException e)
    {
      // The offline snapshot executable may not have a live connection,
      // so we cannot fail with an exception. Log and continue.
      LOGGER.log(Level.WARNING, "No connection provided", e);
    }

    requireNonNull(linters, "No linters provided");
    linters.lint(catalog, connection);
    collector = linters.getCollector();
  }

  public LintCollector getCollector()
  {
    return collector;
  }

}
