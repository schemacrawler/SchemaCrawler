package schemacrawler.tools.integration.xml;


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

import com.thoughtworks.xstream.XStream;

public final class XmlSchemaCrawler
  implements SchemaCrawler
{

  private Schema schema;
  private Reader reader;

  public XmlSchemaCrawler(final Reader reader)
    throws SchemaCrawlerException
  {
    load(reader);
  }

  public XmlSchemaCrawler()
  {
  }

  public XmlSchemaCrawler(final Schema schema)
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
    for (final Procedure procedure: schema.getProcedures())
    {
      handler.handle(procedure);
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
    if (reader == null)
    {
      throw new SchemaCrawlerException("Reader not provided");
    }
    final XStream xStream = new XStream();
    schema = (Schema) xStream.fromXML(reader);
    return schema;
  }

  public Schema load(final Reader reader)
    throws SchemaCrawlerException
  {
    setReader(reader);
    return load((SchemaCrawlerOptions) null);
  }

  public void save(final Writer writer)
    throws SchemaCrawlerException
  {
    if (writer == null)
    {
      throw new SchemaCrawlerException("Writer not provided");
    }
    if (schema == null)
    {
      throw new SchemaCrawlerException("No schema loaded");
    }
    final XStream xStream = new XStream();
    xStream.toXML(schema, writer);
    try
    {
      writer.flush();
    }
    catch (IOException e)
    {
      throw new SchemaCrawlerException("Could not flush writer", e);
    }
  }

  public void setReader(final Reader reader)
  {
    this.reader = reader;
  }

}
