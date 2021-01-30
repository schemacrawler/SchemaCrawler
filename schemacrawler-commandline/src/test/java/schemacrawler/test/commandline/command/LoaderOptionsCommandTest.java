package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.writeStringToTempFile;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import java.sql.Connection;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParseResult;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.utility.CommandLineUtility;

@ExtendWith(TestContextParameterResolver.class)
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class LoaderOptionsCommandTest {

  private final String COMMAND_HELP = "command_help/";

  @Test
  public void dynamicOptionValue(final Connection connection) throws Exception {
    final String[] args = {
      "--test-load-option", "true",
    };

    final ShellState state = new ShellState();

    @Command(name = "base-command")
    class SomeClass {}

    final CommandSpec loaderOptionsCommandSpec = CommandLineUtility.loaderOptionsCommandSpec(state);

    final CommandLine baseCommandLine = newCommandLine(new SomeClass(), null);
    baseCommandLine.addMixin("loaderoptions", loaderOptionsCommandSpec);

    final ParseResult parseResult = baseCommandLine.parseArgs(args);

    final Map<String, Object> matchedOptionValues =
        CommandLineUtility.matchedOptionValues(parseResult);
    System.out.println(matchedOptionValues);

    assertThat(matchedOptionValues.containsKey("test-load-option"), is(true));
    assertThat(matchedOptionValues.get("test-load-option"), is(true));
  }

  @Test
  public void help(final TestContext testContext) throws Exception {

    final ShellState state = new ShellState();

    @Command(name = "base-command")
    class SomeClass {}

    final CommandSpec loaderOptionsCommandSpec = CommandLineUtility.loaderOptionsCommandSpec(state);

    final CommandLine baseCommandLine =
        newCommandLine(new SomeClass(), null).addSubcommand(loaderOptionsCommandSpec);

    final String helpMessage =
        baseCommandLine.getSubcommands().get("loaderoptions").getUsageMessage();

    assertThat(
        outputOf(writeStringToTempFile(helpMessage)),
        hasSameContentAs(
            classpathResource(COMMAND_HELP + testContext.testMethodFullName() + ".txt")));
  }
}
