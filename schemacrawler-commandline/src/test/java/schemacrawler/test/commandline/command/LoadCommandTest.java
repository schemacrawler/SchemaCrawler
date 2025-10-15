/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.FileHasContent.text;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.CaptureLogs;
import schemacrawler.test.utility.CapturedLogs;
import schemacrawler.test.utility.CommandlineTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.commandline.command.LoadCommand;
import schemacrawler.tools.commandline.state.ShellState;
import us.fatehi.utility.datasource.ConnectionDatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@ResolveTestContext
@WithTestDatabase
public class LoadCommandTest {

  private final String COMMAND_HELP = "command_help/";

  @Test
  public void execute(final DatabaseConnectionSource dataSource) {
    final String[] args = {"--info-level", "detailed", "--load-row-counts", "additional", "-extra"};

    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());
    state.setDataSource(dataSource);
    assertThat(state.getCatalog(), is(nullValue()));

    final LoadCommand optionsParser = new LoadCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    final InfoLevel schemaInfoLevel = optionsParser.getInfoLevel();

    assertThat(schemaInfoLevel, is(InfoLevel.detailed));
    assertThat(state.getCatalog(), is(not(nullValue())));
    assertThat(state.getCatalog().getTables(), hasSize(20));
  }

  @Test
  @CaptureLogs
  public void executeDeferCatalogLoad(final CapturedLogs logs) throws Throwable {

    final String[] args = {"--info-level", "detailed", "--load-row-counts", "additional", "-extra"};

    final ShellState state = new ShellState();
    state.setDeferCatalogLoad(true);
    state.setSchemaCrawlerOptions(SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());
    assertThat(state.getCatalog(), is(nullValue()));

    final LoadCommand optionsParser = new LoadCommand(state);
    CommandlineTestUtility.executeCommandInTest(optionsParser, args);
    assertThat(state.getCatalog(), is(nullValue()));
    assertThat(
        logs.contains(Level.CONFIG, Pattern.compile("Not loading catalog, since this is deferred")),
        is(true));
  }

  @Test
  public void executeExceptionLoading() throws SQLException {

    final Connection connection = mock(Connection.class);
    when(connection.getMetaData()).thenReturn(mock(DatabaseMetaData.class));

    final String[] args = {"--info-level", "detailed", "--load-row-counts", "additional", "-extra"};

    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());
    state.setDataSource(new ConnectionDatabaseConnectionSource(connection));
    assertThat(state.getCatalog(), is(nullValue()));

    final LoadCommand optionsParser = new LoadCommand(state);
    assertThrows(
        IllegalArgumentException.class,
        () -> CommandlineTestUtility.executeCommandInTest(optionsParser, args));
  }

  @Test
  public void executeNotConnected() throws SQLException {

    final String[] args = {"--info-level", "detailed", "--load-row-counts", "additional", "-extra"};

    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());
    assertThat(state.getCatalog(), is(nullValue()));

    final LoadCommand optionsParser = new LoadCommand(state);
    assertThrows(
        CommandLine.ExecutionException.class,
        () -> CommandlineTestUtility.executeCommandInTest(optionsParser, args));
  }

  @Test
  public void help(final TestContext testContext) throws Exception {
    final ShellState state = new ShellState();
    final LoadCommand optionsParser = new LoadCommand(state);

    final CommandLine commandLine = newCommandLine(optionsParser, null);

    final String helpMessage = commandLine.getUsageMessage();

    assertThat(
        outputOf(text(helpMessage)),
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
    final String[] args = {};

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
