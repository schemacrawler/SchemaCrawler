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

package schemacrawler.tools.commandline.command;

import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.tools.executable.CommandRegistry;
import us.fatehi.utility.property.PropertyName;

public class AvailableCommands extends BaseAvailableCommandDescriptions {

  private static Collection<PropertyName> availableCommands() {
    final Collection<PropertyName> supportedCommands =
        new ArrayList<>(CommandRegistry.getCommandRegistry().getCommandDescriptions());
    // Add meta-commands
    supportedCommands.add(
        new PropertyName(
            "<query_name>",
            "Shows results of query <query_name>, "
                + "as specified in the configuration properties file"));
    supportedCommands.add(
        new PropertyName(
            "<query>",
            String.join(
                "\n",
                "Shows results of SQL <query>",
                "The query itself can contain the variables ${table}, ${columns} "
                    + "and ${tabletype}, or system properties referenced as ${<system-property-name>}",
                "Queries without any variables are executed exactly once",
                "Queries with variables are executed once for each table, "
                    + "with the variables substituted")));
    return supportedCommands;
  }

  public AvailableCommands() {
    super(availableCommands());
  }

  @Override
  protected String getName() {
    return CommandRegistry.getCommandRegistry().getName();
  }
}
