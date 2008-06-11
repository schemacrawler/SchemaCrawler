package schemacrawler.tools.integration.xml;


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import schemacrawler.crawl.CachedSchemaCrawler;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerException;

import com.thoughtworks.xstream.XStream;

public final class XmlSchemaCrawler
  extends CachedSchemaCrawler
{

  public XmlSchemaCrawler(final Reader reader)
  {
    super((Schema) new XStream().fromXML(reader));
  }

  public XmlSchemaCrawler(final Schema schema)
  {
    super(schema);
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
      throw new SchemaCrawlerException("No cached schema");
    }
    final XStream xStream = new XStream();
    xStream.toXML(schema, writer);
    try
    {
      writer.flush();
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not flush writer", e);
    }
  }

}
