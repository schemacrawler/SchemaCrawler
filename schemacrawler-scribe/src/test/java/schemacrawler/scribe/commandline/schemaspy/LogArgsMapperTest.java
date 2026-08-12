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
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.scribe.commandline.schemaspy.LogArgsMapper.LogArgs;
import schemacrawler.tools.commandline.command.LogCommand;
import schemacrawler.tools.commandline.command.LogLevel;

public class LogArgsMapperTest {

  private static LogArgsMapper mapper(
      final String logLevel, final boolean debug, final String configFile) {
    return new LogArgsMapper(new LogArgs(logLevel, debug));
  }

  @Test
  public void defaultLogLevelIsInfo() {
    final List<String> args = mapper(null, false, null).toArgs();
    assertThat(args, contains("--log-level", "INFO"));
  }

  @Test
  public void debugOverridesLogLevel() {
    final List<String> args = mapper("warn", true, null).toArgs();
    assertThat(args, contains("--log-level", "FINE"));
  }

  @Test
  public void warnsMapToWarning() {
    final List<String> args = mapper("warn", false, null).toArgs();
    assertThat(args, contains("--log-level", "WARNING"));
  }

  @Test
  public void logArgsParseWithLogCommand() {
    final List<String> args = mapper("error", false, null).toArgs();
    final LogCommand command = new LogCommand();
    final CommandLine commandLine = new CommandLine(command);
    commandLine.setUnmatchedArgumentsAllowed(true);

    assertDoesNotThrow(() -> commandLine.parseArgs(args.toArray(String[]::new)));
    assertThat(command.getLogLevel(), is(LogLevel.SEVERE));
  }
}
