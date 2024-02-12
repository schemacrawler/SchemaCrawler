package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.chatgpt.options.ChatGPTCommandOptions;
import schemacrawler.tools.command.chatgpt.options.ChatGPTCommandOptionsBuilder;
import schemacrawler.tools.options.Config;

public class ChatGPTCommandOptionsBuilderTest {

  @Test
  public void chatGPTCommandOptionsBuilderApiKey() {

    assertThrows(
        IllegalArgumentException.class, () -> ChatGPTCommandOptionsBuilder.builder().toOptions());

    final ChatGPTCommandOptionsBuilder optionsBuilder =
        ChatGPTCommandOptionsBuilder.builder().withApiKey("api-key");
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-3.5-turbo"));

    optionsBuilder.withApiKey(null);
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-3.5-turbo"));

    optionsBuilder.withApiKey("\t");
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-3.5-turbo"));

    optionsBuilder.withApiKey("new-api-key");
    assertThat(optionsBuilder.toOptions().getApiKey(), is("new-api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-3.5-turbo"));
  }

  @Test
  public void chatGPTCommandOptionsBuilderContext() {

    final ChatGPTCommandOptionsBuilder optionsBuilder =
        ChatGPTCommandOptionsBuilder.builder().withApiKey("api-key");

    assertThat(optionsBuilder.toOptions().getContext(), is(10));
    optionsBuilder.withContext(20);
    assertThat(optionsBuilder.toOptions().getContext(), is(20));
    optionsBuilder.withContext(0);
    assertThat(optionsBuilder.toOptions().getContext(), is(10));
    optionsBuilder.withContext(500);
    assertThat(optionsBuilder.toOptions().getContext(), is(10));
    optionsBuilder.withContext(-2);
    assertThat(optionsBuilder.toOptions().getContext(), is(10));
  }

  @Test
  public void chatGPTCommandOptionsBuilderModel() {

    assertThrows(
        IllegalArgumentException.class, () -> ChatGPTCommandOptionsBuilder.builder().toOptions());

    final ChatGPTCommandOptionsBuilder optionsBuilder =
        ChatGPTCommandOptionsBuilder.builder().withApiKey("api-key");

    optionsBuilder.withModel(null);
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-3.5-turbo"));

    optionsBuilder.withModel("\t");
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-3.5-turbo"));

    optionsBuilder.withModel("new-model");
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), is("new-model"));
  }

  @Test
  public void chatGPTCommandOptionsBuilderTimeout() {

    final ChatGPTCommandOptionsBuilder optionsBuilder =
        ChatGPTCommandOptionsBuilder.builder().withApiKey("api-key");

    assertThat(optionsBuilder.toOptions().getTimeout(), is(10));
    optionsBuilder.withTimeout(20);
    assertThat(optionsBuilder.toOptions().getTimeout(), is(20));
    optionsBuilder.withTimeout(0);
    assertThat(optionsBuilder.toOptions().getTimeout(), is(0));
    optionsBuilder.withTimeout(500);
    assertThat(optionsBuilder.toOptions().getTimeout(), is(10));
    optionsBuilder.withTimeout(-2);
    assertThat(optionsBuilder.toOptions().getTimeout(), is(10));
  }

  @Test
  public void chatGPTCommandOptionsBuilderUseMetadata() {

    final ChatGPTCommandOptionsBuilder optionsBuilder =
        ChatGPTCommandOptionsBuilder.builder().withApiKey("api-key");

    assertThat(optionsBuilder.toOptions().isUseMetadata(), is(false));
    optionsBuilder.withUseMetadata(true);
    assertThat(optionsBuilder.toOptions().isUseMetadata(), is(true));
    optionsBuilder.withUseMetadata(false);
    assertThat(optionsBuilder.toOptions().isUseMetadata(), is(false));
  }

  @Test
  public void fromConfig() {
    Config config;

    config = new Config();
    config.put("api-key", "api-key");
    final ChatGPTCommandOptions options =
        ChatGPTCommandOptionsBuilder.builder().fromConfig(config).toOptions();
    assertThat(options.getApiKey(), is("api-key"));
    assertThat(options.getModel(), startsWith("gpt-3.5-turbo"));

    // Have both keys
    config = new Config();
    config.put("api-key", "api-key");
    config.put("api-key:env", "api-key-env");
    final ChatGPTCommandOptions options2 =
        ChatGPTCommandOptionsBuilder.builder().fromConfig(config).toOptions();
    assertThat(options2.getApiKey(), is("api-key"));
    assertThat(options2.getModel(), startsWith("gpt-3.5-turbo"));

    config = new Config();
    config.put("api-key:env", "api-key-env");
    System.setProperty("api-key-env", "api-key");
    final ChatGPTCommandOptions options3 =
        ChatGPTCommandOptionsBuilder.builder().fromConfig(config).toOptions();
    assertThat(options3.getApiKey(), is("api-key"));
    assertThat(options3.getModel(), startsWith("gpt-3.5-turbo"));

    // Have both keys, with api-key blank
    config = new Config();
    config.put("api-key", "\t");
    config.put("api-key:env", "api-key-env");
    final ChatGPTCommandOptions options4 =
        ChatGPTCommandOptionsBuilder.builder().fromConfig(config).toOptions();
    assertThat(options4.getApiKey(), is("api-key"));
    assertThat(options4.getModel(), startsWith("gpt-3.5-turbo"));

    // No value for environmental variable
    final Config config1 = new Config();
    config1.put("api-key:env", "\t");
    assertThrows(
        IllegalArgumentException.class,
        () -> ChatGPTCommandOptionsBuilder.builder().fromConfig(config1).toOptions());

    // Null config
    assertThrows(
        IllegalArgumentException.class,
        () -> ChatGPTCommandOptionsBuilder.builder().fromConfig(null).toOptions());
  }

  @Test
  public void fromOptions() {
    final ChatGPTCommandOptions options =
        ChatGPTCommandOptionsBuilder.builder().withApiKey("api-key").withModel("model").toOptions();
    final ChatGPTCommandOptionsBuilder optionsBuilder =
        ChatGPTCommandOptionsBuilder.builder().fromOptions(options);
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), is("model"));

    // With null options
    assertThrows(
        IllegalArgumentException.class,
        () -> ChatGPTCommandOptionsBuilder.builder().fromOptions(null).toOptions());
  }

  @Test
  public void toConfig() {
    final ChatGPTCommandOptionsBuilder builder = ChatGPTCommandOptionsBuilder.builder();
    assertThrows(UnsupportedOperationException.class, () -> builder.toConfig());
  }
}
