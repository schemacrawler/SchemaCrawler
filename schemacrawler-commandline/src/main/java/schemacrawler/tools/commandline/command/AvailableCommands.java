/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.tools.executable.CommandRegistry;
import us.fatehi.utility.property.PropertyName;

public class AvailableCommands extends BaseAvailableRegistryPlugins {

  private static Collection<PropertyName> availableCommands() {
    final Collection<PropertyName> supportedCommands =
        new ArrayList<>(CommandRegistry.getCommandRegistry().getRegisteredPlugins());
    // Add meta-commands
    supportedCommands.add(
        new PropertyName(
            "<query_name>",
            "Show results of query <query_name>, "
                + "as specified in the configuration properties file"));
    supportedCommands.add(
        new PropertyName(
            "<query>",
            String.join(
                "\n",
                "Show results of SQL <query>",
                "The query itself can contain the variables ${table}, ${columns} "
                    + "and ${tabletype}, or system properties referenced as ${<system-property-name>}",
                "Queries without any variables are executed exactly once",
                "Queries with variables are executed once for each table, "
                    + "with the variables substituted")));
    return supportedCommands;
  }

  private final String name;

  public AvailableCommands() {
    super(availableCommands());
    name = CommandRegistry.getCommandRegistry().getName();
  }

  @Override
  public String getName() {
    return name;
  }
}
