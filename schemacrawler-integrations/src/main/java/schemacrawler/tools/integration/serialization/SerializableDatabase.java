package schemacrawler.tools.integration.serialization;


import java.io.Writer;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public interface SerializableDatabase
  extends Database
{

  void save(final Writer writer)
    throws SchemaCrawlerException;

}
