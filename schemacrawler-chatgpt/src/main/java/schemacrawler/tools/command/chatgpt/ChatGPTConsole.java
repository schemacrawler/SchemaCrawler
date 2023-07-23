package schemacrawler.tools.command.chatgpt;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility.isExitCondition;
import static schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility.printResponse;
import java.sql.Connection;
import java.util.ArrayList;
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
import schemacrawler.tools.command.chatgpt.options.ChatGPTCommandOptions;
import schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility;
import us.fatehi.utility.string.StringFormat;

public final class ChatGPTConsole {

  private static final Logger LOGGER = Logger.getLogger(ChatGPTConsole.class.getCanonicalName());

  private static final String PROMPT = String.format("%nPrompt: ");

  private final ChatGPTCommandOptions commandOptions;
  private final FunctionExecutor functionExecutor;
  private final OpenAiService service;
  private final ChatHistory chatHistory;

  public ChatGPTConsole(
      final ChatGPTCommandOptions commandOptions,
      final Catalog catalog,
      final Connection connection) {

    this.commandOptions = requireNonNull(commandOptions, "ChatGPT options not provided");
    requireNonNull(catalog, "No catalog provided");
    requireNonNull(connection, "No connection provided");

    functionExecutor = ChatGPTUtility.newFunctionExecutor(catalog, connection);
    service = new OpenAiService(commandOptions.getApiKey());

    final List<ChatMessage> systemMessages;
    if (commandOptions.isUseMetadata()) {
      systemMessages = ChatGPTUtility.systemMessages(catalog, connection);
    } else {
      systemMessages = new ArrayList<>();
    }
    chatHistory = new ChatHistory(commandOptions.getContext(), systemMessages);
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
          System.exit(0);
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
      final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
      chatHistory.add(userMessage);

      final List<ChatMessage> messages = chatHistory.toList();
      final ChatCompletionRequest completionRequest =
          ChatCompletionRequest.builder()
              .messages(messages)
              .functions(functionExecutor.getFunctions())
              .functionCall(new ChatCompletionRequestFunctionCall("auto"))
              .model(commandOptions.getModel())
              .build();

      final ChatCompletionResult chatCompletion = service.createChatCompletion(completionRequest);
      LOGGER.log(Level.INFO, new StringFormat("Token usage: %s", chatCompletion.getUsage()));
      chatCompletion
          .getChoices()
          .forEach(
              c -> {
                LOGGER.log(Level.CONFIG, String.valueOf(c));
                final ChatMessage message = c.getMessage();
                chatHistory.add(message);
                final ChatFunctionCall functionCall = message.getFunctionCall();
                if (functionCall != null) {
                  final FunctionReturn functionReturn = functionExecutor.execute(functionCall);
                  final ChatMessage functionResponseMessage =
                      new ChatMessage(
                          ChatMessageRole.FUNCTION.value(),
                          functionReturn.get(),
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
}
