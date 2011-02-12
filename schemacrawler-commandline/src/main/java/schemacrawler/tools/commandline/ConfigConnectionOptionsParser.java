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

package schemacrawler.tools.commandline;


import java.util.HashMap;
import java.util.Map;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConfigConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.Utility;
import sf.util.clparser.BooleanOption;
import sf.util.clparser.StringOption;

/**
 * Options for the command line.
 * 
 * @author sfatehi
 */
final class ConfigConnectionOptionsParser
  extends BaseDatabaseConnectionOptionsParser
{

  /**
   * Gets a sub-group of properties - those that start with a given
   * prefix. The prefix is removed in the result.
   * 
   * @param config
   *        Config to partition
   * @param prefix
   *        Prefix to group by.
   * @return Partitioned properties.
   */
  private static Map<String, String> partition(final Map<String, String> config,
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
   * Parses the command line into options.
   * 
   * @param args
   *        Command-line arguments
   * @param config
   *        Configuration
   */
  ConfigConnectionOptionsParser(final Config config)
  {
    super(config);
    addOption(new BooleanOption('d', "default"));
    addOption(new StringOption('c', "connection", null));
  }

  @Override
  public ConnectionOptions getOptions()
    throws SchemaCrawlerException
  {
    final String connectionName;
    if (hasOptionValue("default"))
    {
      connectionName = config.get("defaultconnection");
    }
    else
    {
      connectionName = getStringValue("connection");
    }
    if (config.isEmpty())
    {
      throw new SchemaCrawlerException(String.format("No configuration provided for connection \"%s\"",
                                                     connectionName));
    }

    final Map<String, String> databaseConnectionConfig = partition(config,
                                                                   connectionName);
    config.putAll(databaseConnectionConfig);

    final ConnectionOptions connectionOptions = new DatabaseConfigConnectionOptions(databaseConnectionConfig);

    if (hasOptionValue("user") && !databaseConnectionConfig.containsKey("user"))
    {
      connectionOptions.setUser(getStringValue("user"));
    }

    if (hasOptionValue("password")
        && !databaseConnectionConfig.containsKey("password"))
    {
      connectionOptions.setPassword(getStringValue("password"));
    }

    return connectionOptions;
  }

}
