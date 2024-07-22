package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.executable.CommandRegistry;
import us.fatehi.utility.property.CommandDescription;

public class CommandPluginTest {

  @Test
  public void testCommandPlugin() throws Exception {
    final CommandRegistry registry = CommandRegistry.getCommandRegistry();
    assertThat(isCommandSupported(registry, "chatgpt"), is(true));
  }

  private boolean isCommandSupported(final CommandRegistry registry, final String command) {
    final Collection<CommandDescription> supportedCommands = registry.getCommandDescriptions();
    for (final CommandDescription supportedCommand : supportedCommands) {
      if (supportedCommand.getName().equals(command)) {
        return true;
      }
    }
    return false;
  }
}
