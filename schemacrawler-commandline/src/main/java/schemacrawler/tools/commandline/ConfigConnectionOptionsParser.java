/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;
import sf.util.Utility;

/**
 * Options for the command line.
 *
 * @author sfatehi
 */
final class ConfigConnectionOptionsParser
  extends BaseDatabaseConnectionOptionsParser
{

  /**
   * Gets a sub-group of properties - those that start with a given prefix. The prefix is removed in the result.
   *
   * @param config Config to partition
   * @param prefix Prefix to group by.
   *
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
    for (final Map.Entry<String, String> entry : config.entrySet())
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

  private final BooleanOption optionUseDefaultConnection = new BooleanOption('d',
                                                                             "default");

  private final StringOption optionConnection = new StringOption('c',
                                                                 "connection",
                                                                 null);

  /**
   * Parses the command line into options.
   *
   * @param args
   */
  ConfigConnectionOptionsParser(final String[] args, final Config config)
  {
    super(args, config);
  }

  @Override
  public ConnectionOptions getOptions()
    throws SchemaCrawlerException
  {
    parse(new Option[]{
      optionUseDefaultConnection,
      optionConnection,
      optionUser,
      optionPassword,
    });

    final String connectionName;
    if (optionUseDefaultConnection.getValue())
    {
      connectionName = config.get("defaultconnection");
    }
    else
    {
      connectionName = optionConnection.getValue();
    }

    final Map<String, String> databaseConnectionConfig = partition(config,
                                                                   connectionName);

    final ConnectionOptions connectionOptions = new DatabaseConfigConnectionOptions(databaseConnectionConfig);

    final String user = optionUser.getValue();
    if (user != null && !databaseConnectionConfig.containsKey("user"))
    {
      connectionOptions.setUser(user);
    }

    final String password = optionPassword.getValue();
    if (password != null && !databaseConnectionConfig.containsKey("password"))
    {
      connectionOptions.setPassword(password);
    }

    return connectionOptions;
  }

}
