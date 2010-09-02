/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.commandline;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.options.HelpOptions;
import sf.util.Utility;

/**
 * Utility for parsing the SchemaCrawler command line.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerHelpCommandLine
  implements CommandLine
{

  private static final long serialVersionUID = -3748989545708155963L;

  private static void showHelp(final String helpResource)
  {
    final String helpResourceName;
    if (sf.util.Utility.isBlank(helpResource)
        || SchemaCrawlerHelpCommandLine.class.getResource(helpResource) == null)
    {
      helpResourceName = "/help/DefaultExecutable.txt";
    }
    else
    {
      helpResourceName = helpResource;
    }
    final String helpText = Utility.readResourceFully(helpResourceName);
    System.out.println(helpText);
  }

  private final String command;

  private final HelpOptions helpOptions;

  /**
   * Shows comman line help.
   * 
   * @param args
   *        Command line arguments.
   * @param helpOptions
   *        Help options.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public SchemaCrawlerHelpCommandLine(final String[] args,
                                      final HelpOptions helpOptions)
    throws SchemaCrawlerException
  {
    if (args == null)
    {
      throw new IllegalArgumentException("No command line arguments provided");
    }

    if (helpOptions == null)
    {
      throw new SchemaCrawlerException("No help options provided");
    }
    this.helpOptions = helpOptions;

    if (args.length == 0)
    {
      command = null;
    }
    else
    {
      command = new CommandParser(args).getOptions().toString();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.commandline.CommandLine#execute()
   */
  public void execute()
    throws SchemaCrawlerException
  {
    System.out.println(helpOptions.getTitle());
    showHelp("/help/SchemaCrawler.txt");
    System.out.println();

    showHelp(helpOptions.getResourceConnections());
    showHelp("/help/SchemaCrawlerOptions.txt");
    showHelp("/help/ApplicationOptions.txt");
    final CommandRegistry commandRegistry = new CommandRegistry();
    if (command == null)
    {
      showHelp("/help/Command.txt");
      System.out.println("  Available commands are: ");
      final String[] availableCommands = commandRegistry
        .lookupAvailableCommands();
      for (final String availableCommand: availableCommands)
      {
        System.out.println("  " + availableCommand);
      }
    }
    else
    {
      final String commandExecutableClassName = commandRegistry
        .lookupCommandExecutableClassName(command);
      final String helpResource = "/help/"
                                  + commandExecutableClassName
                                    .substring(commandExecutableClassName
                                      .lastIndexOf('.') + 1) + ".txt";
      showHelp(helpResource);
    }

    System.exit(0);
  }

  public final String getCommand()
  {
    return command;
  }

}
