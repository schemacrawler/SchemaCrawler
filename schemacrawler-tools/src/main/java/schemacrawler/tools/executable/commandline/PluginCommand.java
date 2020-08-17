/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.executable.commandline;


import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.executable.commandline.PluginCommandType.command;
import static schemacrawler.tools.executable.commandline.PluginCommandType.server;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;

public class PluginCommand
  implements Iterable<PluginCommandOption>
{

  public static PluginCommand empty()
  {
    return new PluginCommand(command, null, null, null, null, null);
  }

  public static PluginCommand newPluginCommand(final String name,
                                               final String helpHeader)
  {
    return new PluginCommand(command, name, helpHeader, null, null, null);
  }

  public static PluginCommand newDatabasePluginCommand(final String name,
                                                       final String helpHeader)
  {
    return new PluginCommand(server, name, helpHeader, null, null, null);
  }

  public static PluginCommand newPluginCommand(final String name,
                                               final String helpHeader,
                                               final String helpDescription)
  {
    return new PluginCommand(command,
                             name,
                             helpHeader,
                             helpDescription,
                             null,
                             null);
  }

  public static PluginCommand newPluginCommand(final String name,
                                               final String helpHeader,
                                               final String helpDescription,
                                               final Supplier<String> helpFooter)
  {
    return new PluginCommand(command,
                             name,
                             helpHeader,
                             helpDescription,
                             helpFooter,
                             null);
  }

  private final PluginCommandType type;
  private final String helpDescription;
  private final String helpHeader;
  private final String name;
  private final Supplier<String> helpFooter;
  private final Collection<PluginCommandOption> options;

  private PluginCommand(final PluginCommandType type,
                        final String name,
                        final String helpHeader,
                        final String helpDescription,
                        final Supplier<String> helpFooter,
                        final Collection<PluginCommandOption> options)
  {
    this.type = requireNonNull(type, "No plugin command type provided");
    if (options == null)
    {
      this.options = new ArrayList<>();
    }
    else
    {
      this.options = new HashSet<>(options);
    }

    if (isBlank(name) && !this.options.isEmpty())
    {
      throw new IllegalArgumentException("No command name provided");
    }
    this.name = name;

    if (isBlank(helpHeader))
    {
      this.helpHeader = null;
    }
    else
    {
      this.helpHeader = helpHeader;
    }

    if (isBlank(helpDescription))
    {
      this.helpDescription = null;
    }
    else
    {
      this.helpDescription = helpDescription;
    }

    this.helpFooter = helpFooter;
  }

  public Supplier<String> getHelpFooter()
  {
    return helpFooter;
  }

  public boolean hasHelpFooter()
  {
    return helpFooter != null;
  }

  public String getHelpDescription()
  {
    return helpDescription;
  }

  public String getHelpHeader()
  {
    return helpHeader;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof PluginCommand))
    {
      return false;
    }
    final PluginCommand that = (PluginCommand) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public String toString()
  {
    return new StringJoiner(", ",
                            PluginCommand.class.getSimpleName() + "[",
                            "]")
      .add("name='" + name + "'")
      .add("options=" + options)
      .toString();
  }

  @Override
  public Iterator<PluginCommandOption> iterator()
  {
    return options.iterator();
  }

  public boolean isEmpty()
  {
    return isBlank(name) && options.isEmpty();
  }

  public PluginCommand addOption(final String name,
                                 final String helpText,
                                 final Class<?> valueClass)
  {
    final PluginCommandOption option =
      new PluginCommandOption(name, helpText, valueClass);
    if (option != null)
    {
      options.add(option);
    }
    return this;
  }

  public String getName()
  {
    return type.toPluginCommandName(name);
  }

}
