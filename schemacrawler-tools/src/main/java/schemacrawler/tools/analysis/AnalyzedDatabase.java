/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import sf.util.Utility;

public final class AnalyzedDatabase
  implements Database
{

  private static final long serialVersionUID = -3953296149824921463L;

  private static final Logger LOGGER = Logger.getLogger(AnalyzedDatabase.class
    .getName());

  private static final String TABLE_WTH_SINGLE_COLUMN = "table with single column";
  private static final String INCREMENTING_COLUMNS = "incrementing columns";
  private static final String TABLE_WTH_NO_INDICES = "table with no indices";
  private static final String NULLABLE_COLUMN_IN_UNIQUE_INDEX = "nullable column in unique index";
  private static final String NULL_DEFAULT_VALUE_MAY_BE_INTENDED = "NULL default value may be intended";

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

  public static final boolean isNullableColumnInUniqueIndex(final Column column)
  {
    if (column == null)
    {
      return false;
    }
    else
    {
      return column
        .getAttribute(NULLABLE_COLUMN_IN_UNIQUE_INDEX, Boolean.FALSE);
    }
  }

  public static final boolean isNullDefaultValueMayBeIntended(final Column column)
  {
    if (column == null)
    {
      return false;
    }
    else
    {
      return column.getAttribute(NULL_DEFAULT_VALUE_MAY_BE_INTENDED,
                                 Boolean.FALSE);
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

  private static Column[] findIncrementingColumns(final Column[] columns)
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

  private final Map<String, Column[]> incrementingColumnsMap = new LinkedHashMap<String, Column[]>();

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

  public final Column[] getIncrementingColumns()
  {
    final Set<Column> incrementingColumns = new HashSet<Column>();
    for (final Column[] columns: incrementingColumnsMap.values())
    {
      incrementingColumns.addAll(Arrays.asList(columns));
    }
    final Column[] incrementingColumnsArray = incrementingColumns
      .toArray(new Column[incrementingColumns.size()]);
    Arrays.sort(incrementingColumnsArray);
    return incrementingColumnsArray;
  }

  public final Column[] getIncrementingColumnsForTable(final String tableFullName)
  {
    if (incrementingColumnsMap.containsKey(tableFullName))
    {
      return incrementingColumnsMap.get(tableFullName);
    }
    else
    {
      return new Column[0];
    }
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
      final Index[] indices = table.getIndices();
      if (indices.length == 0)
      {
        table.setAttribute(TABLE_WTH_NO_INDICES, Boolean.TRUE);
      }

      final Column[] columns = table.getColumns();
      if (columns.length <= 1)
      {
        table.setAttribute(TABLE_WTH_SINGLE_COLUMN, Boolean.TRUE);
      }

      final Column[] incrementingColumns = findIncrementingColumns(columns);
      if (incrementingColumns.length > 0)
      {
        table.setAttribute(INCREMENTING_COLUMNS, incrementingColumns);
        incrementingColumnsMap.put(table.getFullName(), incrementingColumns);
      }

      for (final Column column: columns)
      {
        if (column.isNullable() && column.isPartOfUniqueIndex())
        {
          column.setAttribute(NULLABLE_COLUMN_IN_UNIQUE_INDEX, Boolean.TRUE);
        }
        final String columnDefaultValue = column.getDefaultValue();
        if (!Utility.isBlank(columnDefaultValue)
            && columnDefaultValue.trim().equalsIgnoreCase("NULL"))
        {
          column.setAttribute(NULL_DEFAULT_VALUE_MAY_BE_INTENDED, Boolean.TRUE);
        }
      }
    }
  }

}
