package schemacrawler.tools.text;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import schemacrawler.schemacrawler.Options;
import sf.util.Utility;

public final class Commands
  implements Options, Iterable<String>
{

  private static final long serialVersionUID = -3450943894546747834L;

  private final List<String> commands;

  public Commands()
  {
    commands = new ArrayList<String>();
  }

  public Commands(final String commandsList)
  {
    this();
    if (!Utility.isBlank(commandsList))
    {
      final String[] commandStrings = commandsList.split(",");
      commands.addAll(Arrays.asList(commandStrings));
    }
  }

  public void add(final String command)
  {
    if (!Utility.isBlank(command))
    {
      commands.add(command);
    }
  }

  public boolean isEmpty()
  {
    return commands.isEmpty();
  }

  public boolean isFirstCommand(final String command)
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

  public boolean isLastCommand(final String command)
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

  public Iterator<String> iterator()
  {
    return commands.iterator();
  }

  public int size()
  {
    return commands.size();
  }

  @Override
  public String toString()
  {
    return commands.toString();
  }

}
