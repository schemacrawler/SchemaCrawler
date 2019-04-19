package schemacrawler.test.commandline.parser;


import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.CommandlineTestUtility.runCommandInTest;
import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.commandline.command.LimitCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;

public class LimitCommandTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    newCommandLine(new LimitCommand(state))
      .parseWithHandlers(new CommandLine.RunLast(),
                         new CommandLine.DefaultExceptionHandler<>(),
                         args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.getSchemaInclusionRule(),
               is(new IncludeAll()));
    assertThat(schemaCrawlerOptions.getSynonymInclusionRule(),
               is(new ExcludeAll()));
    assertThat(schemaCrawlerOptions.getSequenceInclusionRule(),
               is(new ExcludeAll()));

    assertThat(schemaCrawlerOptions.getTableInclusionRule(),
               is(new IncludeAll()));
    assertThat(schemaCrawlerOptions.getColumnInclusionRule(),
               is(new IncludeAll()));
    assertThat(schemaCrawlerOptions.getTableTypes(),
               hasItems("TABLE", "BASE TABLE", "VIEW"));

    assertThat(schemaCrawlerOptions.getRoutineInclusionRule(),
               is(new ExcludeAll()));
    assertThat(schemaCrawlerOptions.getRoutineColumnInclusionRule(),
               is(new ExcludeAll()));
    assertThat(schemaCrawlerOptions.getRoutineTypes(),
               hasItems(RoutineType.function, RoutineType.procedure));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    runCommandInTest(new LimitCommand(state), args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.getParentTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.getChildTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.isNoEmptyTables(), is(false));
  }

  @Test
  public void routinesBadValue()
  {
    final String[] args = { "--routines", "[" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> runCommandInTest(new LimitCommand(state), args));
  }

  @Test
  public void tablesBadValue()
  {
    final String[] args = { "--tables", "[" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> runCommandInTest(new LimitCommand(state), args));
  }

  @Test
  public void tablesNoValue()
  {
    final String[] args = { "--tables" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> runCommandInTest(new LimitCommand(state), args));
  }

  @Test
  public void routinesNoValue()
  {
    final String[] args = { "--routines" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> runCommandInTest(new LimitCommand(state), args));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--parents",
      "2",
      "--children",
      "2",
      "--no-empty-tables=true",
      "additional",
      "-extra" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    newCommandLine(new LimitCommand(state))
      .parseWithHandlers(new CommandLine.RunLast(),
                         new CommandLine.DefaultExceptionHandler<>(),
                         args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.getSchemaInclusionRule(),
               is(new IncludeAll()));
    assertThat(schemaCrawlerOptions.getSynonymInclusionRule(),
               is(new ExcludeAll()));
    assertThat(schemaCrawlerOptions.getSequenceInclusionRule(),
               is(new ExcludeAll()));

    assertThat(schemaCrawlerOptions.getTableInclusionRule(),
               is(new IncludeAll()));
    assertThat(schemaCrawlerOptions.getColumnInclusionRule(),
               is(new IncludeAll()));
    assertThat(schemaCrawlerOptions.getTableTypes(),
               hasItems("TABLE", "BASE TABLE", "VIEW"));

    assertThat(schemaCrawlerOptions.getRoutineInclusionRule(),
               is(new ExcludeAll()));
    assertThat(schemaCrawlerOptions.getRoutineColumnInclusionRule(),
               is(new ExcludeAll()));
    assertThat(schemaCrawlerOptions.getRoutineTypes(),
               hasItems(RoutineType.function, RoutineType.procedure));
  }

}
