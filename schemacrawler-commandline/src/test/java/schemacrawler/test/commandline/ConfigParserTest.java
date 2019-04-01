package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.ConfigParser;

public class ConfigParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final ConfigParser optionsParser = new ConfigParser();
    optionsParser.parse(args);
    final Config config = optionsParser.getConfig();

    assertThat("Config is not empty", config.isEmpty(), is(true));

    final String[] remainder = optionsParser.getRemainder();
    assertThat("Remainder is not empty", remainder.length, is(0));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final ConfigParser optionsParser = new ConfigParser();
    optionsParser.parse(args);
    final Config config = optionsParser.getConfig();

    assertThat("Config is not empty", config.isEmpty(), is(true));

    final String[] remainder = optionsParser.getRemainder();
    assertThat("Remainder is not empty", remainder.length, is(1));
  }

  @Test
  public void commandNoValue()
  {
    final String[] args = { "-g" };

    final ConfigParser optionsParser = new ConfigParser();
    assertThrows(CommandLine.MissingParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "-g", "a_file", "additional", "--extra" };

    final ConfigParser optionsParser = new ConfigParser();
    optionsParser.parse(args);
    final Config config = optionsParser.getConfig();

    assertThat("Config is not empty", config.isEmpty(), is(true));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(new String[] {
      "additional", "--extra" }));
  }

}
