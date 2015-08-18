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


import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.RegularExpressionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public abstract class BaseLinter
  extends BaseLinterCatalog
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseLinter.class.getName());

  private Catalog catalog;
  private InclusionRule tableInclusionRule;

  protected BaseLinter()
  {
    tableInclusionRule = new IncludeAll();
  }

  @Override
  public void configure(final LinterConfig linterConfig)
  {
    super.configure(linterConfig);
    if (linterConfig != null)
    {
      tableInclusionRule = new RegularExpressionRule(linterConfig
        .getTableInclusionPattern(), linterConfig.getTableExclusionPattern());
    }
  }

  @Override
  public InclusionRule getTableInclusionRule()
  {
    return tableInclusionRule;
  }

  @Override
  public final void lint(final Catalog catalog, final Connection connection)
    throws SchemaCrawlerException
  {
    this.catalog = requireNonNull(catalog, "No catalog provided");

    start();
    for (final Table table: catalog.getTables())
    {
      if (tableInclusionRule.test(table.getFullName()))
      {
        lint(table, connection);
      }
      else
      {
        LOGGER
          .log(Level.FINE,
               String.format("Excluding table %s for lint %s", table, getId()));
      }
    }
    end();
    this.catalog = null;
  }

  protected <V extends Serializable> void addCatalogLint(final String message,
                                                         final V value)
  {
    if (catalog != null)
    {
      addLint(catalog, message, value);
    }
  }

  protected void end()
  {
  }

  protected CrawlInfo getCrawlInfo()
  {
    return catalog.getCrawlInfo();
  }

  protected void lint(final Table table, final Connection connection)
    throws SchemaCrawlerException
  {
  }

  protected void start()
  {
  }

}
