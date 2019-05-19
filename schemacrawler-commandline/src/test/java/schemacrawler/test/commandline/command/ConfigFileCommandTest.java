package schemacrawler.test.commandline.command;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.is;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.command.ConfigFileCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;

public class ConfigFileCommandTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final CommandLine commandLine = newCommandLine(new ConfigFileCommand(state),
                                                   null,
                                                   false);
    commandLine.execute(args);
    final Config config = state.getBaseConfiguration();

    assertThat("Config is not empty", config, is(anEmptyMap()));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final CommandLine commandLine = newCommandLine(new ConfigFileCommand(state),
                                                   null,
                                                   false);
    commandLine.execute(args);
    final Config config = state.getBaseConfiguration();

    assertThat("Config is not empty", config, is(anEmptyMap()));
  }

  @Test
  public void commandNoValue()
  {
    final String[] args = { "-g" };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final CommandLine commandLine = newCommandLine(new ConfigFileCommand(state),
                                                   null,
                                                   false);
    commandLine.execute(args);
    final Config config = state.getBaseConfiguration();

    assertThat("Config is not null", config, is(anEmptyMap()));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "-g", "a_file", "additional", "--extra"
    };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final CommandLine commandLine = newCommandLine(new ConfigFileCommand(state),
                                                   null,
                                                   false);
    commandLine.execute(args);
    final Config config = state.getBaseConfiguration();

    assertThat("Config is not empty", config, is(anEmptyMap()));
  }

}
