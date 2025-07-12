/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.script;

import static schemacrawler.tools.command.script.ScriptCommand.COMMAND;
import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;
import schemacrawler.tools.command.script.options.ScriptLanguageOptionsBuilder;
import schemacrawler.tools.command.script.options.ScriptOptions;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

public class ScriptCommandProvider extends BaseCommandProvider {

  public ScriptCommandProvider() {
    super(COMMAND);
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand = newPluginCommand(COMMAND);
    pluginCommand
        .addOption("script", String.class, "Path to the script file or to the CLASSPATH resource")
        .addOption("script-language", String.class, "Scripting language");
    return pluginCommand;
  }

  @Override
  public ScriptCommand newSchemaCrawlerCommand(final String command, final Config config) {
    if (!supportsCommand(command)) {
      throw new IllegalArgumentException("Cannot support command, " + command);
    }

    final ScriptOptions scriptOptions =
        ScriptLanguageOptionsBuilder.builder().fromConfig(config).toOptions();

    final ScriptCommand scCommand = new ScriptCommand();
    scCommand.configure(scriptOptions);
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return true;
  }
}
