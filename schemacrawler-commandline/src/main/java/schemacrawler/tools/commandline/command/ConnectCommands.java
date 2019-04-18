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

package schemacrawler.tools.commandline.command;


import static java.util.Objects.requireNonNull;

import java.util.logging.Level;

import picocli.CommandLine;
import schemacrawler.tools.commandline.AvailableServers;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public class ConnectCommands
{
  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ConnectCommands.class.getName());

  @CommandLine.Command(description = "List available SchemaCrawler database plugins")
  public static void servers()
  {
    LOGGER.log(Level.INFO, "servers");

    for (String server : new AvailableServers())
    {
      System.out.println(server);
    }
  }

  private final SchemaCrawlerShellState state;

  public ConnectCommands(final SchemaCrawlerShellState state)
  {
    this.state = requireNonNull(state, "No state provided");
  }


  @CommandLine.Command(description = "Disconnect from a database")
  public void disconnect()
  {
    LOGGER.log(Level.INFO, "disconnect");

    state.disconnect();
  }

  @CommandLine.Command(description = "Connect to a database, using a connection URL specification")
  public boolean isConnected()
  {
    final boolean isConnected = state.isConnected();
    LOGGER.log(Level.INFO, new StringFormat("isConnected=%b", isConnected));
    return isConnected;
  }

  @CommandLine.Command(description = "Disconnect from a database, and clear loaded catalog")
  public void sweep()
  {
    LOGGER.log(Level.INFO, "sweep");

    state.sweep();
  }

}
