/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.tools.analysis.associations;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Table;
import sf.util.Multimap;
import sf.util.ObjectToString;
import sf.util.StringFormat;
import sf.util.Utility;

final class TableMatchKeys
{

  private static final Logger LOGGER = Logger
    .getLogger(TableMatchKeys.class.getName());

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
      LOGGER.log(Level.FINE, new StringFormat("Table prefixes=%s", prefixes));
      LOGGER.log(Level.FINE,
                 new StringFormat("Table matches map: %s",
                                  ObjectToString.toString(tableKeys)));
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
        if (!isBlank(commonPrefix) && commonPrefix.endsWith("_"))
        {
          final List<String> splitCommonPrefixes = new ArrayList<>();
          final String[] splitPrefix = commonPrefix.split("_");
          if (splitPrefix != null && splitPrefix.length > 0)
          {
            for (int k = 0; k < splitPrefix.length; k++)
            {
              final StringBuilder buffer = new StringBuilder(1024);
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
    Collections.sort(keySet, (o1, o2) -> {
      int comparison = 0;
      comparison = o2.length() - o1.length();
      if (comparison == 0)
      {
        comparison = o2.compareTo(o1);
      }
      return comparison;
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
    Collections.sort(prefixesList,
                     (o1, o2) -> o1.getValue().compareTo(o2.getValue()));

    // Reduce the number of prefixes in use
    final List<String> prefixes = new ArrayList<>();
    for (int i = 0; i < prefixesList.size(); i++)
    {
      final boolean add = i < 5 || prefixesList.get(i)
        .getValue() > prefixesMap.size() * 0.5;
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
