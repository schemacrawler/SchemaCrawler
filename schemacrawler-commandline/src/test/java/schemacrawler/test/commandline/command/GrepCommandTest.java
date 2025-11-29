/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.CommandlineTestUtility.executeCommandInTest;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.GrepOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.commandline.command.GrepCommand;
import schemacrawler.tools.commandline.state.ShellState;

public class GrepCommandTest {

  @Test
  public void allArgs() throws Throwable {
    final String[] args = {
      "--grep-columns",
      "new.*pattern[1-3]",
      "--grep-parameters",
      "new.*pattern[4-6]",
      "--grep-def",
      "new.*pattern[7-9]",
      "--invert-match=true",
      "additional",
      "-extra"
    };

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executeCommandInTest(new GrepCommand(state), args);

    final GrepOptions grepOptions = state.getSchemaCrawlerOptions().grepOptions();
    assertThat(grepOptions.isGrepColumns(), is(true));
    assertThat(
        grepOptions.grepColumnInclusionRule(),
        is(new RegularExpressionInclusionRule(Pattern.compile("new.*pattern[1-3]"))));

    assertThat(grepOptions.isGrepRoutineParameters(), is(true));
    assertThat(
        grepOptions.grepRoutineParameterInclusionRule(),
        is(new RegularExpressionInclusionRule(Pattern.compile("new.*pattern[4-6]"))));

    assertThat(grepOptions.isGrepDefinitions(), is(true));
    assertThat(
        grepOptions.grepDefinitionInclusionRule(),
        is(new RegularExpressionInclusionRule(Pattern.compile("new.*pattern[7-9]"))));

    assertThat(grepOptions.isGrepInvertMatch(), is(true));
  }

  @Test
  public void grepColumnsBadValue() {
    final String[] args = {"--grep-columns", "[["};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    assertThrows(
        CommandLine.ParameterException.class,
        () -> executeCommandInTest(new GrepCommand(state), args));
  }

  @Test
  public void grepColumnsNoValue() {
    final String[] args = {"--grep-columns"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);

    assertThrows(
        CommandLine.ParameterException.class,
        () -> executeCommandInTest(new GrepCommand(state), args));
  }

  @Test
  public void noArgs() throws Throwable {
    final String[] args = {};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executeCommandInTest(new GrepCommand(state), args);

    final GrepOptions grepOptions = schemaCrawlerOptions.grepOptions();
    assertThat(grepOptions.isGrepColumns(), is(false));
    assertThat(grepOptions.isGrepRoutineParameters(), is(false));
    assertThat(grepOptions.isGrepDefinitions(), is(false));
    assertThat(grepOptions.isGrepInvertMatch(), is(false));
  }

  @Test
  public void noValidArgs() throws Throwable {
    final String[] args = {"--some-option"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executeCommandInTest(new GrepCommand(state), args);

    final GrepOptions grepOptions = schemaCrawlerOptions.grepOptions();
    assertThat(grepOptions.isGrepColumns(), is(false));
    assertThat(grepOptions.isGrepRoutineParameters(), is(false));
    assertThat(grepOptions.isGrepDefinitions(), is(false));
    assertThat(grepOptions.isGrepInvertMatch(), is(false));
  }
}
