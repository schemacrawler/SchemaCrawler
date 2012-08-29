package schemacrawler.tools.executable;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import sf.util.Utility;

final class Commands
  implements Serializable, Iterable<String>
{

  private static final long serialVersionUID = -3450943894546747834L;

  private final List<String> commands;

  Commands(final String commandsList)
  {
    commands = new ArrayList<String>();
    if (!Utility.isBlank(commandsList))
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
