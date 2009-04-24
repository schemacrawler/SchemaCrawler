package schemacrawler.tools;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import schemacrawler.schemacrawler.Options;

public class Commands
  implements Iterable<Command>, Options
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

  public Command getFirstComand()
  {
    if (commands.size() > 0)
    {
      return commands.get(0);
    }
    else
    {
      return null;
    }
  }

  public Command get(final int index)
  {
    return commands.get(index);
  }

  public Iterator<Command> iterator()
  {
    return commands.iterator();
  }

  public int size()
  {
    return commands.size();
  }

}
