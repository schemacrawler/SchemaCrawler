package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import sf.util.Inflection;
import sf.util.ObjectToString;
import sf.util.Utility;

final class WeakAssociationsAnalyzer
{

  private static final Logger LOGGER = Logger
    .getLogger(WeakAssociationsAnalyzer.class.getName());

  private final NamedObjectList<MutableTable> tables;
  private final List<ColumnMap> weakAssociations;

  WeakAssociationsAnalyzer(final NamedObjectList<MutableTable> tables,
                           final List<ColumnMap> weakAssociations)
  {
    this.tables = tables;
    this.weakAssociations = weakAssociations;
  }

  void analyzeTables()
  {
    final Collection<String> prefixes = findTableNamePrefixes(tables);
    final Map<String, MutableTable> tableMatchMap = mapTableNameMatches(tables,
                                                                        prefixes);
    if (LOGGER.isLoggable(Level.FINE))
    {
      LOGGER.log(Level.FINE, "Table prefixes=" + prefixes);
      LOGGER.log(Level.FINE, "Table matches map:"
                             + ObjectToString.toString(tableMatchMap));
    }

    final Map<String, ForeignKeyColumnMap> fkColumnsMap = mapForeignKeyColumns(tables);

    findWeakAssociations(tables, tableMatchMap, fkColumnsMap);
  }

  /**
   * Finds table prefixes. A prefix ends with "_".
   */
  private Collection<String> findTableNamePrefixes(final NamedObjectList<MutableTable> tables)
  {
    final SortedMap<String, Integer> prefixesMap = new TreeMap<String, Integer>();
    final List<MutableTable> tablesList = tables.values();
    for (int i = 0; i < tables.size(); i++)
    {
      for (int j = i + 1; j < tables.size(); j++)
      {
        final String table1 = tablesList.get(i).getName();
        final String table2 = tablesList.get(j).getName();
        final String commonPrefix = Utility.commonPrefix(table1, table2);
        if (commonPrefix != null && !commonPrefix.equals("")
            && commonPrefix.endsWith("_"))
        {
          final List<String> splitCommonPrefixes = new ArrayList<String>();
          final String[] splitPrefix = commonPrefix.split("_");
          if (splitPrefix != null && splitPrefix.length > 0)
          {
            for (int k = 0; k < splitPrefix.length; k++)
            {
              final StringBuilder buffer = new StringBuilder();
              for (int l = 0; l < k; l++)
              {
                buffer.append(splitPrefix[l]).append("_");
              }
              if (buffer.length() > 0)
              {
                splitCommonPrefixes.add(buffer.toString());
              }
            }
          }
          splitCommonPrefixes.add(commonPrefix);

          for (final String splitCommonPrefix: splitCommonPrefixes)
          {
            final int prevCount;
            if (prefixesMap.containsKey(splitCommonPrefix))
            {
              prevCount = prefixesMap.get(splitCommonPrefix);
            }
            else
            {
              prevCount = 0;
            }
            prefixesMap.put(splitCommonPrefix, prevCount + 1);
          }
        }
      }
    }

    // Make sure we have the smallest prefixes
    final List<String> keySet = new ArrayList<String>(prefixesMap.keySet());
    Collections.sort(keySet, new Comparator<String>()
    {

      public int compare(final String o1, final String o2)
      {
        int comparison = 0;
        comparison = o2.length() - o1.length();
        if (comparison == 0)
        {
          comparison = o2.compareTo(o1);
        }
        return comparison;
      }

    });
    for (int i = 0; i < keySet.size(); i++)
    {
      for (int j = i + 1; j < keySet.size(); j++)
      {
        final String longPrefix = keySet.get(i);
        if (longPrefix.startsWith(keySet.get(j)))
        {
          prefixesMap.remove(longPrefix);
          break;
        }
      }
    }

    // Sort prefixes by the number of tables using them, in descending
    // order
    final List<Map.Entry<String, Integer>> prefixesList = new ArrayList<Map.Entry<String, Integer>>(prefixesMap
      .entrySet());
    Collections.sort(prefixesList, new Comparator<Map.Entry<String, Integer>>()
    {

      public int compare(final Entry<String, Integer> o1,
                         final Entry<String, Integer> o2)
      {
        return o1.getValue().compareTo(o2.getValue());
      }
    });

    // Reduce the number of prefixes in use
    final List<String> prefixes = new ArrayList<String>();
    for (int i = 0; i < prefixesList.size(); i++)
    {
      final boolean add = i < 5
                          || prefixesList.get(i).getValue() > prefixesMap
                            .size() * 0.5;
      if (add)
      {
        prefixes.add(prefixesList.get(i).getKey());
      }
    }
    prefixes.add("");

    return prefixes;
  }

  private void findWeakAssociations(final NamedObjectList<MutableTable> tables,
                                    final Map<String, MutableTable> tableMatchMap,
                                    final Map<String, ForeignKeyColumnMap> fkColumnsMap)
  {
    final List<MutableTable> tablesList = tables.values();
    for (final MutableTable table: tablesList)
    {
      final Map<String, Column> columnNameMatchesMap = mapColumnNameMatches(table);

      for (final Map.Entry<String, Column> columnEntry: columnNameMatchesMap
        .entrySet())
      {
        final String matchColumnName = columnEntry.getKey();
        final MutableTable matchedTable = tableMatchMap.get(matchColumnName);
        final Column fkColumn = columnEntry.getValue();
        if (matchedTable != null && fkColumn != null
            && !fkColumn.getParent().equals(matchedTable))
        {
          // Check if the table association is already expressed as a
          // foreign key
          final ForeignKeyColumnMap fkColumnMap = fkColumnsMap.get(fkColumn
            .getFullName());
          if (fkColumnMap == null
              || !fkColumnMap.getPrimaryKeyColumn().getParent()
                .equals(matchedTable))
          {
            // Ensure that we associate to the primary key
            final Map<String, Column> pkColumnNameMatchesMap = mapColumnNameMatches(matchedTable);
            final Column pkColumn = pkColumnNameMatchesMap.get("id");
            if (pkColumn != null)
            {
              final ColumnDataType fkColumnType = fkColumn.getType();
              final ColumnDataType pkColumnType = pkColumn.getType();
              if (pkColumnType != null && fkColumnType != null
                  && fkColumnType.getType() == pkColumnType.getType())
              {
                LOGGER.log(Level.FINE, String
                  .format("Found weak association: %s --> %s", fkColumn
                    .getFullName(), pkColumn.getFullName()));
                final MutableColumnMap columnMap = new MutableColumnMap(pkColumn,
                                                                        fkColumn);

                ((MutableTable) pkColumn.getParent())
                  .addWeakAssociation(columnMap);
                ((MutableTable) fkColumn.getParent())
                  .addWeakAssociation(columnMap);
                weakAssociations.add(columnMap);
              }
            }
          }
        }
      }
    }
  }

  private Map<String, Column> mapColumnNameMatches(final MutableTable table)
  {
    final Map<String, Column> matchMap = new HashMap<String, Column>();

    final PrimaryKey primaryKey = table.getPrimaryKey();
    if (primaryKey != null && primaryKey.getColumns().length == 1)
    {
      matchMap.put("id", primaryKey.getColumns()[0]);
    }

    for (final Column column: table.getColumns())
    {
      String matchColumnName = column.getName().toLowerCase();
      if (matchColumnName.endsWith("_id"))
      {
        matchColumnName = matchColumnName
          .substring(0, matchColumnName.length() - 3);
      }
      if (matchColumnName.endsWith("id") && !matchColumnName.equals("id"))
      {
        matchColumnName = matchColumnName
          .substring(0, matchColumnName.length() - 2);
      }
      matchMap.put(matchColumnName, column);
    }

    return matchMap;
  }

  private Map<String, ForeignKeyColumnMap> mapForeignKeyColumns(final NamedObjectList<MutableTable> tables)
  {
    final Map<String, ForeignKeyColumnMap> fkColumnsMap = new HashMap<String, ForeignKeyColumnMap>();
    for (final Table table: tables)
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

  private Map<String, MutableTable> mapTableNameMatches(final NamedObjectList<MutableTable> tables,
                                                        final Collection<String> prefixes)
  {
    final Map<String, MutableTable> matchMap = new HashMap<String, MutableTable>();
    for (final MutableTable table: tables)
    {
      for (final String prefix: prefixes)
      {
        String matchTableName = table.getName().toLowerCase();
        if (matchTableName.startsWith(prefix))
        {
          matchTableName = matchTableName.substring(prefix.length());
          matchTableName = Inflection.singularize(matchTableName);
          matchMap.put(matchTableName, table);
        }
      }
    }
    matchMap.remove("");
    return matchMap;
  }

}
