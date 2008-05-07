package schemacrawler.crawl;


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

  protected final Schema schema;

  public CachedSchemaCrawler(final Schema schema)
  {
    this.schema = schema;
  }

  public void crawl(final SchemaCrawlerOptions options,
                    final CrawlHandler handler)
    throws SchemaCrawlerException
  {
    if (handler == null)
    {
      throw new SchemaCrawlerException("No crawl handler specified");
    }
    if (schema == null)
    {
      throw new SchemaCrawlerException("No schema loaded");
    }

    handler.begin();
    handler.handle(schema.getJdbcDriverInfo());
    handler.handle(schema.getDatabaseInfo());
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
    handler.end();
  }

  public Schema getSchema()
  {
    return schema;
  }

  public Schema load(final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    if (schema == null)
    {
      throw new SchemaCrawlerException("No cached schema");
    }
    return schema;
  }

}
