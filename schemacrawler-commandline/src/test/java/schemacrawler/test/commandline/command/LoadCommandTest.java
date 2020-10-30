package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.tools.commandline.command.LoadCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;

public class LoadCommandTest {

  @Test
  public void infoLevelBadValue() {
    final String[] args = {"--info-level", "someinfolvl"};

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final LoadCommand optionsParser = new LoadCommand(state);

    assertThrows(
        CommandLine.ParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void infoLevelNoValue() {
    final String[] args = {"--info-level"};

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final LoadCommand optionsParser = new LoadCommand(state);

    assertThrows(
        CommandLine.ParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void infoLevelWithValue() {
    final String[] args = {"--info-level", "detailed", "additional", "-extra"};

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final LoadCommand optionsParser = new LoadCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);

    final InfoLevel schemaInfoLevel = optionsParser.getInfoLevel();

    assertThat(schemaInfoLevel, is(InfoLevel.detailed));
    assertThat(optionsParser.isLoadRowCounts(), is(false));
  }

  @Test
  public void loadRowCountsWithoutValue() {
    final String[] args = {"--info-level", "detailed", "--load-row-counts", "additional", "-extra"};

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final LoadCommand optionsParser = new LoadCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);

    final InfoLevel schemaInfoLevel = optionsParser.getInfoLevel();

    assertThat(schemaInfoLevel, is(InfoLevel.detailed));
    assertThat(optionsParser.isLoadRowCounts(), is(true));
  }

  @Test
  public void loadRowCountsWithValue() {
    final String[] args = {
      "--info-level", "detailed", "--load-row-counts", "true", "additional", "-extra"
    };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final LoadCommand optionsParser = new LoadCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);

    final InfoLevel schemaInfoLevel = optionsParser.getInfoLevel();

    assertThat(schemaInfoLevel, is(InfoLevel.detailed));
    assertThat(optionsParser.isLoadRowCounts(), is(true));
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final LoadCommand optionsParser = new LoadCommand(state);

    assertThrows(
        CommandLine.ParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    final LoadCommand optionsParser = new LoadCommand(state);

    assertThrows(
        CommandLine.ParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }
}
