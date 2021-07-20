/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.loader.weakassociations;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.commonPrefix;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import us.fatehi.utility.Multimap;
import us.fatehi.utility.string.ObjectToStringFormat;
import us.fatehi.utility.string.StringFormat;

final class PrefixMatches {

  private static final Logger LOGGER = Logger.getLogger(PrefixMatches.class.getName());

  private final List<String> keys;
  private final String keySeparator;
  private final Multimap<String, String> keyPrefixes;

  PrefixMatches(final List<String> keys, final String keySeparator) {
    this.keys = requireNonNull(keys, "No keys provided");
    this.keySeparator = requireNonNull(keySeparator, "No key separator provided");
    keyPrefixes = new Multimap<>();

    analyze();
  }

  public List<String> get(final String key) {
    return keyPrefixes.get(key);
  }

  @Override
  public String toString() {
    return keyPrefixes.toString();
  }

  private void analyze() {
    if (keys.isEmpty()) {
      return;
    }

    final Collection<String> prefixes = findPrefixes(keys);
    mapPrefixes(keys, prefixes);

    LOGGER.log(Level.FINE, new StringFormat("Key prefixes=%s", prefixes));
    LOGGER.log(
        Level.FINE, new StringFormat("Key matches map: %s", new ObjectToStringFormat(keyPrefixes)));
  }

  /**
   * Finds key prefixes. Prefixes are separated by a separator character.
   *
   * @param keys Keys
   * @return Key name prefixes
   */
  private Collection<String> findPrefixes(final List<String> keys) {
    final SortedMap<String, Integer> prefixesMap = new TreeMap<>();
    for (int i = 0; i < keys.size(); i++) {
      for (int j = i + 1; j < keys.size(); j++) {
        final String key1 = keys.get(i);
        final String key2 = keys.get(j);
        final String commonPrefix = commonPrefix(key1, key2);
        if (isBlank(commonPrefix)) {
          continue;
        }

        final List<String> splitCommonPrefixes = new ArrayList<>();
        final String[] splitPrefix = commonPrefix.split(keySeparator);
        if (splitPrefix != null && splitPrefix.length > 0) {
          for (int k = 0; k < splitPrefix.length; k++) {
            final StringBuilder buffer = new StringBuilder(1024);
            for (int l = 0; l < k; l++) {
              buffer.append(splitPrefix[l]).append(keySeparator);
            }
            if (buffer.length() > 0) {
              splitCommonPrefixes.add(buffer.toString());
            }
          }
        }
        if (commonPrefix.endsWith(keySeparator)) {
          splitCommonPrefixes.add(commonPrefix);
        }

        for (final String splitCommonPrefix : splitCommonPrefixes) {
          final int prevCount;
          if (prefixesMap.containsKey(splitCommonPrefix)) {
            prevCount = prefixesMap.get(splitCommonPrefix);
          } else {
            prevCount = 0;
          }
          prefixesMap.put(splitCommonPrefix, prevCount + 1);
        }
      }
    }

    // Make sure we have the smallest prefixes
    final List<String> keySet = new ArrayList<>(prefixesMap.keySet());
    keySet.sort(
        (key1, key2) -> {
          int comparison = 0;
          comparison = key2.length() - key1.length();
          if (comparison == 0) {
            comparison = key2.compareTo(key1);
          }
          return comparison;
        });
    for (int i = 0; i < keySet.size(); i++) {
      for (int j = i + 1; j < keySet.size(); j++) {
        final String longPrefix = keySet.get(i);
        if (longPrefix.startsWith(keySet.get(j))) {
          prefixesMap.remove(longPrefix);
          break;
        }
      }
    }

    // Sort prefixes by the number of keys using them, in descending
    // order
    final List<Map.Entry<String, Integer>> prefixesList = new ArrayList<>(prefixesMap.entrySet());
    Collections.sort(
        prefixesList, (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));

    // Reduce the number of prefixes in use
    final List<String> prefixes = new ArrayList<>();
    for (int i = 0; i < prefixesList.size(); i++) {
      final boolean add = i < 5 || prefixesList.get(i).getValue() > prefixesMap.size() * 0.5;
      if (add) {
        prefixes.add(prefixesList.get(i).getKey());
      }
    }
    prefixes.add("");

    return prefixes;
  }

  private void mapPrefixes(final List<String> keys, final Collection<String> prefixes) {
    for (final String key : keys) {
      for (final String prefix : prefixes) {
        String matchKeyName = key.toLowerCase();
        if (matchKeyName.startsWith(prefix)) {
          matchKeyName = matchKeyName.substring(prefix.length());
          matchKeyName = Inflection.singularize(matchKeyName);
          if (!isBlank(matchKeyName)) {
            keyPrefixes.add(key, matchKeyName);
          }
        }
      }
    }
  }
}
