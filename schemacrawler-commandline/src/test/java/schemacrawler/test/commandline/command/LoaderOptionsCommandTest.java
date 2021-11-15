package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.CommandlineTestUtility.createConnectedSchemaCrawlerShellState;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.writeStringToTempFile;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.addPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.catalogLoaderPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import picocli.CommandLine;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.commandline.SchemaCrawlerShellCommands;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;

@ExtendWith(TestContextParameterResolver.class)
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class LoaderOptionsCommandTest {

  private final String COMMAND_HELP = "command_help/";

  @Test
  public void dynamicOptionValue(final Connection connection) throws Exception {
    final String[] args = {
      "--test-load-option", "true",
    };

    final ShellState state = createConnectedSchemaCrawlerShellState(connection);
    final CommandLine commandLine = createShellCommandLine(connection, state);

    commandLine.parseArgs(args);
  }

  @Test
  public void help(final TestContext testContext, final Connection connection) throws Exception {

    final ShellState state = createConnectedSchemaCrawlerShellState(connection);
    final CommandLine commandLine = createShellCommandLine(connection, state);

    final String helpMessage = commandLine.getSubcommands().get("load").getUsageMessage();

    assertThat(
        outputOf(writeStringToTempFile(helpMessage)),
        hasSameContentAs(
            classpathResource(COMMAND_HELP + testContext.testMethodFullName() + ".txt")));
  }

  private CommandLine createShellCommandLine(final Connection connection, final ShellState state) {

    final SchemaCrawlerShellCommands commands = new SchemaCrawlerShellCommands();
    final CommandLine commandLine = newCommandLine(commands, new StateFactory(state));
    final CommandLine loadCommandLine = commandLine.getSubcommands().getOrDefault("load", null);
    if (loadCommandLine != null) {
      addPluginCommands(loadCommandLine, catalogLoaderPluginCommands);
      commandLine.addSubcommand(loadCommandLine);
    }
    return commandLine;
  }
}
