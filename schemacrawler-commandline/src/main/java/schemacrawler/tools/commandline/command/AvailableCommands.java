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
package schemacrawler.tools.commandline.command;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.CommandRegistry;

public class AvailableCommands
  implements Iterable<String>
{

  private static List<String> availableCommands(final boolean isDescriptive)
  {
    final List<String> availableCommands = new ArrayList<>();
    try
    {
      for (final CommandDescription command : new CommandRegistry())
      {
        final String description;
        if (isDescriptive)
        {
          description = command.toString();
        }
        else
        {
          description = command.getName();
        }
        availableCommands.add(description);
      }
    }
    catch (final SchemaCrawlerException e)
    {
      throw new SchemaCrawlerRuntimeException(
        "Could not initialize command registry",
        e);
    }
    return availableCommands;
  }

  public static List<String> descriptive()
  {
    return new AvailableCommands(true).availableCommands;
  }

  private final List<String> availableCommands;

  public AvailableCommands()
  {
    this(false);
  }

  private AvailableCommands(final boolean isDescriptive)
  {
    availableCommands = availableCommands(isDescriptive);
  }

  @Override
  public Iterator<String> iterator()
  {
    return availableCommands.iterator();
  }

  public int size()
  {
    return availableCommands.size();
  }

}
