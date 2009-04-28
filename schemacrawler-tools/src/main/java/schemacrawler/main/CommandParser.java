/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import schemacrawler.tools.Command;
import schemacrawler.tools.Commands;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import sf.util.Utilities;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
final class CommandParser
  extends BaseOptionsParser<Commands>
{

  private final StringOption optionCommand = new StringOption(Option.NO_SHORT_FORM,
                                                              "command",
                                                              "");

  CommandParser(final String[] args)
  {
    super(args);
  }

  @Override
  protected Commands getOptions()
  {
    parse(new Option[] {
      optionCommand
    });

    final String commandOptionValue = optionCommand.getValue();
    if (Utilities.isBlank(commandOptionValue))
    {
      return new Commands();
    }
    final String[] commandStrings = commandOptionValue.split(",");

    final Commands commands = new Commands();
    for (final String commandString: commandStrings)
    {
      SchemaTextDetailType schemaTextDetailType;
      try
      {
        schemaTextDetailType = SchemaTextDetailType.valueOf(commandString);
      }
      catch (final IllegalArgumentException e)
      {
        schemaTextDetailType = null;
      }
      final boolean isQuery = schemaTextDetailType == null;
      commands.add(new Command(commandString, isQuery));
    }

    return commands;
  }

  @Override
  protected String getHelpResource()
  {
    return "/help/Commands.readme.txt";
  }

}
