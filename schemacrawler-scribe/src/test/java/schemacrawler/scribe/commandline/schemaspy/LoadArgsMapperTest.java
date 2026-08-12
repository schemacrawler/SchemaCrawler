/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.scribe.commandline.schemaspy.LoadArgsMapper.LoadArgs;
import schemacrawler.tools.commandline.command.LoadCommand;
import schemacrawler.tools.commandline.state.ShellState;

public class LoadArgsMapperTest {

  private static LoadArgsMapper mapper(final boolean noRows) {
    return new LoadArgsMapper(new LoadArgs(noRows));
  }

  @Test
  public void includesInfoLevelMaximum() {
    final List<String> args = mapper(false).toArgs();
    assertThat(args, contains("--info-level", "maximum", "--load-row-counts", "true"));
  }

  @Test
  public void omitsRowCountsWhenNoRowsSet() {
    final List<String> args = mapper(true).toArgs();
    assertThat(args, hasItem("--info-level"));
    assertThat(args, not(hasItem("--load-row-counts")));
  }

  @Test
  public void argsParseWithLoadCommand() {
    final List<String> args = mapper(false).toArgs();
    final LoadCommand command = new LoadCommand(new ShellState());
    final CommandLine commandLine = new CommandLine(command);
    commandLine.setUnmatchedArgumentsAllowed(true);

    assertDoesNotThrow(() -> commandLine.parseArgs(args.toArray(String[]::new)));
    assertThat(command.getInfoLevel(), org.hamcrest.Matchers.is(InfoLevel.maximum));
  }
}
