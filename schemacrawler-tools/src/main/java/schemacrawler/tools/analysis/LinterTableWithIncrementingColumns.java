package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import sf.util.ObjectToString;

public class LinterTableWithIncrementingColumns
  extends BaseLinter<Table>
{

  public void lint(final Table table)
  {
    if (table != null)
    {
      final Column[] incrementingColumns = findIncrementingColumns(table
        .getColumns());
      if (incrementingColumns.length > 0)
      {
        addLint(table, new Lint("incrementing columns", incrementingColumns)
        {

          private static final long serialVersionUID = -9152369844685463520L;

          @Override
          public String getLintValueAsString()
          {
            final List<String> columnNames = new ArrayList<String>();
            for (final Column column: incrementingColumns)
            {
              columnNames.add(column.getName());
            }
            return ObjectToString.toString(columnNames);
          }
        });
      }
    }
  }

  private Column[] findIncrementingColumns(final Column[] columns)
  {
    if (columns == null || columns.length <= 1)
    {
      return new Column[0];
    }

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

    final HashSet<String> columnNameBases = new HashSet<String>(incrementingColumnsMap
      .keySet());
    for (final String columnNameBase: columnNameBases)
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

}
