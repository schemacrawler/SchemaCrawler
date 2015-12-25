/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static sf.util.Utility.isBlank;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.Command;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class CommandParser
  extends BaseOptionsParser<Command>
{

  public CommandParser(final Config config)
  {
    super(config);
    normalizeOptionName("command", "c");
  }

  @Override
  public Command getOptions()
    throws SchemaCrawlerException
  {
    final String command = config.getStringValue("command", null);
    if (!isBlank(command))
    {
      final Command commandOption = new Command(command);
      consumeOption("command");
      return commandOption;
    }
    else
    {
      throw new SchemaCrawlerCommandLineException("No command specified");
    }
  }

  public boolean hasOptions()
  {
    final String command = config.getStringValue("command", null);
    return !isBlank(command);
  }

}
