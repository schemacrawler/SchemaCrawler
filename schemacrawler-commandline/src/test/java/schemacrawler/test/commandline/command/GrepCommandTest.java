package schemacrawler.test.commandline.command;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.CommandlineTestUtility.runCommandInTest;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.commandline.command.GrepCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;

public class GrepCommandTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaCrawlerOptionsBuilder builder =
      SchemaCrawlerOptionsBuilder.builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    runCommandInTest(new GrepCommand(state), args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.isGrepColumns(), is(false));
    assertThat(schemaCrawlerOptions.isGrepRoutineParameters(), is(false));
    assertThat(schemaCrawlerOptions.isGrepDefinitions(), is(false));
    assertThat(schemaCrawlerOptions.isGrepInvertMatch(), is(false));
    assertThat(schemaCrawlerOptions.isGrepOnlyMatching(), is(false));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final SchemaCrawlerOptionsBuilder builder =
      SchemaCrawlerOptionsBuilder.builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    runCommandInTest(new GrepCommand(state), args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.isGrepColumns(), is(false));
    assertThat(schemaCrawlerOptions.isGrepRoutineParameters(), is(false));
    assertThat(schemaCrawlerOptions.isGrepDefinitions(), is(false));
    assertThat(schemaCrawlerOptions.isGrepInvertMatch(), is(false));
    assertThat(schemaCrawlerOptions.isGrepOnlyMatching(), is(false));
  }

  @Test
  public void grepColumnsBadValue()
  {
    final String[] args = { "--grep-columns", "[[" };

    final SchemaCrawlerOptionsBuilder builder =
      SchemaCrawlerOptionsBuilder.builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> runCommandInTest(new GrepCommand(state), args));
  }

  @Test
  public void grepColumnsNoValue()
  {
    final String[] args = { "--grep-columns" };

    final SchemaCrawlerOptionsBuilder builder =
      SchemaCrawlerOptionsBuilder.builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);

    assertThrows(CommandLine.ParameterException.class,
                 () -> runCommandInTest(new GrepCommand(state), args));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--grep-columns",
      "new.*pattern[1-3]",
      "--grep-parameters",
      "new.*pattern[4-6]",
      "--grep-def",
      "new.*pattern[7-9]",
      "--invert-match=true",
      "--only-matching=true",
      "additional",
      "-extra"
    };

    final SchemaCrawlerOptionsBuilder builder =
      SchemaCrawlerOptionsBuilder.builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    runCommandInTest(new GrepCommand(state), args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.isGrepColumns(), is(true));
    assertThat(schemaCrawlerOptions
                 .getGrepColumnInclusionRule()
                 .get(),
               is(new RegularExpressionInclusionRule(Pattern.compile(
                 "new.*pattern[1-3]"))));

    assertThat(schemaCrawlerOptions.isGrepRoutineParameters(), is(true));
    assertThat(schemaCrawlerOptions
                 .getGrepRoutineParameterInclusionRule()
                 .get(),
               is(new RegularExpressionInclusionRule(Pattern.compile(
                 "new.*pattern[4-6]"))));

    assertThat(schemaCrawlerOptions.isGrepDefinitions(), is(true));
    assertThat(schemaCrawlerOptions
                 .getGrepDefinitionInclusionRule()
                 .get(),
               is(new RegularExpressionInclusionRule(Pattern.compile(
                 "new.*pattern[7-9]"))));

    assertThat(schemaCrawlerOptions.isGrepInvertMatch(), is(true));
    assertThat(schemaCrawlerOptions.isGrepOnlyMatching(), is(true));
  }

}
