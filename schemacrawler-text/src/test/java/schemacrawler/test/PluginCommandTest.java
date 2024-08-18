package schemacrawler.test;

import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.PluginCommandTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.command.text.operation.OperationCommandProvider;
import schemacrawler.tools.command.text.schema.SchemaTextCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;

@ResolveTestContext
public class PluginCommandTest {

  @Test
  public void testSchemaTextCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new SchemaTextCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testSchemaTextCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new SchemaTextCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

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
