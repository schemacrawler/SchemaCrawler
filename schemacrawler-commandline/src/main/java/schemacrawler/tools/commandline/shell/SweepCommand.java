/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.util.Objects.requireNonNull;

import java.util.logging.Level;

import picocli.CommandLine.Command;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import sf.util.SchemaCrawlerLogger;

@Command(name = "sweep", aliases = { "clean" }, header = "** Disconnect from a database, and clear loaded catalog", headerHeading = "", synopsisHeading = "Shell Command:%n", customSynopsis = {
  "sweep"
}, optionListHeading = "Options:%n")
public class SweepCommand
  implements Runnable
{
  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(SweepCommand.class.getName());

  private final SchemaCrawlerShellState state;

  public SweepCommand(final SchemaCrawlerShellState state)
  {
    this.state = requireNonNull(state, "No state provided");
  }

  @Override
  public void run()
  {
    LOGGER.log(Level.INFO, "sweep");
    state.sweep();
  }

}
