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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.scribe.commandline.schemaspy.ExecuteArgsMapper.ExecuteArgs;
import schemacrawler.tools.commandline.command.CommandOptions;
import schemacrawler.tools.commandline.command.CommandOutputOptions;
import schemacrawler.tools.commandline.command.ExecuteCommand;
import schemacrawler.tools.commandline.state.ShellState;

public class ExecuteArgsMapperTest {

  private static ExecuteArgsMapper mapper(final String outputPath, final String locale) {
    return new ExecuteArgsMapper(new ExecuteArgs(outputPath, locale));
  }

  @Test
  public void includesOutputArguments() {
    final List<String> args = mapper("schemaspy-output.zip", null).toArgs();
    assertThat(
        args,
        contains(
            "--command",
            "scribe",
            "--output-format",
            "okf",
            "--output-file",
            "schemaspy-output.zip"));
  }

  @Test
  public void includesLocaleWhenPresent() {
    final List<String> args = mapper("out.zip", "en-US").toArgs();
    assertThat(args, hasItem("--language"));
    assertThat(args, hasItem("en-US"));
  }

  @Test
  public void omitsLocaleWhenBlank() {
    final List<String> args = mapper("out.zip", null).toArgs();
    assertThat(args, not(hasItem("--language")));
  }

  @Test
  public void argsParseWithExecuteCommand() {
    final List<String> args = mapper("out.zip", "de-DE").toArgs();
    final ExecuteCommand command = new ExecuteCommand(new ShellState());
    final CommandLine commandLine = new CommandLine(command);
    commandLine.setUnmatchedArgumentsAllowed(true);

    assertDoesNotThrow(() -> commandLine.parseArgs(args.toArray(String[]::new)));
    final CommandOptions commandOptions =
        fieldValue(command, "commandOptions", CommandOptions.class);
    final CommandOutputOptions commandOutputOptions =
        fieldValue(command, "commandOutputOptions", CommandOutputOptions.class);
    assertThat(commandOptions.getCommand(), is("scribe"));
    assertThat(commandOutputOptions.getOutputFormatValue().orElseThrow(), is("okf"));
    assertThat(
        commandOutputOptions.getOutputFile().orElseThrow().getFileName().toString(), is("out.zip"));
  }

  private static <T> T fieldValue(
      final Object target, final String fieldName, final Class<T> fieldType) {
    try {
      final Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return assertInstanceOf(fieldType, field.get(target));
    } catch (final ReflectiveOperationException e) {
      throw new RuntimeException("Could not read field <" + fieldName + ">", e);
    }
  }
}
