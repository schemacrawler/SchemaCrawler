/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.ParsedLine;
import org.jline.shell.ExitShellException;
import org.jline.shell.Shell;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;

@DisableLogging
@DisplayName("Interactive shell wiring")
public class SchemaCrawlerShellTest {

  private Terminal terminal;
  private ShellState state;
  private Shell shell;

  @BeforeEach
  public void setUp() throws IOException {
    terminal = newTerminal("");
    state = new ShellState();
    shell = SchemaCrawlerShell.newShell(terminal, new StateFactory(state));
  }

  @AfterEach
  public void tearDown() throws IOException {
    shell.close();
    state.close();
    terminal.close();
  }

  @Test
  @DisplayName("Complete root commands")
  public void completeRootCommands() {
    final List<String> candidates = complete("");
    assertThat(candidates, hasItems("load", "execute", "help", "connect", "grep", "limit"));
  }

  @Test
  @DisplayName("Complete a partial command name")
  public void completePartialCommand() {
    final List<String> candidates = complete("exe");
    assertThat(candidates, hasItem("execute"));
  }

  @Test
  @DisplayName("Complete options of a command")
  public void completeCommandOptions() {
    final List<String> candidates = complete("execute --");
    assertThat(candidates, hasItem("--command"));
  }

  @Test
  @DisplayName("Complete options of a plugin subcommand")
  public void completePluginSubcommand() {
    final List<String> candidates = complete("load --");
    assertThat(candidates, hasItems("--info-level", "--load-row-counts", "--attributes-file"));
  }

  @Test
  @DisplayName("Keep Windows paths intact")
  public void windowsPath() {
    final String path = "C:\\Users\\sualeh\\config.properties";
    final List<String> words =
        shell.reader().getParser().parse("connect --server hsqldb " + path, 0).words();
    assertThat(words.get(words.size() - 1), is(path));
  }

  @Test
  @DisplayName("Clear the screen on a terminal without screen control")
  public void clearScreen() throws Exception {
    shell.dispatcher().execute("cls");
    shell.dispatcher().execute("clear");
  }

  @Test
  @DisplayName("Run Picocli shell help")
  public void help() throws Exception {
    shell.dispatcher().execute("help");
  }

  @Test
  @DisplayName("JLine exit and quit commands end the shell")
  public void exitCommands() throws Exception {
    assertThrows(ExitShellException.class, () -> shell.dispatcher().execute("exit"));
    assertThrows(ExitShellException.class, () -> shell.dispatcher().execute("quit"));
    runShell("exit\n");
    runShell("quit\n");
  }

  @Test
  @DisplayName("EOF ends the shell")
  public void endOfFile() throws Exception {
    runShell("");
  }

  @Test
  @DisplayName("Ctrl-C discards the current line")
  public void interruptLine() throws Exception {
    runShell("\u0003exit\n");
  }

  @Test
  @DisplayName("A command error does not end the shell")
  public void commandError() throws Exception {
    runShell("no-such-command\nexit\n");
  }

  @Test
  @DisplayName("Close shell state after the shell exits")
  public void shellStateCleanup() throws Exception {
    runShell("exit\n");
  }

  @Test
  @DisplayName("The supplied terminal remains open when the shell is closed")
  public void terminalOwnership() throws IOException {
    final Terminal suppliedTerminal = spy(newTerminal(""));
    final Shell suppliedShell =
        SchemaCrawlerShell.newShell(suppliedTerminal, new StateFactory(state));

    suppliedShell.close();

    verify(suppliedTerminal, never()).close();
    suppliedTerminal.close();
  }

  @Test
  @DisplayName("Terminate is not a supported command")
  public void terminateIsNotSupported() {
    assertThat(complete("term"), not(hasItem("terminate")));
    assertThrows(Exception.class, () -> shell.dispatcher().execute("terminate"));
  }

  private List<String> complete(final String line) {
    final ParsedLine parsedLine = shell.reader().getParser().parse(line, line.length());
    final List<Candidate> candidates = new ArrayList<>();
    final Completer completer = shell.dispatcher().completer();
    completer.complete(shell.reader(), parsedLine, candidates);
    return candidates.stream().map(Candidate::value).toList();
  }

  private static Terminal newTerminal(final String input) throws IOException {
    return TerminalBuilder.builder()
        .dumb(true)
        .system(false)
        .streams(
            new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
            new ByteArrayOutputStream())
        .build();
  }

  private static void runShell(final String input) throws Exception {
    final Terminal testTerminal = newTerminal(input);
    final TrackingShellState testState = new TrackingShellState();
    try {
      final int exitCode = SchemaCrawlerShell.runShell(testTerminal, new String[0], testState);
      assertThat(exitCode, is(0));
      assertThat(testState.closed, is(true));
    } finally {
      testTerminal.close();
    }
  }

  private static final class TrackingShellState extends ShellState {

    private boolean closed;

    @Override
    public void close() {
      closed = true;
      super.close();
    }
  }
}
