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

package schemacrawler.tools.executable;


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
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

  private static Map<String, String> loadCommandRegistry()
    throws SchemaCrawlerException
  {
    final Map<String, String> commandRegistry = new HashMap<String, String>();

    try
    {
      final ClassLoader classLoader = CommandRegistry.class.getClassLoader();
      final URL commandRegistryUrl = classLoader
        .getResource("tools.command.properties");

      final Properties commandRegistryProperties = new Properties();
      commandRegistryProperties.load(commandRegistryUrl.openStream());
      final List<String> propertyNames = (List<String>) Collections
        .list(commandRegistryProperties.propertyNames());
      for (final String commandName: propertyNames)
      {
        final String executableClassName = commandRegistryProperties
          .getProperty(commandName);
        commandRegistry.put(commandName, executableClassName);
      }
      if (commandRegistry.isEmpty())
      {
        throw new SchemaCrawlerException("Could not load base command registry");
      }
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not load base command registry",
                                       e);
    }

    try
    {
      final ServiceLoader<Executable> serviceLoader = ServiceLoader
        .load(Executable.class);
      for (final Executable executable: serviceLoader)
      {
        final String executableCommand = executable.getCommand();
        final String executableClassName = executable.getClass().getName();
        LOGGER.log(Level.FINER, "Loading executable, " + executableCommand
                                + "=" + executableClassName);
        commandRegistry.put(executableCommand, executableClassName);
      }
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not load extended command registry",
                                       e);
    }

    return commandRegistry;
  }

  private final Map<String, String> commandRegistry;

  public CommandRegistry()
    throws SchemaCrawlerException
  {
    commandRegistry = loadCommandRegistry();
  }

  Executable instantiateExecutableForCommand(final String command)
    throws SchemaCrawlerException
  {
    final String commandExecutableClassName = lookupExecutableClassName(command);
    if (commandExecutableClassName == null)
    {
      throw new SchemaCrawlerException("No executable found for command '"
                                       + command + "'");
    }

    Class<? extends Executable> commandExecutableClass;
    try
    {
      commandExecutableClass = (Class<? extends Executable>) Class
        .forName(commandExecutableClassName);
    }
    catch (final ClassNotFoundException e)
    {
      throw new SchemaCrawlerException("Could not load class "
                                       + commandExecutableClassName, e);
    }

    Executable executable;
    try
    {
      executable = commandExecutableClass.newInstance();
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.FINE, "Could not instantiate "
                             + commandExecutableClassName
                             + " using the default constructor");
      try
      {
        final Constructor<? extends Executable> constructor = commandExecutableClass
          .getConstructor(new Class[] {
            String.class
          });
        executable = constructor.newInstance(command);
      }
      catch (final Exception e1)
      {
        throw new SchemaCrawlerException("Could not instantiate executable for command '"
                                             + command + "'",
                                         e1);
      }
    }

    return executable;
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

  public String lookupExecutableClassName(final String command)
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

}
