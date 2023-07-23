package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.FunctionExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.command.chatgpt.functions.ExitFunctionDefinition;
import schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility;

public class ChatGPTUtilityTest {

  @Test
  public void isExitCondition() {
    final boolean exitCondition1 =
        ChatGPTUtility.isExitCondition(Arrays.asList(mock(ChatMessage.class)));
    assertThat(exitCondition1, is(false));

    final ChatMessage mockChatMessage1 = mock(ChatMessage.class);
    when(mockChatMessage1.getFunctionCall()).thenReturn(mock(ChatFunctionCall.class));
    when(mockChatMessage1.getName()).thenReturn(new ExitFunctionDefinition().getName());
    final boolean exitCondition2 = ChatGPTUtility.isExitCondition(Arrays.asList(mockChatMessage1));
    assertThat(exitCondition2, is(true));

    final ChatMessage mockChatMessage2 = mock(ChatMessage.class);
    when(mockChatMessage2.getFunctionCall()).thenReturn(mock(ChatFunctionCall.class));
    when(mockChatMessage2.getName()).thenReturn("Something else");
    final boolean exitCondition3 = ChatGPTUtility.isExitCondition(Arrays.asList(mockChatMessage2));
    assertThat(exitCondition3, is(false));
  }

  @Test
  public void printResponse() {
    final TestOutputStream stream = new TestOutputStream();
    final PrintStream out = new PrintStream(stream);

    assertThrows(NullPointerException.class, () -> ChatGPTUtility.printResponse(null, out));
    assertThrows(
        NullPointerException.class,
        () -> ChatGPTUtility.printResponse(Collections.emptyList(), null));

    ChatGPTUtility.printResponse(Arrays.asList(mock(ChatMessage.class)), out);
    out.flush();
    assertThat(stream.getContents(), startsWith("null"));
  }

  @Test
  public void systemMessages() {
    final Catalog catalog = mock(Catalog.class);
    final Connection connection = mock(Connection.class);

    assertThrows(NullPointerException.class, () -> ChatGPTUtility.systemMessages(null, connection));
    assertThrows(NullPointerException.class, () -> ChatGPTUtility.systemMessages(catalog, null));

    final List<ChatMessage> systemMessages = ChatGPTUtility.systemMessages(catalog, connection);
    assertThat(systemMessages, hasSize(1));
  }

  @Test
  public void utility() throws Exception {
    final Catalog catalog = mock(Catalog.class);
    final Connection connection = mock(Connection.class);
    final FunctionExecutor functionExecutor =
        ChatGPTUtility.newFunctionExecutor(catalog, connection);
    assertThat(functionExecutor, is(not(nullValue())));
    assertThat(functionExecutor.getFunctions(), hasSize(6));
  }
}
