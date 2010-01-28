package schemacrawler.tools.text.base;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public class DatabaseTraverser
{

  private final Database database;

  public DatabaseTraverser(final Database database)
  {
    this.database = database;
  }

  public void traverse(final DatabaseTraversalHandler handler)
    throws SchemaCrawlerException
  {
    if (handler == null)
    {
      return;
    }

    handler.begin();
    handler.handle(database.getSchemaCrawlerInfo(),
                   database.getDatabaseInfo(),
                   database.getJdbcDriverInfo());

    final List<ColumnDataType> columnDataTypes = new ArrayList<ColumnDataType>();
    final List<Table> tables = new ArrayList<Table>();
    final List<Procedure> procedures = new ArrayList<Procedure>();

    columnDataTypes.addAll(Arrays.asList(database.getSystemColumnDataTypes()));
    for (final Schema schema: database.getSchemas())
    {
      columnDataTypes.addAll(Arrays.asList(schema.getColumnDataTypes()));
      tables.addAll(Arrays.asList(schema.getTables()));
      procedures.addAll(Arrays.asList(schema.getProcedures()));
    }

    if (!columnDataTypes.isEmpty())
    {
      handler.handleColumnDataTypesStart();
      for (final ColumnDataType columnDataType: columnDataTypes)
      {
        handler.handle(columnDataType);
      }
      handler.handleColumnDataTypesEnd();
    }

    if (!tables.isEmpty())
    {
      handler.handleTablesStart();
      for (final Table table: tables)
      {
        handler.handle(table);
      }
      handler.handleTablesEnd();
    }

    if (!procedures.isEmpty())
    {
      handler.handleProceduresStart();
      for (final Procedure procedure: procedures)
      {
        handler.handle(procedure);
      }
      handler.handleProceduresEnd();
    }

    handler.end();
  }

}
