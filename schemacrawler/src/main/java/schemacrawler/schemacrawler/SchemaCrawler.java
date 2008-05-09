package schemacrawler.schemacrawler;



public interface SchemaCrawler
{

  /**
   * Crawls the schema for all tables and views, and other database
   * objects. The options control the extent of the crawling, while the
   * handler is responsible for doing something (such as outputting
   * details) each time a new object is found and processed.
   * 
   * @param options
   *        Options
   * @param handler
   *        Handler for SchemaCrawler
   * @throws SchemaCrawlerException
   *         On an exception
   */
  void crawl(final SchemaCrawlerOptions options, final CrawlHandler handler)
    throws SchemaCrawlerException;

}
