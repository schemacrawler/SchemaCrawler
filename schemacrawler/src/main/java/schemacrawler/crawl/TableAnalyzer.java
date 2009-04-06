package schemacrawler.crawl;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;

public class TableAnalyzer
{

  public void analyzeTables(final NamedObjectList<MutableTable> tables)
  {
    final Set<String> prefixes = findTableNamePrefixes(tables);
    System.err.println("prefixes=" + prefixes);

    final Map<String, MutableTable> tableMatchMap = mapTableNameMatches(tables,
                                                                        prefixes);
    System.err.println("table matches=" + tableMatchMap);

    final Map<String, ForeignKeyColumnMap> fkColumnsMap = mapForeignKeyColumns(tables);
    findTableAssociations(tables, tableMatchMap, fkColumnsMap);

  }

  private String commonPrefix(final String string1, final String string2)
  {
    final int index = indexOfDifference(string1, string2);
    if (index == -1)
    {
      return null;
    }
    else
    {
      return string1.substring(0, index);
    }
  }

  private void findTableAssociations(final NamedObjectList<MutableTable> tables,
                                     final Map<String, MutableTable> tableMatchMap,
                                     final Map<String, ForeignKeyColumnMap> fkColumnsMap)
  {
    final List<MutableTable> tablesList = tables.getAll();
    for (final MutableTable table: tablesList)
    {
      for (final Column column: table.getColumns())
      {
        String matchColumnName = column.getName();
        if (matchColumnName.toLowerCase().endsWith("_id"))
        {
          matchColumnName = matchColumnName.substring(0, matchColumnName
            .length() - 3);
        }
        if (matchColumnName.toLowerCase().endsWith("id"))
        {
          matchColumnName = matchColumnName.substring(0, matchColumnName
            .length() - 2);
        }
        final MutableTable matchedTable = tableMatchMap.get(matchColumnName);
        if (matchedTable != null && !column.getParent().equals(matchedTable))
        {
          final ForeignKeyColumnMap fkColumnMap = fkColumnsMap.get(column
            .getFullName());
          if (!fkColumnMap.getPrimaryKeyColumn().getParent()
            .equals(matchedTable))
          {
            System.err.println("*** " + column.getFullName() + "-->"
                               + matchedTable.getFullName());
          }
        }
      }
    }
  }

  private Set<String> findTableNamePrefixes(final NamedObjectList<MutableTable> tables)
  {
    final Set<String> prefixes = new HashSet<String>();
    final List<MutableTable> tablesList = tables.getAll();
    for (int i = 0; i < tables.size(); i++)
    {
      for (final int j = i + 1; i < tables.size(); i++)
      {
        final String table1 = tablesList.get(i).getName();
        final String table2 = tablesList.get(j).getName();
        final String commonPrefix = commonPrefix(table1, table2);
        if (commonPrefix != null && !commonPrefix.equals(""))
        {
          prefixes.add(commonPrefix);
        }
      }
    }
    prefixes.add("");
    return prefixes;
  }

  private int indexOfDifference(final String string1, final String string2)
  {
    if (string1 == string2)
    {
      return -1;
    }
    if (string1 == null || string2 == null)
    {
      return 0;
    }
    int i;
    for (i = 0; i < string1.length() && i < string2.length(); ++i)
    {
      if (string1.charAt(i) != string2.charAt(i))
      {
        break;
      }
    }
    if (i < string2.length() || i < string1.length())
    {
      return i;
    }
    return -1;
  }

  private Map<String, MutableTable> mapTableNameMatches(final NamedObjectList<MutableTable> tables,
                                                        final Set<String> prefixes)
  {
    final Map<String, MutableTable> tableMatchMap = new HashMap<String, MutableTable>();
    for (final MutableTable table: tables)
    {
      for (final String prefix: prefixes)
      {
        String matchTableName = table.getName();
        if (matchTableName.startsWith(prefix))
        {
          matchTableName = matchTableName.substring(prefix.length());
          matchTableName = Inflection.singularize(matchTableName);
          tableMatchMap.put(matchTableName, table);
        }
      }
    }
    tableMatchMap.remove("");
    return tableMatchMap;
  }

  private Map<String, ForeignKeyColumnMap> mapForeignKeyColumns(final NamedObjectList<MutableTable> tables)
  {
    final Map<String, ForeignKeyColumnMap> fkColumnsMap = new HashMap<String, ForeignKeyColumnMap>();
    for (final MutableTable table: tables)
    {
      for (final ForeignKey fk: table.getForeignKeys())
      {
        for (final ForeignKeyColumnMap fkMap: fk.getColumnPairs())
        {
          fkColumnsMap.put(fkMap.getForeignKeyColumn().getFullName(), fkMap);
        }
      }
    }
    return fkColumnsMap;
  }

}
