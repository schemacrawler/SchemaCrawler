/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.command.script;

import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;

import schemacrawler.tools.command.script.options.ScriptLanguageOptionsBuilder;
import schemacrawler.tools.command.script.options.ScriptOptions;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

public class ScriptCommandProvider extends BaseCommandProvider {

  public static final String DESCRIPTION_HEADER =
      "Process a script file, such as JavaScript, " + "against the database schema";

  public ScriptCommandProvider() {
    super(new CommandDescription(ScriptCommand.COMMAND, DESCRIPTION_HEADER));
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand =
        newPluginCommand(ScriptCommand.COMMAND, "** " + DESCRIPTION_HEADER);
    pluginCommand
        .addOption("script", String.class, "Path to the script file or to the CLASSPATH resource")
        .addOption("script-language", String.class, "Scripting language");
    return pluginCommand;
  }

  @Override
  public ScriptCommand newSchemaCrawlerCommand(final String command, final Config config) {
    final ScriptOptions scriptOptions =
        ScriptLanguageOptionsBuilder.builder().fromConfig(config).toOptions();

    final ScriptCommand scCommand = new ScriptCommand();
    scCommand.setCommandOptions(scriptOptions);
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return true;
  }
}
