package schemacrawler.tools.command.chatgpt.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;
import schemacrawler.tools.command.chatgpt.embeddings.EmbeddingService;

public class EmbeddingServiceTest {

  private OpenAiService openAiService;
  private EmbeddingService embeddingService;

  @BeforeEach
  void setUp() {
    openAiService = mock(OpenAiService.class);
    embeddingService = new EmbeddingService(openAiService);
  }

  @Test
  void testEmbeddingWithEmptyText() {
    final String text = "";

    final IllegalArgumentException exception =
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class, () -> embeddingService.embed(text));

    assertEquals("No text provided", exception.getMessage());
  }

  @Test
  void testEmbeddingWithValidText() {
    final String text = "example text";
    final List<Double> expectedEmbedding = new ArrayList<>(Collections.singletonList(0.5));

    // Mock behavior of OpenAiService
    final Embedding embedding = mock(Embedding.class);
    when(embedding.getEmbedding()).thenReturn(expectedEmbedding);
    final EmbeddingResult embeddingResult = mock(EmbeddingResult.class);
    when(embeddingResult.getData()).thenReturn(Collections.singletonList(embedding));
    when(openAiService.createEmbeddings(any(EmbeddingRequest.class))).thenReturn(embeddingResult);

    // Test the embed method
    final List<Double> actualEmbedding = embeddingService.embed(text);

    // Verify the result
    assertEquals(expectedEmbedding, actualEmbedding);
  }
}
