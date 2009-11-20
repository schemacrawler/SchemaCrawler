package schemacrawler.tools.options;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import schemacrawler.schemacrawler.Options;

public final class Commands
  implements Options, Iterable<Command>
{

  private static final long serialVersionUID = -3450943894546747834L;

  private final List<Command> commands;

  public Commands()
  {
    commands = new ArrayList<Command>();
  }

  public void add(final Command command)
  {
    if (command != null)
    {
      commands.add(command);
    }
  }

  public boolean isFirstCommand(final Command command)
  {
    if (command != null)
    {
      final Command firstCommand = commands.get(0);
      return firstCommand.equals(command);
    }
    else
    {
      return false;
    }
  }

  public boolean isLastCommand(final Command command)
  {
    if (command != null)
    {
      final Command lastCommand = commands.get(commands.size() - 1);
      return lastCommand.equals(command);
    }
    else
    {
      return false;
    }
  }

  public Iterator<Command> iterator()
  {
    return commands.iterator();
  }

}
