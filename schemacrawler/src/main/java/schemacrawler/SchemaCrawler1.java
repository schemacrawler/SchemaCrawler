package schemacrawler;


import schemacrawler.schema.Schema;

public interface SchemaCrawler1
{

  /**
   * Crawls the schema for all tables and views.
   * 
   * @param options
   *        Options
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void crawl(final SchemaCrawlerOptions options, final CrawlHandler handler)
    throws SchemaCrawlerException;

  /**
   * Gets the entire schema, using a caching crawl handler.
   * 
   * @param options
   *        Options
   * @return Schema
   */
  Schema load(final SchemaCrawlerOptions options);

}
