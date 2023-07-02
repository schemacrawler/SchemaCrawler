package schemacrawler.tools.command.chatgpt;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest.ChatCompletionRequestFunctionCall;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.FunctionExecutor;
import com.theokanning.openai.service.OpenAiService;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.functions.FunctionReturn;
import schemacrawler.tools.command.chatgpt.options.ChatGPTCommandOptions;
import schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility;

public final class ChatGPTConsole {

  private static final Logger LOGGER = Logger.getLogger(ChatGPTConsole.class.getCanonicalName());

  private static final String PROMPT = String.format("%nPrompt: ");

  private final ChatGPTCommandOptions commandOptions;

  private final FunctionExecutor functionExecutor;
  private final OpenAiService service;

  public ChatGPTConsole(final ChatGPTCommandOptions commandOptions, final Catalog catalog) {
    this.commandOptions = requireNonNull(commandOptions, "ChatGPT options not provided");
    requireNonNull(catalog, "No catalog provided");

    functionExecutor = ChatGPTUtility.newFunctionExecutor(catalog);
    service = new OpenAiService(commandOptions.getApiKey());
  }

  public void console() {
    try (final Scanner scanner = new Scanner(System.in)) {
      while (true) {
        System.out.print(PROMPT);
        final String prompt = scanner.nextLine();
        final List<ChatMessage> completions = complete(prompt);
        printResponse(completions);
        checkEndLoop(completions);
      }
    }
  }

  private void checkEndLoop(final List<ChatMessage> completions) {
    completions.stream()
        .forEach(
            c -> {
              if (c.getFunctionCall() != null && c.getName().equals("exit")) {
                System.exit(0);
              }
            });
  }

  /**
   * Send prompt to ChatGPT API and get completions.
   *
   * @param prompt Input prompt.
   */
  private List<ChatMessage> complete(final String prompt) {

    final List<ChatMessage> completions = new ArrayList<>();

    try {
      final ChatCompletionRequest completionRequest =
          ChatCompletionRequest.builder()
              .messages(
                  Collections.singletonList(new ChatMessage(ChatMessageRole.USER.value(), prompt)))
              .functions(functionExecutor.getFunctions())
              .functionCall(new ChatCompletionRequestFunctionCall("auto"))
              .model(commandOptions.getModel())
              .build();

      final ChatCompletionResult chatCompletion = service.createChatCompletion(completionRequest);

      chatCompletion
          .getChoices()
          .forEach(
              c -> {
                LOGGER.log(Level.CONFIG, String.valueOf(c));
                final ChatMessage message = c.getMessage();
                final ChatFunctionCall functionCall = message.getFunctionCall();
                if (functionCall != null) {
                  final FunctionReturn functionReturn = functionExecutor.execute(functionCall);
                  final ChatMessage functionResponseMessage =
                      new ChatMessage(
                          ChatMessageRole.FUNCTION.value(),
                          functionReturn.render(),
                          functionCall.getName(),
                          functionCall);
                  completions.add(functionResponseMessage);
                } else {
                  completions.add(message);
                }
              });
    } catch (final Exception e) {
      LOGGER.log(Level.INFO, e.getMessage(), e);
      final ChatMessage exceptionMessage = functionExecutor.convertExceptionToMessage(e);
      completions.add(exceptionMessage);
    }

    return completions;
  }

  /**
   * Send prompt to ChatGPT API and display response
   *
   * @param prompt Input prompt.
   */
  private void printResponse(final List<ChatMessage> completions) {
    completions.stream()
        .forEach(
            c -> {
              System.out.println(c.getContent());
            });
  }
}
