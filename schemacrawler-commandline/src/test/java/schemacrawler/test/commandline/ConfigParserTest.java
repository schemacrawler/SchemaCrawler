package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.ConfigParser;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;

public class ConfigParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    picocli.CommandLine.run(new ConfigParser(state), args);
    final Config config = state.getBaseConfiguration();

    assertThat("Config is not empty", config, is(anEmptyMap()));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    picocli.CommandLine.run(new ConfigParser(state), args);
    final Config config = state.getBaseConfiguration();

    assertThat("Config is not empty", config, is(anEmptyMap()));
  }

  @Test
  public void commandNoValue()
  {
    final String[] args = { "-g" };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    picocli.CommandLine.run(new ConfigParser(state), args);
    final Config config = state.getBaseConfiguration();

    assertThat("Config is not null", config, is(anEmptyMap()));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "-g", "a_file", "additional", "--extra" };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    picocli.CommandLine.run(new ConfigParser(state), args);
    final Config config = state.getBaseConfiguration();

    assertThat("Config is not empty", config, is(anEmptyMap()));
  }

}
