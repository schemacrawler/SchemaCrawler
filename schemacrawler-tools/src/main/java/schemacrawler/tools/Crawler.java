package schemacrawler.tools;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnMap;
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

    final Schema[] schemas = database.getSchemas();
    for (final ColumnDataType columnDataType: database
      .getSystemColumnDataTypes())
    {
      handler.handle(columnDataType);

    }
    for (final Schema schema: schemas)
    {
      for (final ColumnDataType columnDataType: schema.getColumnDataTypes())
      {
        handler.handle(columnDataType);
      }
    }

    final Set<ColumnMap> weakAssociations = new HashSet<ColumnMap>();
    for (final Schema schema: schemas)
    {
      for (final Table table: schema.getTables())
      {
        handler.handle(table);
        weakAssociations.addAll(Arrays.asList(table.getWeakAssociations()));
      }
    }

    final ColumnMap[] weakAssociationsArray = weakAssociations
      .toArray(new ColumnMap[weakAssociations.size()]);
    Arrays.sort(weakAssociationsArray);
    handler.handle(weakAssociationsArray);

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
