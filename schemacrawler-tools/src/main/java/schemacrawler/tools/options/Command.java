package schemacrawler.tools.options;


import static sf.util.Utility.isBlank;

import schemacrawler.schemacrawler.Options;

public final class Command
  implements Options
{

  private static final long serialVersionUID = -3450943894546747834L;

  private final String command;

  public Command(final String command)
  {
    if (isBlank(command))
    {
      throw new IllegalArgumentException("No command specified");
    }
    this.command = command;
  }

  @Override
  public String toString()
  {
    return command;
  }

}
