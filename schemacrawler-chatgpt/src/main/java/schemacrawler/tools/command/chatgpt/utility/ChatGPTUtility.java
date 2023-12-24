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

package schemacrawler.tools.command.chatgpt.utility;

import static java.util.Objects.requireNonNull;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import com.theokanning.openai.completion.chat.ChatFunction;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.FunctionExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.FunctionDefinition;
import schemacrawler.tools.command.chatgpt.FunctionDefinition.FunctionType;
import schemacrawler.tools.command.chatgpt.FunctionParameters;
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.tools.command.chatgpt.functions.ExitFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.FunctionDefinitionRegistry;
import schemacrawler.tools.command.chatgpt.functions.NoFunctionParameters;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class ChatGPTUtility {

  public static boolean isExitCondition(final List<ChatMessage> completions) {
    requireNonNull(completions, "No completions provided");
    final String exitFunctionName = new ExitFunctionDefinition().getName();
    for (final ChatMessage c : completions) {
      if (c.getFunctionCall() != null && c.getName().equals(exitFunctionName)) {
        return true;
      }
    }
    return false;
  }

  public static FunctionExecutor newFunctionExecutor(
      final Catalog catalog, final Connection connection) {

    requireNonNull(catalog, "No catalog provided");
    requireNonNull(connection, "No connection provided");

    final List<ChatFunction> chatFunctions = new ArrayList<>();
    for (final FunctionDefinition functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry()) {
      if (functionDefinition.getFunctionType() != FunctionType.USER) {
        continue;
      }
      functionDefinition.setCatalog(catalog);
      functionDefinition.setConnection(connection);
      final ChatFunction chatFunction =
          ChatFunction.builder()
              .name(functionDefinition.getName())
              .description(functionDefinition.getDescription())
              .executor(functionDefinition.getParameters(), functionDefinition.getExecutor())
              .build();
      chatFunctions.add(chatFunction);
    }
    return new FunctionExecutor(chatFunctions);
  }

  /**
   * Send prompt to ChatGPT API and display response
   *
   * @param prompt Input prompt.
   */
  public static void printResponse(final List<ChatMessage> completions, final PrintStream out) {
    requireNonNull(out, "No ouput stream provided");
    requireNonNull(completions, "No completions provided");
    completions.stream()
        .forEach(
            c -> {
              out.println(c.getContent());
            });
  }

  public static List<ChatMessage> systemMessages(
      final Catalog catalog, final Connection connection) {

    requireNonNull(catalog, "No catalog provided");
    requireNonNull(connection, "No connection provided");

    final List<ChatMessage> systemMessages = new ArrayList<>();
    new ArrayList<>();
    for (final FunctionDefinition<FunctionParameters> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry()) {
      if (functionDefinition.getFunctionType() != FunctionType.SYSTEM) {
        continue;
      }
      functionDefinition.setCatalog(catalog);
      functionDefinition.setConnection(connection);
      final FunctionReturn functionReturn =
          functionDefinition.getExecutor().apply(new NoFunctionParameters());
      final ChatMessage systemMessage =
          new ChatMessage(ChatMessageRole.SYSTEM.value(), functionReturn.get());
      systemMessages.add(systemMessage);
    }
    return systemMessages;
  }

  private ChatGPTUtility() {
    // Prevent instantiation
  }
}
