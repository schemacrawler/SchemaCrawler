package schemacrawler.tools.text.operation;


import java.sql.ResultSet;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.base.Formatter;

interface DataFormatter
  extends Formatter
{

  void handleData(final String title, final ResultSet rows)
    throws SchemaCrawlerException;

}
