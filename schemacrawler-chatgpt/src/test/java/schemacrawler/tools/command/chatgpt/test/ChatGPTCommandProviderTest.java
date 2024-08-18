package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.test.utility.PluginCommandTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.command.chatgpt.ChatGPTCommand;
import schemacrawler.tools.command.chatgpt.ChatGPTCommandProvider;
import schemacrawler.tools.command.chatgpt.options.ChatGPTCommandOptions;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;

@ResolveTestContext
public class ChatGPTCommandProviderTest {

  @Test
  public void newSchemaCrawlerCommand() {
    final ChatGPTCommandProvider commandProvider = new ChatGPTCommandProvider();
    assertThrows(
        IllegalArgumentException.class,
        () -> commandProvider.newSchemaCrawlerCommand("bad-command", new Config()));

    assertThrows(
        ExecutionRuntimeException.class,
        () -> commandProvider.newSchemaCrawlerCommand("chatgpt", new Config()));

    final Config config = new Config();
    config.put("api-key", "api-key");
    final ChatGPTCommand command = commandProvider.newSchemaCrawlerCommand("chatgpt", config);
    final ChatGPTCommandOptions commandOptions = command.getCommandOptions();
    assertThat(commandOptions.getApiKey(), is("api-key"));
    assertThat(commandOptions.getModel(), startsWith("gpt-3.5-turbo"));
  }

  @Test
  public void testChatGPTCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new ChatGPTCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testChatGPTCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new ChatGPTCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void pluginCommand() {
    final ChatGPTCommandProvider commandProvider = new ChatGPTCommandProvider();
    assertThat(
        commandProvider.getCommandLineCommand().toString(),
        is(
            "PluginCommand[name='chatgpt', options=["
                + "PluginCommandOption[name='api-key', valueClass=java.lang.String], "
                + "PluginCommandOption[name='api-key:env', valueClass=java.lang.String], "
                + "PluginCommandOption[name='model', valueClass=java.lang.String], "
                + "PluginCommandOption[name='timeout', valueClass=java.lang.Integer], "
                + "PluginCommandOption[name='context', valueClass=java.lang.Integer], "
                + "PluginCommandOption[name='use-metadata', valueClass=java.lang.Boolean]"
                + "]]"));
  }

  @Test
  public void supportsOutputFormat() {
    final ChatGPTCommandProvider commandProvider = new ChatGPTCommandProvider();
    assertThat(commandProvider.supportsOutputFormat(null, null), is(true));
  }
}
