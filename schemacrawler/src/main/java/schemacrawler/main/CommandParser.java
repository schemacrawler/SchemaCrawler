/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.main;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.tools.Command;
import sf.util.CommandLineParser;
import sf.util.Utilities;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
final class CommandParser
{

  private static final String OPTION_COMMAND = "command";

  /**
   * Parses the command line.
   * 
   * @param args
   *        Command line arguments
   * @return Command line options
   * @throws SchemaCrawlerException
   */
  static List<Command> parseCommands(final String[] args)
    throws SchemaCrawlerException
  {
    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

    final String commandOptionValue = parser
      .getStringOptionValue(OPTION_COMMAND);
    if (Utilities.isBlank(commandOptionValue))
    {
      throw new SchemaCrawlerException("No SchemaCrawler command specified");
    }
    final String[] commandStrings = commandOptionValue.split(",");

    final List<Command> commands = new ArrayList<Command>(commandStrings.length);
    for (final String commandString: commandStrings)
    {
      commands.add(new Command(commandString));
    }

    return commands;
  }

  private static CommandLineParser createCommandLineParser()
  {
    final CommandLineParser parser = new CommandLineParser();
    parser
      .addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_COMMAND, ""));
    return parser;
  }

  private CommandParser()
  {

  }

}
