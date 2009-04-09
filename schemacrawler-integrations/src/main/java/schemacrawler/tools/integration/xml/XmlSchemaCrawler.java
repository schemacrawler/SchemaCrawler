package schemacrawler.tools.integration.xml;


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import schemacrawler.crawl.CachedSchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;

import com.thoughtworks.xstream.XStream;

public final class XmlSchemaCrawler
  extends CachedSchemaCrawler
{

  public XmlSchemaCrawler(final Catalog catalog)
  {
    super(catalog);
  }

  public XmlSchemaCrawler(final Reader reader)
  {
    super((Catalog) new XStream().fromXML(reader));
  }

  public void save(final Writer writer)
    throws SchemaCrawlerException
  {
    if (writer == null)
    {
      throw new SchemaCrawlerException("Writer not provided");
    }
    if (catalog == null)
    {
      throw new SchemaCrawlerException("No cached schema");
    }
    final XStream xStream = new XStream();
    xStream.toXML(catalog, writer);
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
