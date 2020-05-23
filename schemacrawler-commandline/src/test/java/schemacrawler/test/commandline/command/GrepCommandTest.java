package schemacrawler.test.commandline.command;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.CommandlineTestUtility.runCommandInTest;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.GrepOptions;
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

    final GrepOptions grepOptions = schemaCrawlerOptions.getGrepOptions();
    assertThat(grepOptions.isGrepColumns(), is(false));
    assertThat(grepOptions.isGrepRoutineParameters(), is(false));
    assertThat(grepOptions.isGrepDefinitions(), is(false));
    assertThat(grepOptions.isGrepInvertMatch(), is(false));
    assertThat(grepOptions.isGrepOnlyMatching(), is(false));
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

    final GrepOptions grepOptions = schemaCrawlerOptions.getGrepOptions();
    assertThat(grepOptions.isGrepColumns(), is(false));
    assertThat(grepOptions.isGrepRoutineParameters(), is(false));
    assertThat(grepOptions.isGrepDefinitions(), is(false));
    assertThat(grepOptions.isGrepInvertMatch(), is(false));
    assertThat(grepOptions.isGrepOnlyMatching(), is(false));
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

    final GrepOptions grepOptions = schemaCrawlerOptions.getGrepOptions();
    assertThat(grepOptions.isGrepColumns(), is(true));
    assertThat(grepOptions
                 .getGrepColumnInclusionRule()
                 .get(),
               is(new RegularExpressionInclusionRule(Pattern.compile(
                 "new.*pattern[1-3]"))));

    assertThat(grepOptions.isGrepRoutineParameters(), is(true));
    assertThat(grepOptions
                 .getGrepRoutineParameterInclusionRule()
                 .get(),
               is(new RegularExpressionInclusionRule(Pattern.compile(
                 "new.*pattern[4-6]"))));

    assertThat(grepOptions.isGrepDefinitions(), is(true));
    assertThat(grepOptions
                 .getGrepDefinitionInclusionRule()
                 .get(),
               is(new RegularExpressionInclusionRule(Pattern.compile(
                 "new.*pattern[7-9]"))));

    assertThat(grepOptions.isGrepInvertMatch(), is(true));
    assertThat(grepOptions.isGrepOnlyMatching(), is(true));
  }

}
