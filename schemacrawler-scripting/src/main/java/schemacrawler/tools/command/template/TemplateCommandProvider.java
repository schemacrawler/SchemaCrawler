/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.template;

import static schemacrawler.tools.command.template.TemplateCommand.COMMAND;
import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;
import schemacrawler.tools.command.template.options.TemplateLanguageOptionsBuilder;
import schemacrawler.tools.command.template.options.TemplateLanguageType;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.LanguageOptions;
import schemacrawler.tools.options.OutputOptions;

public class TemplateCommandProvider extends BaseCommandProvider {

  public TemplateCommandProvider() {
    super(COMMAND);
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand = newPluginCommand(COMMAND);
    pluginCommand
        .addOption(
            "template", String.class, "Path to the template file or to the CLASSPATH resource")
        .addOption("templating-language", TemplateLanguageType.class, "Templating language");
    return pluginCommand;
  }

  @Override
  public TemplateCommand newSchemaCrawlerCommand(final String command, final Config config) {
    if (!supportsCommand(command)) {
      throw new IllegalArgumentException("Cannot support command, " + command);
    }

    final LanguageOptions toOptions =
        TemplateLanguageOptionsBuilder.builder().fromConfig(config).toOptions();

    final TemplateCommand scCommand = new TemplateCommand();
    scCommand.configure(toOptions);
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return true;
  }
}
