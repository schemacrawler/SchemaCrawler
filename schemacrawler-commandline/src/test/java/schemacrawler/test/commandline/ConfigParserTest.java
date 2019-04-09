package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

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

    final Config config = CommandLine
      .call(new ConfigParser(new Config()), args);

    assertThat("Config is not empty", config.isEmpty(), is(true));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final Config config = CommandLine
      .call(new ConfigParser(new Config()), args);

    assertThat("Config is not empty", config.isEmpty(), is(true));
  }

  @Test
  public void commandNoValue()
  {
    final String[] args = { "-g" };

    final Config config = CommandLine
      .call(new ConfigParser(new Config()), args);

    assertThat("Config is not null", config, is(nullValue()));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "-g", "a_file", "additional", "--extra" };

    final Config config = CommandLine
      .call(new ConfigParser(new Config()), args);

    assertThat("Config is not empty", config.isEmpty(), is(true));
  }

}
