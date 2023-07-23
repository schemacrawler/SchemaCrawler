package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.theokanning.openai.completion.chat.ChatMessage;
import schemacrawler.tools.command.chatgpt.ChatHistory;

public class ChatHistoryTest {

  @Test
  public void chatHistory() {
    final List<ChatMessage> messages = Arrays.asList(mock(ChatMessage.class));
    final ChatHistory chatHistory = new ChatHistory(10, messages);
    chatHistory.add(mock(ChatMessage.class));
    chatHistory.add(null);
    final List<ChatMessage> historyList = chatHistory.toList();
    assertThat(historyList, hasSize(2));
  }
}
