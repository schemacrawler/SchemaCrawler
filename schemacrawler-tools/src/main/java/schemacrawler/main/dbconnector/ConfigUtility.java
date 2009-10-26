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

package schemacrawler.main.dbconnector;


import java.util.Map;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.utility.ObjectToString;
import schemacrawler.utility.Utility;

/**
 * Configuration properties.
 * 
 * @author Sualeh Fatehi
 */
public final class ConfigUtility
{

  private static final long serialVersionUID = 8720699738076915453L;

  private static final Logger LOGGER = Logger.getLogger(ConfigUtility.class
    .getName());

  private ConfigUtility()
  {
  }

  /**
   * Gets a sub-group of properties - those that start with a given
   * prefix. The prefix is removed in the result.
   * 
   * @param prefix
   *        Prefix to group by.
   * @return Partitioned properties.
   */
  public Config partition(final Config config, final String prefix)
  {
    if (Utility.isBlank(prefix))
    {
      substituteVariables();
      return this;
    }

    final String dottedPrefix = prefix + ".";
    final Config partition = new Config();

    for (final Map.Entry<String, String> entry: entrySet())
    {
      final String key = entry.getKey();
      if (key.startsWith(dottedPrefix))
      {
        final String unprefixed = key.substring(dottedPrefix.length());
        partition.put(unprefixed, entry.getValue());
      }
    }

    partition.substituteVariables();
    return partition;
  }

  /**
   * Substitutes variables in this config.
   */
  public void substituteVariables()
  {
    for (final Map.Entry<String, String> entry: entrySet())
    {
      put(entry.getKey(), substituteVariables(entry.getValue(), this));
    }
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

}
