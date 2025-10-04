/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.tools.commandline.command.CommandOutputOptions;

public class CommandOutputOptionsTest {

  @Test
  public void allArgs() {
    final String[] args = {
      "--output-file", "file.txt", "--output-format", "tables.js", "additional", "-extra"
    };

    final CommandOutputOptions options = new CommandOutputOptions();
    final CommandLine commandLine = newCommandLine(options, null);
    commandLine.parseArgs(args);

    assertThat(
        options
            .getOutputFile()
            .orElseThrow(() -> new IllegalArgumentException("No file found"))
            .getFileName(),
        is(Path.of("file.txt")));
    assertThat(options.getOutputFormatValue(), is(Optional.of("tables.js")));
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final CommandOutputOptions options = new CommandOutputOptions();
    final CommandLine commandLine = newCommandLine(options, null);
    commandLine.parseArgs(args);

    assertThat(options.getOutputFile().isPresent(), is(false));
    assertThat(options.getOutputFormatValue().isPresent(), is(false));
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final CommandOutputOptions options = new CommandOutputOptions();
    final CommandLine commandLine = newCommandLine(options, null);
    commandLine.parseArgs(args);

    assertThat(options.getOutputFile().isPresent(), is(false));
    assertThat(options.getOutputFormatValue().isPresent(), is(false));
  }

  @Test
  public void outputfileNoValue() {
    final String[] args = {"--output-file"};

    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(new CommandOutputOptions(), null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void outputformatNoValue() {
    final String[] args = {"--output-format"};

    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(new CommandOutputOptions(), null);
          commandLine.parseArgs(args);
        });
  }
}
