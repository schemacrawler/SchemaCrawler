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

import java.util.logging.Level;
import java.util.logging.Logger;
import com.theokanning.openai.model.Model;
import com.theokanning.openai.service.OpenAiService;
import schemacrawler.tools.command.chatgpt.options.ChatGPTCommandOptions;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;

/** SchemaCrawler command plug-in. */
public final class ChatGPTCommand extends BaseSchemaCrawlerCommand<ChatGPTCommandOptions> {

  private static final Logger LOGGER = Logger.getLogger(ChatGPTCommand.class.getName());

  static final String COMMAND = "chatgpt";

  protected ChatGPTCommand() {
    super(COMMAND);
  }

  @Override
  public void checkAvailability() throws RuntimeException {
    // Check that OpenAI API key works, and the model is available
    final OpenAiService service = new OpenAiService(this.commandOptions.getApiKey());
    final Model model = service.getModel(this.commandOptions.getModel());
    LOGGER.log(Level.CONFIG, String.format("Using ChatGPT model:%n%s", model));
  }

  @Override
  public void execute() {
    try (ChatGPTConsole chatGPTConsole =
        new ChatGPTConsole(this.commandOptions, this.catalog, this.connection); ) {
      chatGPTConsole.console();
    }
  }

  @Override
  public boolean usesConnection() {
    // Support commands that use connections
    return true;
  }
}
