package schemacrawler.tools;


import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public final class Command
  implements Options
{

  private static final long serialVersionUID = -3450943894546747834L;

  private final String command;
  private final String commandExecutableClassName;

  public Command()
  {
    this(null);
  }

  public Command(final String command)
  {
    this.command = command;

    String commandExecutableClassName;
    try
    {
      commandExecutableClassName = CommandRegistry
        .lookupCommandExecutableClassName(command);
    }
    catch (final SchemaCrawlerException e)
    {
      commandExecutableClassName = null;
    }
    this.commandExecutableClassName = commandExecutableClassName;
  }

  public String getCommandExecutableClassName()
  {
    return commandExecutableClassName;
  }

  @Override
  public String toString()
  {
    return command;
  }

}
