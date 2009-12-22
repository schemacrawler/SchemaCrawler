package schemacrawler.tools;


import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public class Crawler
{

  private final Database database;

  public Crawler(Database database)
  {
    this.database = database;
  }

  public void crawl(final CrawlHandler handler)
    throws SchemaCrawlerException
  {
    if (handler == null)
    {
      return;
    }

    handler.begin();
    handler.handle(database.getSchemaCrawlerInfo());
    handler.handle(database.getDatabaseInfo());
    handler.handle(database.getJdbcDriverInfo());

    for (final ColumnDataType columnDataType: database
      .getSystemColumnDataTypes())
    {
      handler.handle(columnDataType);

    }

    final Schema[] schemas = database.getSchemas();
    for (final Schema schema: schemas)
    {
      for (final Table table: schema.getTables())
      {
        handler.handle(table);
      }
    }
    for (final Schema schema: schemas)
    {
      for (final Procedure procedure: schema.getProcedures())
      {
        handler.handle(procedure);
      }
    }

    handler.end();
  }
}
