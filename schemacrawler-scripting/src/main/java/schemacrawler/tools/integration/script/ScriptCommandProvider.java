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
package schemacrawler.tools.integration.script;


import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.OutputOptions;

public class ScriptCommandProvider
  extends BaseCommandProvider
{

  public static final String DESCRIPTION_HEADER =
    "Process a script file, such as JavaScript, "
    + "against the database schema";

  public ScriptCommandProvider()
  {
    super(new CommandDescription(ScriptCommand.COMMAND, DESCRIPTION_HEADER));
  }

  @Override
  public SchemaCrawlerCommand newSchemaCrawlerCommand(final String command)
  {
    return new ScriptCommand();
  }

  @Override
  public boolean supportsOutputFormat(final String command,
                                      final OutputOptions outputOptions)
  {
    return true;
  }

  @Override
  public PluginCommand getCommandLineCommand()
  {
    final PluginCommand pluginCommand =
      newPluginCommand(ScriptCommand.COMMAND, "** " + DESCRIPTION_HEADER);
    pluginCommand
      .addOption("script",
                 "Path to the script file or to the CLASSPATH resource",
                 String.class)
      .addOption("script-language", "Scripting language", String.class);
    return pluginCommand;
  }

}
