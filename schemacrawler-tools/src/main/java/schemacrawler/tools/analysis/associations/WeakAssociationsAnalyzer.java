/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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
package schemacrawler.tools.analysis.associations;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import sf.util.Multimap;
import sf.util.ObjectToString;
import sf.util.Utility;

final class WeakAssociationsAnalyzer
{

  private static final Logger LOGGER = Logger
    .getLogger(WeakAssociationsAnalyzer.class.getName());

  private final List<Table> tables;
  private final WeakAssociationsCollector collector;

  WeakAssociationsAnalyzer(final List<Table> tables,
                           final WeakAssociationsCollector collector)
  {
    this.tables = tables;
    this.collector = collector;
  }

  void analyzeTables()
  {
    if (tables == null || tables.size() < 3)
    {
      return;
    }

    final Collection<String> prefixes = findTableNamePrefixes(tables);
    final Multimap<String, Table> tableMatchMap = mapTableNameMatches(tables,
                                                                      prefixes);
    if (LOGGER.isLoggable(Level.FINE))
    {
      LOGGER.log(Level.FINE, "Table prefixes=" + prefixes);
      LOGGER.log(Level.FINE,
                 "Table matches map:" + ObjectToString.toString(tableMatchMap));
    }

    final Map<String, ForeignKeyColumnMap> fkColumnsMap = mapForeignKeyColumns(tables);

    findWeakAssociations(tables, tableMatchMap, fkColumnsMap);
  }

  private void addWeakAssociation(final Column fkColumn, final Column pkColumn)
  {
    LOGGER.log(Level.FINE,
               String.format("Found weak association: %s --> %s",
                             fkColumn.getFullName(),
                             pkColumn.getFullName()));
    if (collector != null)
    {
      final ColumnMap weakAssociation = new WeakAssociation(pkColumn, fkColumn);
      collector.addWeakAssociation(weakAssociation);
    }
  }

  /**
   * Finds table prefixes. A prefix ends with "_".
   * 
   * @param tables
   *        Tables
   * @return Table name prefixes
   */
  private Collection<String> findTableNamePrefixes(final List<Table> tables)
  {
    final SortedMap<String, Integer> prefixesMap = new TreeMap<String, Integer>();
    for (int i = 0; i < tables.size(); i++)
    {
      for (int j = i + 1; j < tables.size(); j++)
      {
        final String table1 = tables.get(i).getName();
        final String table2 = tables.get(j).getName();
        final String commonPrefix = Utility.commonPrefix(table1, table2);
        if (!Utility.isBlank(commonPrefix) && commonPrefix.endsWith("_"))
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

      @Override
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

      @Override
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

  private void findWeakAssociations(final List<Table> tables,
                                    final Multimap<String, Table> tableMatchMap,
                                    final Map<String, ForeignKeyColumnMap> fkColumnsMap)
  {
    for (final Table table: tables)
    {
      final Map<String, Column> columnNameMatchesMap = mapColumnNameMatches(table);

      for (final Map.Entry<String, Column> columnEntry: columnNameMatchesMap
        .entrySet())
      {
        final String matchColumnName = columnEntry.getKey();
        final List<Table> matchedTables = tableMatchMap.get(matchColumnName);
        if (matchedTables != null)
        {
          for (Table matchedTable: matchedTables)
          {
            final Column fkColumn = columnEntry.getValue();
            if (matchedTable != null && fkColumn != null
                && !fkColumn.getParent().equals(matchedTable))
            {
              // Check if the table association is already expressed as
              // a
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
                    addWeakAssociation(fkColumn, pkColumn);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private Map<String, Column> mapColumnNameMatches(final Table table)
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

  private Map<String, ForeignKeyColumnMap> mapForeignKeyColumns(final List<Table> tables)
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

  private Multimap<String, Table> mapTableNameMatches(final List<Table> tables,
                                                      final Collection<String> prefixes)
  {
    final Multimap<String, Table> matchMap = new Multimap<String, Table>();
    for (final Table table: tables)
    {
      for (final String prefix: prefixes)
      {
        String matchTableName = table.getName().toLowerCase();
        if (matchTableName.startsWith(prefix))
        {
          matchTableName = matchTableName.substring(prefix.length());
          matchTableName = Inflection.singularize(matchTableName);
          matchMap.add(matchTableName, table);
        }
      }
    }
    matchMap.remove("");
    return matchMap;
  }

}
