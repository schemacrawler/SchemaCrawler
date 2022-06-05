package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.writeStringToTempFile;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import picocli.CommandLine;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.commandline.command.LoadCommand;
import schemacrawler.tools.commandline.state.ShellState;

@ResolveTestContext
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class LoadCommandTest {

  private final String COMMAND_HELP = "command_help/";

  @Test
  public void help(final TestContext testContext) throws Exception {
    final ShellState state = new ShellState();
    final LoadCommand optionsParser = new LoadCommand(state);

    final CommandLine commandLine = newCommandLine(optionsParser, null);

    final String helpMessage = commandLine.getUsageMessage();

    assertThat(
        outputOf(writeStringToTempFile(helpMessage)),
        hasSameContentAs(
            classpathResource(COMMAND_HELP + testContext.testMethodFullName() + ".txt")));
  }

  @Test
  public void infoLevelBadValue() {
    final String[] args = {"--info-level", "someinfolvl"};

    final ShellState state = new ShellState();
    final LoadCommand optionsParser = new LoadCommand(state);

    assertThrows(
        CommandLine.ParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void infoLevelNoValue() {
    final String[] args = {"--info-level"};

    final ShellState state = new ShellState();
    final LoadCommand optionsParser = new LoadCommand(state);

    assertThrows(
        CommandLine.ParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void infoLevelWithValue() {
    final String[] args = {"--info-level", "detailed", "additional", "-extra"};

    final ShellState state = new ShellState();
    final LoadCommand optionsParser = new LoadCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);

    final InfoLevel schemaInfoLevel = optionsParser.getInfoLevel();

    assertThat(schemaInfoLevel, is(InfoLevel.detailed));
  }

  @Test
  public void loadRowCountsWithoutValue() {
    final String[] args = {"--info-level", "detailed", "--load-row-counts", "additional", "-extra"};

    final ShellState state = new ShellState();
    final LoadCommand optionsParser = new LoadCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);

    final InfoLevel schemaInfoLevel = optionsParser.getInfoLevel();

    assertThat(schemaInfoLevel, is(InfoLevel.detailed));
  }

  @Test
  public void loadRowCountsWithValue() {
    final String[] args = {
      "--info-level", "detailed", "--load-row-counts", "true", "additional", "-extra"
    };

    final ShellState state = new ShellState();
    final LoadCommand optionsParser = new LoadCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);

    final InfoLevel schemaInfoLevel = optionsParser.getInfoLevel();

    assertThat(schemaInfoLevel, is(InfoLevel.detailed));
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final ShellState state = new ShellState();
    final LoadCommand optionsParser = new LoadCommand(state);

    assertThrows(
        CommandLine.ParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final ShellState state = new ShellState();
    final LoadCommand optionsParser = new LoadCommand(state);

    assertThrows(
        CommandLine.ParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }
}
