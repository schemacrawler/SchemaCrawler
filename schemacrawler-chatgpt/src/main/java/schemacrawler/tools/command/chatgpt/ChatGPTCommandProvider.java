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

package schemacrawler.tools.command.chatgpt;

import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.chatgpt.options.ChatGPTCommandOptions;
import schemacrawler.tools.command.chatgpt.options.ChatGPTCommandOptionsBuilder;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

/** SchemaCrawler command plug-in for ChatGPT. */
public class ChatGPTCommandProvider extends BaseCommandProvider {

  public static final String DESCRIPTION_HEADER = "SchemaCrawler ChatGPT integration";

  public ChatGPTCommandProvider() {
    super(new CommandDescription(ChatGPTCommand.COMMAND, DESCRIPTION_HEADER));
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand =
        newPluginCommand(ChatGPTCommand.COMMAND, "** " + DESCRIPTION_HEADER);
    pluginCommand
        .addOption("api-key", String.class, "OpenAI API key")
        .addOption(
            "api-key:env", String.class, "OpenAI API key, from an environmental variable value")
        .addOption(
            "model", String.class, "ChatGPT model", "Optional, defaults to 'chatgpt-3.5-turbo'")
        .addOption(
            "timeout",
            Integer.class,
            "Number of seconds to timeout a request if no response is received",
            "Optional, defaults to 10")
        .addOption(
            "context",
            Integer.class,
            "Number of chat messages (not tokens) to maintain as chat context",
            "Optional, defaults to 10")
        .addOption(
            "use-metadata",
            Boolean.class,
            "Allow sharing of database metadata with OpenAI to enhance chat responses",
            "Optional, defaults to false");
    return pluginCommand;
  }

  @Override
  public ChatGPTCommand newSchemaCrawlerCommand(final String command, final Config config) {
    if (!ChatGPTCommand.COMMAND.equals(command)) {
      throw new IllegalArgumentException("Cannot support command, " + command);
    }

    try {
      final ChatGPTCommandOptions options =
          ChatGPTCommandOptionsBuilder.builder().fromConfig(config).toOptions();

      final ChatGPTCommand scCommand = new ChatGPTCommand();
      scCommand.setCommandOptions(options);
      return scCommand;
    } catch (final Exception e) {
      throw new ExecutionRuntimeException(e);
    }
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return true;
  }
}
