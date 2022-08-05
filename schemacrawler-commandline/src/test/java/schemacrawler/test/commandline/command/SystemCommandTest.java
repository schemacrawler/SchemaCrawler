package schemacrawler.test.commandline.command;

import static java.util.regex.Pattern.DOTALL;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.mock;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.contentsOf;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.writeStringToTempFile;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static schemacrawler.tools.databaseconnector.TestConnectionDatabaseSources.newTestDatabaseConnectionSource;

import java.sql.Connection;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import schemacrawler.schema.Catalog;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.ShellState;

@ResolveTestContext
@WithTestDatabase
@CaptureSystemStreams
public class SystemCommandTest {

  private final String COMMAND_HELP = "command_help/";

  @Test
  public void help(final TestContext testContext) throws Exception {
    final ShellState state = new ShellState();
    final SystemCommand optionsParser = new SystemCommand(state);

    final CommandLine commandLine = newCommandLine(optionsParser, null);

    final String helpMessage = commandLine.getUsageMessage();

    assertThat(
        outputOf(writeStringToTempFile(helpMessage)),
        hasSameContentAs(
            classpathResource(COMMAND_HELP + testContext.testMethodFullName() + ".txt")));
  }

  @Test
  public void noArgs(final CapturedSystemStreams streams) {
    final String[] args = new String[0];

    final ShellState state = new ShellState();
    final SystemCommand optionsParser = new SystemCommand(state);

    allSystemInformation(optionsParser, args, streams);
  }

  @Test
  public void noValidArgs(final CapturedSystemStreams streams) {
    final String[] args = {"--some-option"};

    final ShellState state = new ShellState();
    final SystemCommand optionsParser = new SystemCommand(state);

    allSystemInformation(optionsParser, args, streams);
  }

  @Test
  public void showConnected(final Connection connection, final CapturedSystemStreams streams) {
    final String[] args = {"-C"};

    final ShellState state = new ShellState();
    state.setDataSource(newTestDatabaseConnectionSource(connection));
    executeSystemCommand(state, args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), containsString("Connected to"));
    assertThat(contentsOf(streams.out()), containsString("HSQL Database Engine"));
  }

  @Test
  public void showConnectedBadConnection(final CapturedSystemStreams streams) {
    final String[] args = {"-C"};

    final Connection connection = mock(Connection.class);

    final ShellState state = new ShellState();
    state.setDataSource(newTestDatabaseConnectionSource(connection));
    executeSystemCommand(state, args);

    assertThat(contentsOf(streams.err()), startsWith("Could not log connection information"));
    assertThat(outputOf(streams.out()), hasNoContent());
  }

  @Test
  public void showEnvironment(final CapturedSystemStreams streams) {
    final String[] args = {"-E"};

    final ShellState state = new ShellState();
    final SystemCommand optionsParser = new SystemCommand(state);

    allSystemInformation(optionsParser, args, streams);
  }

  @Test
  public void showLoaded(final CapturedSystemStreams streams) {
    final String[] args = {"-L"};

    final ShellState state = new ShellState();
    state.setCatalog(mock(Catalog.class));
    executeSystemCommand(state, args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), containsString("Database metadata is loaded"));
  }

  @Test
  public void showNotConnected(final CapturedSystemStreams streams) {
    final String[] args = {"-C"};

    final ShellState state = new ShellState();
    executeSystemCommand(state, args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), startsWith("Not connected to a database"));
  }

  @Test
  public void showNotLoaded(final CapturedSystemStreams streams) {
    final String[] args = {"-L"};

    final ShellState state = new ShellState();
    executeSystemCommand(state, args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), containsString("Database metadata is not loaded"));
  }

  @Test
  public void showState(final CapturedSystemStreams streams) {
    final String[] args = {"--show-state"};

    final ShellState state = new ShellState();
    executeSystemCommand(state, args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        contentsOf(streams.out()),
        matchesPattern(
            Pattern.compile(".*\"@object\": \"schemacrawler.tools.options.Config\".*", DOTALL)));
  }

  @Test
  public void showVersion(final CapturedSystemStreams streams) {
    final String[] args = {"-V"};

    final ShellState state = new ShellState();
    executeSystemCommand(state, args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(
        contentsOf(streams.out()),
        matchesPattern(Pattern.compile("SchemaCrawler \\d{1,2}\\.\\d{1,2}\\.\\d{1,2}.*", DOTALL)));
  }

  private void allSystemInformation(
      final SystemCommand optionsParser, final String[] args, final CapturedSystemStreams streams) {
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), containsString("Available JDBC drivers:"));
    assertThat(
        contentsOf(streams.out()),
        containsString("Available SchemaCrawler database server plugins:"));
    assertThat(contentsOf(streams.out()), containsString("Available SchemaCrawler commands:"));
  }

  private void executeSystemCommand(final ShellState state, final String[] args) {
    final SystemCommand optionsParser = new SystemCommand(state);

    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);
  }
}
