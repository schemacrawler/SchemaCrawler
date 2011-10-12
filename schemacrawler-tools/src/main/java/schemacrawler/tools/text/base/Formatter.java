package schemacrawler.tools.text.base;


import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public interface Formatter
{

  void begin()
    throws SchemaCrawlerException;

  void end()
    throws SchemaCrawlerException;

  void handleInfoStart()
    throws SchemaCrawlerException;

  void handle(SchemaCrawlerInfo schemaCrawlerInfo)
    throws SchemaCrawlerException;

  void handle(DatabaseInfo databaseInfo)
    throws SchemaCrawlerException;

  void handle(JdbcDriverInfo jdbcDriverInfo)
    throws SchemaCrawlerException;

  void handleInfoEnd()
    throws SchemaCrawlerException;

}
