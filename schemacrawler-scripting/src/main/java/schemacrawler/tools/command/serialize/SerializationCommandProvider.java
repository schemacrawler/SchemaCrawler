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

package schemacrawler.tools.command.serialize;

import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;

import schemacrawler.tools.command.serialize.options.SerializationFormat;
import schemacrawler.tools.command.serialize.options.SerializationOptions;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.property.CommandDescription;

public class SerializationCommandProvider extends BaseCommandProvider {

  private static final String DESCRIPTION_HEADER = "Create an offline catalog snapshot";

  public SerializationCommandProvider() {
    super(new CommandDescription(SerializationCommand.COMMAND, DESCRIPTION_HEADER));
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand =
        newPluginCommand(
            SerializationCommand.COMMAND,
            "** " + DESCRIPTION_HEADER,
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
    scCommand.setCommandOptions(new SerializationOptions());
    return scCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return supportsOutputFormat(command, outputOptions, SerializationFormat::isSupportedFormat);
  }
}
