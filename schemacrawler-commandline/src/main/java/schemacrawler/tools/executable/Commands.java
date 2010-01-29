package schemacrawler.tools.executable;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import schemacrawler.schemacrawler.Options;
import sf.util.Utility;

final class Commands
  implements Options, Iterable<String>
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

  public Iterator<String> iterator()
  {
    return commands.iterator();
  }

  @Override
  public String toString()
  {
    return commands.toString();
  }

  void add(final String command)
  {
    if (!Utility.isBlank(command))
    {
      commands.add(command);
    }
  }

  boolean isEmpty()
  {
    return commands.isEmpty();
  }

  boolean isFirstCommand(final String command)
  {
    if (command != null)
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
    if (command != null)
    {
      final String lastCommand = commands.get(commands.size() - 1);
      return lastCommand.equals(command);
    }
    else
    {
      return false;
    }
  }

  int size()
  {
    return commands.size();
  }

}
