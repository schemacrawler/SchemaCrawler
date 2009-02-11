/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
package schemacrawler.crawl;


import schemacrawler.schema.Catalog;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public class CachedSchemaCrawler
  implements SchemaCrawler
{

  protected final Catalog catalog;

  public CachedSchemaCrawler(final Catalog catalog)
  {
    this.catalog = catalog;
  }

  public void crawl(final SchemaCrawlerOptions options,
                    final CrawlHandler handler)
    throws SchemaCrawlerException
  {
    if (handler == null)
    {
      throw new SchemaCrawlerException("No crawl handler specified");
    }
    if (catalog == null)
    {
      throw new SchemaCrawlerException("No schema loaded");
    }

    handler.begin();
    handler.handle(catalog.getJdbcDriverInfo());
    handler.handle(catalog.getDatabaseInfo());
    for (final Schema schema: catalog.getSchemas())
    {
      for (final Table table: schema.getTables())
      {
        handler.handle(table);
      }
      if (options == null || options.isShowStoredProcedures())
      {
        for (final Procedure procedure: schema.getProcedures())
        {
          handler.handle(procedure);
        }
      }
    }
    handler.end();
  }

  public Catalog getCatalog()
  {
    return catalog;
  }

}
