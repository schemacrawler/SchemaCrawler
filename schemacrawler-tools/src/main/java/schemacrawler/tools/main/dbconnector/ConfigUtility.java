/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools.main.dbconnector;


import java.util.HashMap;
import java.util.Map;

import sf.util.Utility;

/**
 * Configuration properties.
 * 
 * @author Sualeh Fatehi
 */
final class ConfigUtility
{

  private static String substituteVariables(final String template,
                                            final Map<String, String> map)
  {
    if (Utility.isBlank(template) || map == null)
    {
      return template;
    }

    final String DELIMITER_START = "${";
    final String DELIMITER_END = "}";

    final StringBuilder buffer = new StringBuilder();
    int currentPosition = 0;
    int delimiterStartPosition;
    int delimiterEndPosition;

    while (true)
    {
      delimiterStartPosition = template.indexOf(DELIMITER_START,
                                                currentPosition);
      if (delimiterStartPosition == -1)
      {
        if (currentPosition == 0)
        {
          // No substitutions required at all
          return template;
        }
        // No more substitutions
        buffer.append(template.substring(currentPosition, template.length()));
        return buffer.toString();
      }
      else
      {
        buffer.append(template.substring(currentPosition,
                                         delimiterStartPosition));
        delimiterEndPosition = template.indexOf(DELIMITER_END,
                                                delimiterStartPosition);
        if (delimiterEndPosition == -1)
        {
          throw new IllegalArgumentException(template
                                             + " does not have a closing brace");
        }
        delimiterStartPosition = delimiterStartPosition
                                 + DELIMITER_START.length();
        final String key = template.substring(delimiterStartPosition,
                                              delimiterEndPosition);
        final String value = map.get(key);
        if (value != null)
        {
          buffer.append(value);
        }
        // Advance current position
        currentPosition = delimiterEndPosition + DELIMITER_END.length();
      }
    }
  }

  /**
   * Gets a sub-group of properties - those that start with a given
   * prefix. The prefix is removed in the result.
   * 
   * @param prefix
   *        Prefix to group by.
   * @return Partitioned properties.
   */
  static Map<String, String> partition(final Map<String, String> config,
                                       final String prefix)
  {
    if (Utility.isBlank(prefix))
    {
      return config;
    }

    final String dottedPrefix = prefix + ".";
    final Map<String, String> partition = new HashMap<String, String>();
    for (final Map.Entry<String, String> entry: config.entrySet())
    {
      final String key = entry.getKey();
      if (key.startsWith(dottedPrefix))
      {
        final String unprefixed = key.substring(dottedPrefix.length());
        partition.put(unprefixed, entry.getValue());
      }
    }

    return partition;
  }

  /**
   * Substitutes variables in the provided config.
   */
  static void substituteVariables(final Map<String, String> config)
  {
    for (final Map.Entry<String, String> entry: config.entrySet())
    {
      config.put(entry.getKey(), substituteVariables(entry.getValue(), config));
    }
  }

  private ConfigUtility()
  {
  }

}
