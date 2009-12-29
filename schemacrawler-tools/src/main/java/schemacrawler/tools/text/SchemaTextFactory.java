package schemacrawler.tools.text;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.CrawlHandler;
import schemacrawler.tools.text.schema.SchemaDotFormatter;
import schemacrawler.tools.text.schema.SchemaTextFormatter;
import schemacrawler.tools.text.schema.SchemaTextOptions;

public final class SchemaTextFactory
{
  /**
   * Text formatting of schema.
   * 
   * @param options
   *        Options for text formatting of schema
   */
  public final static CrawlHandler createSchemaTextCrawlHandler(final SchemaTextOptions options,
                                                                final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    if (options == null)
    {
      throw new IllegalArgumentException("Options not provided");
    }

    final OutputFormat outputFormat = outputOptions.getOutputFormat();
    final CrawlHandler schemaTextCrawlHandler;
    if (outputFormat == OutputFormat.dot)
    {
      schemaTextCrawlHandler = new SchemaDotFormatter(options, outputOptions);
    }
    else
    {
      schemaTextCrawlHandler = new SchemaTextFormatter(options, outputOptions);
    }
    return schemaTextCrawlHandler;
  }

  private SchemaTextFactory()
  {
  }

}
