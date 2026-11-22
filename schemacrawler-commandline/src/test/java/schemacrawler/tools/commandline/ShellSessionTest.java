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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.jline.reader.EndOfFileException;
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
@DisplayName("Interactive shell session wiring")
public class ShellSessionTest {

  private Terminal terminal;
  private ShellState state;
  private ShellSession session;

  @BeforeEach
  public void setUp() throws IOException {
    terminal =
        TerminalBuilder.builder()
            .dumb(true)
            .system(false)
            .streams(new ByteArrayInputStream(new byte[0]), new ByteArrayOutputStream())
            .build();
    state = new ShellState();
    session = new ShellSession(terminal, new StateFactory(state));
  }

  @AfterEach
  public void tearDown() throws IOException {
    state.close();
    terminal.close();
  }

  @Test
  @DisplayName("Complete root commands")
  public void completeRootCommands() {
    final List<String> candidates = session.complete("");
    assertThat(candidates, hasItems("load", "execute", "help", "connect", "grep", "limit"));
  }

  @Test
  @DisplayName("Complete a partial command name")
  public void completePartialCommand() {
    final List<String> candidates = session.complete("exe");
    assertThat(candidates, hasItem("execute"));
  }

  @Test
  @DisplayName("Complete options of a command")
  public void completeCommandOptions() {
    final List<String> candidates = session.complete("execute --");
    assertThat(candidates, hasItem("--command"));
  }

  @Test
  @DisplayName("Complete options of a plugin subcommand")
  public void completePluginSubcommand() {
    final List<String> candidates = session.complete("load --");
    assertThat(candidates, hasItems("--info-level", "--load-row-counts", "--attributes-file"));
  }

  @Test
  @DisplayName("Exit commands end the session")
  public void exitCommands() {
    assertThrows(EndOfFileException.class, () -> session.execute("quit"));
    assertThrows(EndOfFileException.class, () -> session.execute("terminate"));
  }

  @Test
  @DisplayName("Clear the screen on a terminal without screen control")
  public void clearScreen() throws Exception {
    session.execute("cls");
    session.execute("clear");
  }

  @Test
  @DisplayName("Run help")
  public void help() throws Exception {
    session.execute("help");
  }

  @Test
  @DisplayName("Keep Windows paths intact")
  public void windowsPath() {
    final String path = "C:\\Users\\sualeh\\config.properties";
    final List<String> words =
        session.reader().getParser().parse("connect --server hsqldb " + path, 0).words();
    assertThat(words.get(words.size() - 1), is(path));
  }

  @Test
  @DisplayName("A bad command does not end the session")
  public void badCommand() throws Exception {
    final Exception exception =
        assertThrows(Exception.class, () -> session.execute("no-such-command"));
    assertThat(exception instanceof EndOfFileException, is(false));

    session.execute("clear");
    assertThrows(EndOfFileException.class, () -> session.execute("quit"));
  }
}
