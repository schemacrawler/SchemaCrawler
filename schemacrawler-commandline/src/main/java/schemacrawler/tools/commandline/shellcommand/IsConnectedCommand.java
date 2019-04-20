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

package schemacrawler.tools.commandline.shellcommand;


import static java.util.Objects.requireNonNull;

import picocli.CommandLine;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import sf.util.SchemaCrawlerLogger;

@CommandLine.Command(name = "is-connected", description = "Connect to a database, using a connection URL specification")
public class IsConnectedCommand
  implements Runnable
{
  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(IsConnectedCommand.class.getName());

  private final SchemaCrawlerShellState state;

  public IsConnectedCommand(final SchemaCrawlerShellState state)
  {
    this.state = requireNonNull(state, "No state provided");
  }

  public void run()
  {
    final boolean isConnected = state.isConnected();
    System.out.println(String.format("%sconnected", isConnected? "": "not "));
  }

}
