/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.serialize;

import static schemacrawler.tools.command.serialize.SerializationCommand.COMMAND;
import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;

import schemacrawler.tools.command.serialize.options.SerializationFormat;
import schemacrawler.tools.command.serialize.options.SerializationOptions;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

public class SerializationCommandProvider extends BaseCommandProvider {

  public SerializationCommandProvider() {
    super(COMMAND);
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand =
        newPluginCommand(
            COMMAND,
            () ->
                new String[] {
                  "For more information, see https://www.schemacrawler.com/serialize.html %n"
                },
            () ->
                new String[] {
                  "Deserialization is possible with the \"offline\" command for Java serialization"
                });

    return pluginCommand;
  }

  @Override
  public PluginCommand getHelpCommand() {
    final PluginCommand pluginCommand = getCommandLineCommand();
    pluginCommand.addOption(
        "output-format",
        SerializationFormat.class,
        "Supported serialization formats",
        "<output-format> is one of ${COMPLETION-CANDIDATES}",
        "Optional, inferred from the extension of the output file");
    return pluginCommand;
  }

  @Override
  public SerializationCommand newSchemaCrawlerCommand(final String command, final Config config) {
    final SerializationCommand scCommand = new SerializationCommand();
    scCommand.configure(new SerializationOptions());
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return supportsOutputFormat(command, outputOptions, SerializationFormat::isSupportedFormat);
  }
}
