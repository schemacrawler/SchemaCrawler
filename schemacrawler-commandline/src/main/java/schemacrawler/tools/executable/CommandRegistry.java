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

package schemacrawler.tools.executable;


import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Parses the command line.
 *
 * @author Sualeh Fatehi
 */
public final class CommandRegistry
{

  private static final Logger LOGGER = Logger.getLogger(CommandRegistry.class
    .getName());

  private final Map<String, String> commandRegistry;

  public CommandRegistry()
    throws SchemaCrawlerException
  {
    commandRegistry = loadCommandRegistry();
  }

  public String[] lookupAvailableCommands()
  {
    final Set<String> availableCommandsList = commandRegistry.keySet();
    availableCommandsList.remove("default");
    final String[] availableCommands = availableCommandsList
      .toArray(new String[availableCommandsList.size()]);
    Arrays.sort(availableCommands);
    return availableCommands;
  }

  public String lookupCommandExecutableClassName(final String command)
  {
    final String commandExecutableClassName;
    if (commandRegistry.containsKey(command))
    {
      commandExecutableClassName = commandRegistry.get(command);
    }
    else
    {
      commandExecutableClassName = commandRegistry.get("default");
    }
    return commandExecutableClassName;
  }

  private static Map<String, String> loadCommandRegistry()
    throws SchemaCrawlerException
  {
    final Map<String, String> commandRegistry = new HashMap<String, String>();
    final List<URL> commandRegistryUrls;
    try
    {
      final Enumeration<URL> resources = Thread.currentThread()
        .getContextClassLoader()
        .getResources("command.properties");
      commandRegistryUrls = Collections.list(resources);
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not load command registry");
    }
    for (final URL commandRegistryUrl : commandRegistryUrls)
    {
      try
      {
        final Properties commandRegistryProperties = new Properties();
        commandRegistryProperties.load(commandRegistryUrl.openStream());
        final List<String> propertyNames = (List<String>) Collections.list(commandRegistryProperties
          .propertyNames());
        for (String commandName : propertyNames)
        {
          final String executableClassName = commandRegistryProperties
            .getProperty(commandName);
          commandRegistry.put(commandName, executableClassName);
        }
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Could not load command registry, "
          + commandRegistryUrl, e);
      }
    }
    if (commandRegistry.isEmpty())
    {
      throw new SchemaCrawlerException("Could not load command registry");
    }
    return commandRegistry;
  }

}
