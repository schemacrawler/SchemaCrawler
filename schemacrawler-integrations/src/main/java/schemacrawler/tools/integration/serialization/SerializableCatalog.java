package schemacrawler.tools.integration.serialization;


import java.io.Writer;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public interface SerializableCatalog
  extends Catalog
{

  void save(final Writer writer)
    throws SchemaCrawlerException;

}
