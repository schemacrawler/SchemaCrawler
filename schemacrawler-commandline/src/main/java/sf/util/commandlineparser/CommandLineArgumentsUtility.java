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
package sf.util.commandlineparser;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public class CommandLineArgumentsUtility
{

  public static String[] flattenCommandlineArgs(final Map<String, String> argsMap)
  {
    final List<String> argsList = new ArrayList<>();
    for (final Map.Entry<String, String> arg: argsMap.entrySet())
    {
      final String key = arg.getKey();
      final String value = arg.getValue();
      if (value != null)
      {
        argsList.add(String.format("-%s=%s", key, value));
      }
      else
      {
        argsList.add(String.format("-%s", key));
      }
    }
    final String[] args = argsList.toArray(new String[0]);
    return args;
  }

  /**
   * Loads configuration from a number of command-line.
   */
  public static Config loadConfig(final String[] args)
    throws SchemaCrawlerException
  {
    final CommandLineArgumentsParser argsParser = new CommandLineArgumentsParser(args);
    argsParser.parse();
    final Map<String, String> optionsMap = argsParser.getOptionsMap();
    if (!argsParser.getNonOptionArguments().isEmpty())
    {
      LOGGER.log(Level.INFO, "Too many command-line arguments provided: "
                             + argsParser.getNonOptionArguments());
    }

    // Override/ overwrite from the command-line options
    final Config config = new Config();
    config.putAll(optionsMap);
    return config;
  }

  private static final Logger LOGGER = Logger
    .getLogger(CommandLineArgumentsUtility.class.getName());

  private CommandLineArgumentsUtility()
  {
    // Prevent instantiation
  }

}
