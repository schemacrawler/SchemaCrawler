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
