package schemacrawler.tools.command.chatgpt.utility;

import static java.util.Objects.requireNonNull;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import com.theokanning.openai.completion.chat.ChatFunction;
import com.theokanning.openai.service.FunctionExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.FunctionDefinition;
import schemacrawler.tools.command.chatgpt.FunctionDefinition.FunctionType;
import schemacrawler.tools.command.chatgpt.functions.FunctionDefinitionRegistry;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class ChatGPTUtility {

  public static FunctionExecutor newFunctionExecutor(
      final Catalog catalog, final Connection connection) {

    requireNonNull(catalog, "No catalog provided");

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

  private ChatGPTUtility() {
    // Prevent instantiation
  }
}
