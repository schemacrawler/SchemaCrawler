package schemacrawler.test;

import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.PluginCommandTestUtility;
import schemacrawler.tools.command.text.operation.OperationCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
public class PluginCommandTest {

  @Test
  public void testOperationCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new OperationCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testOperationCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new OperationCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }
}
