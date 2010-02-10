package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Index;
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

  private static final String INCREMENTING_COLUMNS = "incrementing columns";
  private static final String TABLE_WTH_NO_INDICES = "table with no indices";

  public static final Column[] getIncrementingColumns(final Table table)
  {
    if (table == null)
    {
      return null;
    }
    else
    {
      return table.getAttribute(INCREMENTING_COLUMNS, new Column[0]);
    }
  }

  public static final boolean isTableWithNoIndices(final Table table)
  {
    if (table == null)
    {
      return false;
    }
    else
    {
      return table.getAttribute(TABLE_WTH_NO_INDICES, Boolean.FALSE);
    }
  }

  public static final boolean isTableWithSingleColumn(final Table table)
  {
    if (table == null)
    {
      return false;
    }
    else
    {
      return table.getAttribute(TABLE_WTH_SINGLE_COLUMN, Boolean.FALSE);
    }
  }

  private static Column[] findIncrementingColumns(final Table table,
                                                  final Column[] columns)
  {
    final Pattern pattern = Pattern.compile("([^0-9]*)([0-9]+)");

    final Map<String, Integer> incrementingColumnsMap = new HashMap<String, Integer>();
    for (final Column column: columns)
    {
      final String columnName = column.getName();
      incrementingColumnsMap.put(columnName, 1);
      final Matcher matcher = pattern.matcher(columnName);
      if (matcher.matches())
      {
        final String columnNameBase = matcher.group(1);
        if (incrementingColumnsMap.containsKey(columnNameBase))
        {
          incrementingColumnsMap.put(columnNameBase, incrementingColumnsMap
            .get(columnNameBase) + 1);
        }
        else
        {
          incrementingColumnsMap.put(columnNameBase, 1);
        }
      }
    }
    for (final String columnNameBase: incrementingColumnsMap.keySet())
    {
      if (incrementingColumnsMap.get(columnNameBase) == 1)
      {
        incrementingColumnsMap.remove(columnNameBase);
      }
    }

    final List<Column> incrementingColumns = new ArrayList<Column>();
    for (final Column column: columns)
    {
      final Matcher matcher = pattern.matcher(column.getName());
      if (matcher.matches())
      {
        final String columnNameBase = matcher.group(1);
        if (incrementingColumnsMap.containsKey(columnNameBase))
        {
          incrementingColumns.add(column);
        }
      }
    }

    return incrementingColumns.toArray(new Column[incrementingColumns.size()]);
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

  public int compareTo(final NamedObject o)
  {
    return database.compareTo(o);
  }

  public Object getAttribute(final String name)
  {
    return database.getAttribute(name);
  }

  public <T> T getAttribute(final String name, final T defaultValue)
  {
    return database.getAttribute(name, defaultValue);
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

  private void analyzeTables()
  {
    for (final Table table: tables)
    {
      final Column[] columns = table.getColumns();
      if (columns.length <= 1)
      {
        table.setAttribute(TABLE_WTH_SINGLE_COLUMN, Boolean.TRUE);
      }
      final Column[] incrementingColumns = findIncrementingColumns(table,
                                                                   columns);
      if (incrementingColumns.length > 0)
      {
        table.setAttribute(INCREMENTING_COLUMNS, incrementingColumns);
      }

      final Index[] indices = table.getIndices();
      if (indices.length == 0)
      {
        table.setAttribute(TABLE_WTH_NO_INDICES, Boolean.TRUE);
      }
    }
  }

}
