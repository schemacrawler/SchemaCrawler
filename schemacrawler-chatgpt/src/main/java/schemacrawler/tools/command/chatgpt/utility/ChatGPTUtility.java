package schemacrawler.tools.command.chatgpt.utility;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;
import com.theokanning.openai.completion.chat.ChatFunction;
import com.theokanning.openai.service.FunctionExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.functions.FunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.FunctionDefinitionRegistry;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class ChatGPTUtility {

  public static FunctionExecutor newFunctionExecutor(final Catalog catalog) {

    requireNonNull(catalog, "No catalog provided");

    final List<ChatFunction> chatFunctions = new ArrayList<>();
    for (final FunctionDefinition functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry()) {
      functionDefinition.setCatalog(catalog);
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

  private ChatGPTUtility() {
    // Prevent instantiation
  }
}
