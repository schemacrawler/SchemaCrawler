package schemacrawler.tools.lint;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;

public class LintUtility
{

  public static final List<String> columns(final ForeignKey foreignKey)
  {
    if (foreignKey == null)
    {
      return Collections.emptyList();
    }

    final List<String> columnNames = new ArrayList<String>();
    final ForeignKeyColumnMap[] columnPairs = foreignKey.getColumnPairs();
    for (final ForeignKeyColumnMap columnPair: columnPairs)
    {
      columnNames.add(columnPair.getForeignKeyColumn().getFullName());
    }
    return columnNames;
  }

  public static final List<String> columns(final Index index)
  {
    if (index == null)
    {
      return Collections.emptyList();
    }

    final List<String> columnNames = new ArrayList<String>();
    for (final IndexColumn indexColumn: index.getColumns())
    {
      columnNames.add(indexColumn.getFullName());
    }
    return columnNames;
  }

  public static final <E> boolean listStartsWith(final List<E> main,
                                                 final List<E> sub)
  {
    if (main == null || sub == null)
    {
      return false;
    }
    if (main.size() < sub.size())
    {
      return false;
    }
    if (main.isEmpty())
    {
      return true;
    }

    return main.subList(0, sub.size()).equals(sub);

  }

  private LintUtility()
  {
  }

}
