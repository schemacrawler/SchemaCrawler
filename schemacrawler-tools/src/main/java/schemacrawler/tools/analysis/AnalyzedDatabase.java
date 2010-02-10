package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schema.Table;

public final class AnalyzedDatabase
  implements Database
{

  private static final long serialVersionUID = -3953296149824921463L;

  private static final Logger LOGGER = Logger.getLogger(AnalyzedDatabase.class
    .getName());

  private static final String TABLE_WTH_SINGLE_COLUMN = "table with single column";

  public static final boolean isTableWithSingleColumn(final Table table)
  {
    if (table == null)
    {
      return false;
    }
    else
    {
      final Column[] columns = table.getColumns();
      return columns.length <= 1;
    }
  }

  private final Database database;
  private final List<Table> tables;

  public AnalyzedDatabase(final Database database)
  {
    if (database == null)
    {
      throw new IllegalArgumentException("No database provided");
    }
    this.database = database;

    tables = new ArrayList<Table>();
    for (final Schema schema: database.getSchemas())
    {
      for (final Table table: schema.getTables())
      {
        tables.add(table);
      }
    }

    analyzeTables();
  }

  private void analyzeTables()
  {
    for (final Table table: tables)
    {
      if (isTableWithSingleColumn(table))
      {
        table.setAttribute(TABLE_WTH_SINGLE_COLUMN, Boolean.TRUE);
      }
    }
  }

  public int compareTo(final NamedObject o)
  {
    return database.compareTo(o);
  }

  public Object getAttribute(final String name)
  {
    return database.getAttribute(name);
  }

  public Map<String, Object> getAttributes()
  {
    return database.getAttributes();
  }

  public DatabaseInfo getDatabaseInfo()
  {
    return database.getDatabaseInfo();
  }

  public String getFullName()
  {
    return database.getFullName();
  }

  public JdbcDriverInfo getJdbcDriverInfo()
  {
    return database.getJdbcDriverInfo();
  }

  public String getName()
  {
    return database.getName();
  }

  public String getRemarks()
  {
    return database.getRemarks();
  }

  public Schema getSchema(final String name)
  {
    return database.getSchema(name);
  }

  public SchemaCrawlerInfo getSchemaCrawlerInfo()
  {
    return database.getSchemaCrawlerInfo();
  }

  public Schema[] getSchemas()
  {
    return database.getSchemas();
  }

  public ColumnDataType getSystemColumnDataType(final String name)
  {
    return database.getSystemColumnDataType(name);
  }

  public ColumnDataType[] getSystemColumnDataTypes()
  {
    return database.getSystemColumnDataTypes();
  }

  public void setAttribute(final String name, final Object value)
  {
    database.setAttribute(name, value);
  }

}
