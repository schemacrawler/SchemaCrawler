/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static com.theokanning.openai.completion.chat.ChatMessageRole.FUNCTION;
import static com.theokanning.openai.completion.chat.ChatMessageRole.USER;
import static schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility.isExitCondition;
import static schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility.printResponse;
import java.sql.Connection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest.ChatCompletionRequestFunctionCall;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.FunctionExecutor;
import com.theokanning.openai.service.OpenAiService;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.embeddings.QueryService;
import schemacrawler.tools.command.chatgpt.options.ChatGPTCommandOptions;
import schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility;
import schemacrawler.tools.command.chatgpt.utility.ChatHistory;
import us.fatehi.utility.string.StringFormat;

public final class ChatGPTConsole implements AutoCloseable {

  private static final Logger LOGGER = Logger.getLogger(ChatGPTConsole.class.getCanonicalName());

  private static final String PROMPT = String.format("%nPrompt: ");

  private final ChatGPTCommandOptions commandOptions;
  private final FunctionExecutor functionExecutor;
  private final OpenAiService service;
  private final QueryService queryService;
  private final ChatHistory chatHistory;
  private final boolean useMetadata;

  public ChatGPTConsole(
      final ChatGPTCommandOptions commandOptions,
      final Catalog catalog,
      final Connection connection) {

    this.commandOptions = requireNonNull(commandOptions, "ChatGPT options not provided");
    requireNonNull(catalog, "No catalog provided");
    requireNonNull(connection, "No connection provided");

    functionExecutor = ChatGPTUtility.newFunctionExecutor(catalog, connection);
    final Duration timeout = Duration.ofSeconds(commandOptions.getTimeout());
    service = new OpenAiService(commandOptions.getApiKey(), timeout);

    queryService = new QueryService(service);
    queryService.addTables(catalog.getTables());

    useMetadata = commandOptions.isUseMetadata();
    chatHistory = new ChatHistory(commandOptions.getContext(), new ArrayList<>());
  }

  @Override
  public void close() {
    service.shutdownExecutor();
  }

  /** Simple REPL for the SchemaCrawler ChatGPT integration. */
  public void console() {
    try (final Scanner scanner = new Scanner(System.in)) {
      while (true) {
        System.out.print(PROMPT);
        final String prompt = scanner.nextLine();
        final List<ChatMessage> completions = complete(prompt);
        printResponse(completions, System.out);
        if (isExitCondition(completions)) {
          return;
        }
      }
    }
  }

  /**
   * Send prompt to ChatGPT API and get completions.
   *
   * @param prompt Input prompt.
   */
  private List<ChatMessage> complete(final String prompt) {

    final List<ChatMessage> completions = new ArrayList<>();

    try {

      final ChatMessage userMessage = new ChatMessage(USER.value(), prompt);
      chatHistory.add(userMessage);

      final List<ChatMessage> messages = chatHistory.toList();

      if (useMetadata) {
        final Collection<ChatMessage> chatMessages = queryService.query(prompt);
        messages.addAll(chatMessages);
      }

      final ChatCompletionRequest completionRequest =
          ChatCompletionRequest.builder()
              .messages(messages)
              .functions(functionExecutor.getFunctions())
              .functionCall(new ChatCompletionRequestFunctionCall("auto"))
              .model(commandOptions.getModel())
              .n(1)
              .build();
      logChatRequest(completionRequest.getMessages(), completionRequest.getFunctions());

      final ChatCompletionResult chatCompletion = service.createChatCompletion(completionRequest);
      LOGGER.log(Level.INFO, new StringFormat("Token usage: %s", chatCompletion.getUsage()));
      // Assume only one message was returned, since we asked for only one
      final ChatMessage responseMessage = chatCompletion.getChoices().get(0).getMessage();
      chatHistory.add(responseMessage);
      final ChatFunctionCall functionCall = responseMessage.getFunctionCall();
      if (functionCall != null) {
        final FunctionReturn functionReturn = functionExecutor.execute(functionCall);
        final ChatMessage functionResponseMessage =
            new ChatMessage(
                FUNCTION.value(), functionReturn.get(), functionCall.getName(), functionCall);
        completions.add(functionResponseMessage);
      } else {
        completions.add(responseMessage);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.INFO, e.getMessage(), e);
      final ChatMessage exceptionMessage = functionExecutor.convertExceptionToMessage(e);
      completions.add(exceptionMessage);
    }

    return completions;
  }

  private void logChatRequest(final List<ChatMessage> messages, final List<?> functions) {
    final Level level = Level.CONFIG;
    if (!LOGGER.isLoggable(level)) {
      return;
    }
    final StringBuilder buffer = new StringBuilder();
    buffer.append("ChatGPT request:").append(System.lineSeparator());
    if (messages != null) {
      for (final ChatMessage message : messages) {
        buffer.append(message).append(System.lineSeparator());
      }
    }
    if (functions != null) {
      for (final Object function : functions) {
        buffer.append(function).append(System.lineSeparator());
      }
    }
    LOGGER.log(level, buffer.toString());
  }
}
