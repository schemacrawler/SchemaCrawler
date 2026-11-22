/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.commandline.state.ShellState;

@DisableLogging
@DisplayName("Interactive shell wiring")
public class SchemaCrawlerShellTest {

  @Test
  @DisplayName("Complete root commands")
  public void completeRootCommands() throws Exception {
    final ShellResult result = runShell("\ty\u0003exit\n");
    assertThat(result.output(), containsString("load"));
    assertThat(result.output(), containsString("execute"));
    assertThat(result.output(), containsString("help"));
    assertThat(result.output(), containsString("connect"));
    assertThat(result.output(), containsString("grep"));
    assertThat(result.output(), containsString("limit"));
  }

  @Test
  @DisplayName("Complete a partial command name")
  public void completePartialCommand() throws Exception {
    final ShellResult result = runShell("exe\t\u0003exit\n");
    assertThat(result.output(), containsString("execute"));
  }

  @Test
  @DisplayName("Complete options of a command")
  public void completeCommandOptions() throws Exception {
    final ShellResult result = runShell("execute --\t\u0003exit\n");
    assertThat(result.output(), containsString("--command"));
  }

  @Test
  @DisplayName("Complete options of a plugin subcommand")
  public void completePluginSubcommand() throws Exception {
    final ShellResult result = runShell("load --\t\u0003exit\n");
    assertThat(result.output(), containsString("--info-level"));
    assertThat(result.output(), containsString("--load-row-counts"));
    assertThat(result.output(), containsString("--attributes-file"));
  }

  @Test
  @DisplayName("Keep Windows paths intact")
  public void windowsPath() throws Exception {
    final String path = "C:\\Users\\sualeh\\config.properties";
    final ShellResult result = runShell("connect --url " + path + "\nexit\n");
    assertThat(result.output(), containsString(path));
  }

  @Test
  @DisplayName("Clear the screen on a terminal without screen control")
  public void clearScreen() throws Exception {
    assertThat(runShell("clear\nexit\n").exitCode(), is(0));
  }

  @Test
  @DisplayName("Run Picocli shell help")
  public void help() throws Exception {
    assertThat(runShell("help\nexit\n").exitCode(), is(0));
  }

  @Test
  @DisplayName("JLine exit and quit commands end the shell")
  public void exitCommands() throws Exception {
    assertThat(runShell("exit\n").exitCode(), is(0));
    assertThat(runShell("quit\n").exitCode(), is(0));
  }

  @Test
  @DisplayName("EOF ends the shell")
  public void endOfFile() throws Exception {
    assertThat(runShell("").exitCode(), is(0));
  }

  @Test
  @DisplayName("Ctrl-C discards the current line")
  public void interruptLine() throws Exception {
    assertThat(runShell("\u0003exit\n").exitCode(), is(0));
  }

  @Test
  @DisplayName("A command error does not end the shell")
  public void commandError() throws Exception {
    assertThat(runShell("no-such-command\nexit\n").exitCode(), is(0));
  }

  @Test
  @DisplayName("Close shell state after the shell exits")
  public void shellStateCleanup() throws Exception {
    assertThat(runShell("exit\n").stateClosed(), is(true));
  }

  @Test
  @DisplayName("Terminate is not a supported command")
  public void terminateIsNotSupported() throws Exception {
    final ShellResult result = runShell("terminate\nexit\n");
    assertThat(result.output(), containsString("Unknown command"));
  }

  private static ShellResult runShell(final String input) throws Exception {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final Terminal terminal = spy(newTerminal(input, output));
    final TrackingShellState state = new TrackingShellState();
    final int exitCode = new SchemaCrawlerShell(() -> terminal, () -> state).execute();
    verify(terminal, times(1)).close();
    return new ShellResult(exitCode, output.toString(StandardCharsets.UTF_8), state.closed);
  }

  private static Terminal newTerminal(final String input, final ByteArrayOutputStream output)
      throws IOException {
    return TerminalBuilder.builder()
        .dumb(true)
        .system(false)
        .streams(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), output)
        .build();
  }

  private record ShellResult(int exitCode, String output, boolean stateClosed) {}

  private static final class TrackingShellState extends ShellState {

    private boolean closed;

    @Override
    public void close() {
      closed = true;
      super.close();
    }
  }
}
