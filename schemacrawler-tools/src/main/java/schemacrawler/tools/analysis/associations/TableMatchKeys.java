/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package schemacrawler.tools.analysis.associations;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Table;
import sf.util.Multimap;
import sf.util.ObjectToString;
import sf.util.Utility;

final class TableMatchKeys
{

  private static final Logger LOGGER = Logger.getLogger(TableMatchKeys.class
    .getName());

  private final List<Table> tables;

  private final Multimap<Table, String> tableKeys;

  TableMatchKeys(final List<Table> tables)
  {
    this.tables = requireNonNull(tables);
    tableKeys = new Multimap<>();

    analyzeTables();
  }

  public List<String> get(final Table table)
  {
    return tableKeys.get(table);
  }

  @Override
  public String toString()
  {
    return tableKeys.toString();
  }

  private void analyzeTables()
  {
    if (tables.isEmpty())
    {
      return;
    }

    final Collection<String> prefixes = findTableNamePrefixes(tables);
    mapTableNameMatches(tables, prefixes);
    if (LOGGER.isLoggable(Level.FINE))
    {
      LOGGER.log(Level.FINE, "Table prefixes=" + prefixes);
      LOGGER.log(Level.FINE,
                 "Table matches map:" + ObjectToString.toString(tableKeys));
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
    final SortedMap<String, Integer> prefixesMap = new TreeMap<>();
    for (int i = 0; i < tables.size(); i++)
    {
      for (int j = i + 1; j < tables.size(); j++)
      {
        final String table1 = tables.get(i).getName();
        final String table2 = tables.get(j).getName();
        final String commonPrefix = Utility.commonPrefix(table1, table2);
        if (!Utility.isBlank(commonPrefix) && commonPrefix.endsWith("_"))
        {
          final List<String> splitCommonPrefixes = new ArrayList<>();
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
    final List<String> keySet = new ArrayList<>(prefixesMap.keySet());
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
    final List<Map.Entry<String, Integer>> prefixesList = new ArrayList<>(prefixesMap
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
    final List<String> prefixes = new ArrayList<>();
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

  private void mapTableNameMatches(final List<Table> tables,
                                   final Collection<String> prefixes)
  {
    for (final Table table: tables)
    {
      for (final String prefix: prefixes)
      {
        String matchTableName = table.getName().toLowerCase();
        if (matchTableName.startsWith(prefix))
        {
          matchTableName = matchTableName.substring(prefix.length());
          matchTableName = Inflection.singularize(matchTableName);
          if (!isBlank(matchTableName))
          {
            tableKeys.add(table, matchTableName);
          }
        }
      }
    }
  }

}
