package schemacrawler.test.commandline.command;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.CommandlineTestUtility.parseCommand;

import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.tools.commandline.command.CommandOutputOptions;

public class CommandOutputOptionsTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final CommandOutputOptions options = new CommandOutputOptions();
    parseCommand(options, args);

    assertThat(options.getOutputFile().isPresent(), is(false));
    assertThat(options.getOutputFormatValue().isPresent(), is(false));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final CommandOutputOptions options = new CommandOutputOptions();
    parseCommand(options, args);

    assertThat(options.getOutputFile().isPresent(), is(false));
    assertThat(options.getOutputFormatValue().isPresent(), is(false));
  }

  @Test
  public void outputfileNoValue()
  {
    final String[] args = { "--output-file" };

    assertThrows(CommandLine.MissingParameterException.class,
                 () -> parseCommand(new CommandOutputOptions(), args));
  }

  @Test
  public void outputformatNoValue()
  {
    final String[] args = { "--output-format" };

    assertThrows(CommandLine.MissingParameterException.class,
                 () -> parseCommand(new CommandOutputOptions(), args));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--output-file",
      "file.txt",
      "--output-format",
      "tables.js",
      "additional",
      "-extra"
    };

    final CommandOutputOptions options = new CommandOutputOptions();
    parseCommand(options, args);

    assertThat(options.getOutputFile()
                      .orElseThrow(() -> new IllegalArgumentException(
                        "No file found"))
                      .getFileName(), is(Paths.get("file.txt")));
    assertThat(options.getOutputFormatValue(), is(Optional.of("tables.js")));
  }

}
