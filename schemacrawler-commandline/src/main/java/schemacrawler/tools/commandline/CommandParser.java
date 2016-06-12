/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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

  private static final String COMMAND = "command";

  public CommandParser(final Config config)
  {
    super(config);
    normalizeOptionName(COMMAND, "c");
  }

  @Override
  public Command getOptions()
    throws SchemaCrawlerException
  {
    final String command = config.getStringValue(COMMAND, null);
    if (!isBlank(command))
    {
      final Command commandOption = new Command(command);
      consumeOption(COMMAND);
      return commandOption;
    }
    else
    {
      throw new SchemaCrawlerCommandLineException("No command specified");
    }
  }

  public boolean hasOptions()
  {
    final String command = config.getStringValue(COMMAND, null);
    return !isBlank(command);
  }

}
