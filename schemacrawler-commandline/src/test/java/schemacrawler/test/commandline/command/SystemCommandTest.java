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
import static us.fatehi.utility.datasource.DatabaseConnectionSourceUtility.newTestDatabaseConnectionSource;
import java.sql.Connection;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schema.Catalog;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.ShellState;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@ResolveTestContext
@WithTestDatabase
@CaptureSystemStreams
@DisableLogging
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
    final String[] args = {};

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
  public void showConnected(
      final DatabaseConnectionSource dataSource, final CapturedSystemStreams streams) {
    final String[] args = {"-C"};

    final ShellState state = new ShellState();
    state.setDataSource(dataSource);
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

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), startsWith("Connected to"));
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

    // Error stream may have some messages on Java 21, due to how tests are set up
    // assertThat(outputOf(streams.err()), hasNoContent());
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
    assertThat(contentsOf(streams.out()), containsString("Available JDBC Drivers:"));
    assertThat(
        contentsOf(streams.out()),
        containsString("Available SchemaCrawler Database Server Plugins:"));
    assertThat(contentsOf(streams.out()), containsString("Available SchemaCrawler Commands:"));
  }

  private void executeSystemCommand(final ShellState state, final String[] args) {
    final SystemCommand optionsParser = new SystemCommand(state);

    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);
  }
}
