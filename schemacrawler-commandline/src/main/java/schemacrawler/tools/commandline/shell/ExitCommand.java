/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.util.logging.Logger;
import picocli.CommandLine.Command;

@Command(
    name = "exit",
    aliases = {"quit", "terminate"},
    header = "** Terminate the interactive shell",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"exit"},
    optionListHeading = "Options:%n")
public class ExitCommand implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(ExitCommand.class.getName());

  @Override
  public void run() {
    LOGGER.log(Level.INFO, "exit");
    // No-op, since the shell will catch this command and exit with a status code of 0
    // This command is registered only for the help message
  }
}
