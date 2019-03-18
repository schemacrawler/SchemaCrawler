package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.tools.commandline.Command;
import schemacrawler.tools.commandline.CommandParser;

public class CommandParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final CommandParser optionsParser = new CommandParser();

    assertThrows(CommandLine.MissingParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final CommandParser optionsParser = new CommandParser();

    assertThrows(CommandLine.MissingParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void commandNoValue()
  {
    final String[] args = { "--command" };

    final CommandParser optionsParser = new CommandParser();
    assertThrows(CommandLine.MissingParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--command", "a_command", "additional", "--extra" };

    final CommandParser optionsParser = new CommandParser();
    final Command options = optionsParser.parse(args);

    assertThat(options.toString(), is("a_command"));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(new String[] {
      "additional", "--extra" }));
  }

}
