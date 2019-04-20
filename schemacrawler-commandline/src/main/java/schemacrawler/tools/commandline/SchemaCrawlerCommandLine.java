/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static us.fatehi.commandlineparser.CommandLineUtility.runCommand;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.tools.commandline.command.*;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import sf.util.SchemaCrawlerLogger;
import us.fatehi.commandlineparser.CommandLineUtility;

/**
 * Utility for parsing the SchemaCrawler command-line.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerCommandLine
  implements CommandLine
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaCrawlerCommandLine.class.getName());

  private final SchemaCrawlerShellState state;

  public SchemaCrawlerCommandLine(final String[] args)
  {

    if (args == null)
    {
      throw new SchemaCrawlerRuntimeException(
        "No command-line arguments provided");
    }

    state = new SchemaCrawlerShellState();

    final Config argsMap = CommandLineUtility.parseArgs(args);
    state.setAdditionalConfiguration(argsMap);

    runCommand(new ConfigFileCommand(state), args);
    runCommand(new ConnectCommand(state), args);

    runCommand(new FilterCommand(state), args);
    runCommand(new GrepCommand(state), args);
    runCommand(new LimitCommand(state), args);

    runCommand(new ShowCommand(state), args);
    runCommand(new SortCommand(state), args);

    runCommand(new LoadCommand(state), args);
    runCommand(new ExecuteCommand(state), args);
  }

  @Override
  public void execute()
    throws Exception
  {
  }

}
