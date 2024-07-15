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

import picocli.CommandLine.Command;
import schemacrawler.tools.commandline.command.AvailableCommands;

@Command(
    name = "commands",
    header = "** List available SchemaCrawler commands",
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"commands"},
    optionListHeading = "Options:%n")
public class AvailableCommandsCommand implements Runnable {

  @Override
  public void run() {
    final AvailableCommands availableCommands = new AvailableCommands();
    if (!availableCommands.isEmpty()) {
      availableCommands.print(System.out);

      System.out.println("Notes:");
      System.out.println("- For help on an individual SchemaCrawler command,");
      System.out.println("  run SchemaCrawler with options like: `-h command:schema`");
      System.out.println("  or from the SchemaCrawler interactive shell: `help command:schema`");
      System.out.println(
          "- Options for the commands should be provided with the `execute` command");
    }
  }
}
