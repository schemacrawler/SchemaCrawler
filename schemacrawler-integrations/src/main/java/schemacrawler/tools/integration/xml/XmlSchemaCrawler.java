package schemacrawler.tools.integration.xml;


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import schemacrawler.crawl.CachedSchemaCrawler;
import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;

import com.thoughtworks.xstream.XStream;

public final class XmlSchemaCrawler
  extends CachedSchemaCrawler
{

  public XmlSchemaCrawler(final Database database)
  {
    super(database);
  }

  public XmlSchemaCrawler(final Reader reader)
  {
    super((Database) new XStream().fromXML(reader));
  }

  public void save(final Writer writer)
    throws SchemaCrawlerException
  {
    if (writer == null)
    {
      throw new SchemaCrawlerException("Writer not provided");
    }
    if (database == null)
    {
      throw new SchemaCrawlerException("No cached database");
    }
    final XStream xStream = new XStream();
    xStream.toXML(database, writer);
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
