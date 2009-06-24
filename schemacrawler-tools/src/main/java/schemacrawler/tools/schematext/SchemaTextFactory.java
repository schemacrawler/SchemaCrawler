package schemacrawler.tools.schematext;


import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.OutputFormat;

final class SchemaTextFactory
{
  /**
   * Text formatting of schema.
   * 
   * @param options
   *        Options for text formatting of schema
   */
  final static CrawlHandler createSchemaTextCrawlHandler(final SchemaTextOptions options)
    throws SchemaCrawlerException
  {
    if (options == null)
    {
      throw new IllegalArgumentException("Options not provided");
    }

    final OutputFormat outputFormat = options.getOutputOptions()
      .getOutputFormat();
    final CrawlHandler schemaTextCrawlHandler;
    if (outputFormat == OutputFormat.dot)
    {
      schemaTextCrawlHandler = new SchemaDotFormatter(options);
    }
    else
    {
      schemaTextCrawlHandler = new SchemaTextFormatter(options);
    }
    return schemaTextCrawlHandler;
  }

  private SchemaTextFactory()
  {
  }

}
