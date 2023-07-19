package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import java.sql.Connection;
import org.junit.jupiter.api.Test;
import com.theokanning.openai.service.FunctionExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility;

public class ChatGPTUtilityTest {

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
