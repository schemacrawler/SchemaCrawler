/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static sf.util.Utility.isBlank;

import java.util.*;

public class PluginCommand
  implements Iterable<PluginCommandOption>
{

  public static PluginCommand empty()
  {
    return new PluginCommand(null, null);
  }

  private final String helpText;
  private final String name;
  private final Collection<PluginCommandOption> options;

  private PluginCommand(final String name,
                        final String helpText,
                        final Collection<PluginCommandOption> options)
  {
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

    if (isBlank(helpText))
    {
      this.helpText = null;
    }
    else
    {
      this.helpText = helpText;
    }
  }

  public PluginCommand(final String name, final String helpText)
  {
    this(name, helpText, null);
  }

  public String getHelpText()
  {
    return helpText;
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
  public Iterator<PluginCommandOption> iterator()
  {
    return options.iterator();
  }

  public boolean isEmpty()
  {
    return options.isEmpty();
  }

  public PluginCommand addOption(final String name,
                                 final String helpText,
                                 final Class<?> valueClass)
  {
    final PluginCommandOption option = new PluginCommandOption(name,
                                                               helpText,
                                                               valueClass);
    if (option != null)
    {
      options.add(option);
    }
    return this;
  }

  public String getName()
  {
    return name;
  }

}
