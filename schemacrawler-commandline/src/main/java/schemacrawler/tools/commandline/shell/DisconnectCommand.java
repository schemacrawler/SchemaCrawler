/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline.shell;

import java.util.logging.Level;

import picocli.CommandLine.Command;
import java.util.logging.Logger;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;

@Command(
    name = "disconnect",
    header = "** Disconnect from a database",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"disconnect"},
    optionListHeading = "Options:%n")
public class DisconnectCommand extends BaseStateHolder implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(DisconnectCommand.class.getName());

  public DisconnectCommand(final ShellState state) {
    super(state);
  }

  @Override
  public void run() {
    LOGGER.log(Level.INFO, "disconnect");

    state.disconnect();
  }
}
