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

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Config;

public abstract class BaseLinterTable
  extends BaseLinterCatalog
{

  private Catalog catalog;

  @Override
  public final void lint(final Catalog catalog)
  {
    if (!isRunLinter())
    {
      return;
    }

    this.catalog = requireNonNull(catalog, "No catalog provided");
    start();
    for (final Table table: catalog.getTables())
    {
      lint(table);
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

  @Override
  protected void configure(final Config config)
  {

  };

  protected void end()
  {
  }

  protected abstract void lint(Table table);

  protected void start()
  {
  }

}
