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

package schemacrawler.tools.main;


import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
final class CommandsParser
  extends BaseOptionsParser<Commands>
{

  private final StringOption optionCommand = new StringOption(Option.NO_SHORT_FORM,
                                                              "command",
                                                              "standard_schema");

  CommandsParser(final String[] args)
  {
    super(args);
  }

  @Override
  protected String getHelpResource()
  {
    return "/help/Commands.readme.txt";
  }

  @Override
  protected Commands getOptions()
  {
    parse(new Option[] {
      optionCommand
    });

    final String commandOptionValue = optionCommand.getValue();
    final Commands commands = new Commands(commandOptionValue);
    return commands;
  }

}
