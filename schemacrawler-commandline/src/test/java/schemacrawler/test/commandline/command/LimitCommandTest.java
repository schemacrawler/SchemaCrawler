package schemacrawler.test.commandline.command;


import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.CommandlineTestUtility.runCommandInTest;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.*;
import schemacrawler.tools.commandline.command.LimitCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.commandline.state.StateFactory;

public class LimitCommandTest
{

  private static void runBadCommand(final String[] args)
  {
    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder.builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> runCommandInTest(new LimitCommand(state), args));
  }

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder.builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    newCommandLine(LimitCommand.class,
                   new StateFactory(state)).parseWithHandlers(new CommandLine.RunLast(),
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

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder.builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    runCommandInTest(new LimitCommand(state), args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.getParentTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.getChildTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.isNoEmptyTables(), is(false));
  }

  @Test
  public void schemasBadValue()
  {
    runBadCommand(new String[] { "--schemas", "[" });
  }

  @Test
  public void synonymsBadValue()
  {
    runBadCommand(new String[] { "--synonyms", "[" });
  }

  @Test
  public void sequencesBadValue()
  {
    runBadCommand(new String[] { "--sequences", "[" });
  }

  @Test
  public void routinesBadValue()
  {
    runBadCommand(new String[] { "--routines", "[" });
  }

  @Test
  public void tablesBadValue()
  {
    runBadCommand(new String[] { "--tables", "[" });
  }

  @Test
  public void excludeColumnsBadValue()
  {
    runBadCommand(new String[] { "--exclude-columns", "[" });
  }

  @Test
  public void excludeInOutBadValue()
  {
    runBadCommand(new String[] { "--exclude-in-out", "[" });
  }

  @Test
  public void tablesNoValue()
  {
    runBadCommand(new String[] { "--tables" });
  }

  @Test
  public void routinesNoValue()
  {
    runBadCommand(new String[] { "--routines" });
  }

  @Test
  public void schemasNoValue()
  {
    runBadCommand(new String[] { "--schemas" });
  }

  @Test
  public void sequencesNoValue()
  {
    runBadCommand(new String[] { "--sequences" });
  }

  @Test
  public void synonymsNoValue()
  {
    runBadCommand(new String[] { "--synonyms" });
  }

  @Test
  public void excludeColumnsNoValue()
  {
    runBadCommand(new String[] { "--exclude-columns" });
  }

  @Test
  public void excludeInOutNoValue()
  {
    runBadCommand(new String[] { "--exclude-in-out" });
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--tables",
      ".*regexp.*",
      "--routines",
      ".*regexp.*",
      "--schemas",
      ".*regexp.*",
      "--sequences",
      ".*regexp.*",
      "--synonyms",
      ".*regexp.*",
      "--exclude-columns",
      ".*regexp.*",
      "--exclude-in-out",
      ".*regexp.*",
      "--table-types",
      "CHAIR",
      "--routine-types",
      "FUNCtion",
      "additional",
      "-extra"
    };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder.builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    newCommandLine(LimitCommand.class,
                   new StateFactory(state)).parseWithHandlers(new CommandLine.RunLast(),
                                                              new CommandLine.DefaultExceptionHandler<>(),
                                                              args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.getSchemaInclusionRule(),
               is(new RegularExpressionInclusionRule(".*regexp.*")));
    assertThat(schemaCrawlerOptions.getSynonymInclusionRule(),
               is(new RegularExpressionInclusionRule(".*regexp.*")));
    assertThat(schemaCrawlerOptions.getSequenceInclusionRule(),
               is(new RegularExpressionInclusionRule(".*regexp.*")));

    assertThat(schemaCrawlerOptions.getTableInclusionRule(),
               is(new RegularExpressionInclusionRule(".*regexp.*")));
    assertThat(schemaCrawlerOptions.getColumnInclusionRule(),
               is(new RegularExpressionExclusionRule(".*regexp.*")));
    assertThat(schemaCrawlerOptions.getTableTypes(), hasItems("CHAIR"));

    assertThat(schemaCrawlerOptions.getRoutineInclusionRule(),
               is(new RegularExpressionInclusionRule(".*regexp.*")));
    assertThat(schemaCrawlerOptions.getRoutineColumnInclusionRule(),
               is(new RegularExpressionExclusionRule(".*regexp.*")));
    assertThat(schemaCrawlerOptions.getRoutineTypes(),
               hasItems(RoutineType.function));
  }

}
