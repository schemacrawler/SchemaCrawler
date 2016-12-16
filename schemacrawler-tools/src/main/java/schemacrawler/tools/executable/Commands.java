/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.executable;


import static sf.util.Utility.isBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

final class Commands
  implements Serializable, Iterable<String>
{

  private static final long serialVersionUID = -3450943894546747834L;

  private final List<String> commands;

  Commands(final String commandsList)
  {
    commands = new ArrayList<>();
    if (!isBlank(commandsList))
    {
      final String[] commandStrings = commandsList.split(",");
      commands.addAll(Arrays.asList(commandStrings));
    }
  }

  @Override
  public Iterator<String> iterator()
  {
    return commands.iterator();
  }

  @Override
  public String toString()
  {
    return commands.toString();
  }

  boolean hasMultipleCommands()
  {
    return commands.size() > 1;
  }

  boolean isEmpty()
  {
    return commands.isEmpty();
  }

  boolean isFirstCommand(final String command)
  {
    if (command != null && !isEmpty())
    {
      final String firstCommand = commands.get(0);
      return firstCommand.equals(command);
    }
    else
    {
      return false;
    }
  }

  boolean isLastCommand(final String command)
  {
    if (command != null && !isEmpty())
    {
      final String lastCommand = commands.get(commands.size() - 1);
      return lastCommand.equals(command);
    }
    else
    {
      return false;
    }
  }

}
