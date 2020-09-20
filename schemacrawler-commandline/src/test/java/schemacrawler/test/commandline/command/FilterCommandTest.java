package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.CommandlineTestUtility.runCommandInTest;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import schemacrawler.schemacrawler.FilterOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.commandline.command.FilterCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.commandline.state.StateFactory;

public class FilterCommandTest {

  @Test
  public void allArgs() {
    final String[] args = {
      "--parents", "2", "--children", "2", "--no-empty-tables=true", "additional", "-extra"
    };

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    final CommandLine commandLine =
        newCommandLine(FilterCommand.class, new StateFactory(state), true);
    commandLine.execute(args);

    final FilterOptions filterOptions = state.getSchemaCrawlerOptions().getFilterOptions();

    assertThat(filterOptions.getParentTableFilterDepth(), is(2));
    assertThat(filterOptions.getChildTableFilterDepth(), is(2));
    assertThat(filterOptions.isNoEmptyTables(), is(true));
  }

  @Test
  public void childrenBadValue() {
    final String[] args = {"--children", "-1"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    assertThrows(
        CommandLine.ParameterException.class,
        () -> runCommandInTest(new FilterCommand(state), args));
  }

  @Test
  public void childrenNoValue() {
    final String[] args = {"--children"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    assertThrows(
        CommandLine.ParameterException.class,
        () -> runCommandInTest(new FilterCommand(state), args));
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    final CommandLine commandLine =
        newCommandLine(FilterCommand.class, new StateFactory(state), true);
    commandLine.parseArgs(args);
    final FilterOptions filterOptions = schemaCrawlerOptions.getFilterOptions();

    assertThat(filterOptions.getParentTableFilterDepth(), is(0));
    assertThat(filterOptions.getChildTableFilterDepth(), is(0));
    assertThat(filterOptions.isNoEmptyTables(), is(false));
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    runCommandInTest(new FilterCommand(state), args);
    final FilterOptions filterOptions = schemaCrawlerOptions.getFilterOptions();

    assertThat(filterOptions.getParentTableFilterDepth(), is(0));
    assertThat(filterOptions.getChildTableFilterDepth(), is(0));
    assertThat(filterOptions.isNoEmptyTables(), is(false));
  }

  @Test
  public void parentsBadValue() {
    final String[] args = {"--parents", "-1"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    assertThrows(
        CommandLine.ParameterException.class,
        () -> runCommandInTest(new FilterCommand(state), args));
  }

  @Test
  public void parentsNoValue() {
    final String[] args = {"--parents"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    assertThrows(
        CommandLine.ParameterException.class,
        () -> runCommandInTest(new FilterCommand(state), args));
  }
}
