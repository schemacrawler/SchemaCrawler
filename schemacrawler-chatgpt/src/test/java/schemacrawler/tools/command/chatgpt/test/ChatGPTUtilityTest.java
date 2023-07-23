package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import java.sql.Connection;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.FunctionExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility;

public class ChatGPTUtilityTest {

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
