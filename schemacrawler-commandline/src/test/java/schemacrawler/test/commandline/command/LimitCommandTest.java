/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForColumnInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineParameterInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSequenceInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSynonymInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForTableInclusion;
import static schemacrawler.test.utility.CommandlineTestUtility.executeCommandInTest;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.FilterOptions;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.commandline.command.LimitCommand;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;

public class LimitCommandTest {

  private static void runBadCommand(final String[] args) {
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    assertThrows(
        CommandLine.ParameterException.class,
        () -> executeCommandInTest(new LimitCommand(state), args));
  }

  @Test
  public void allArgs() {
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
      "--exclude-parameters",
      ".*regexp.*",
      "--table-types",
      "CHAIR",
      "--routine-types",
      "FUNCtion",
      "additional",
      "-extra"
    };

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    final CommandLine commandLine = newCommandLine(LimitCommand.class, new StateFactory(state));
    commandLine.execute(args);

    final LimitOptions limitOptions = state.getSchemaCrawlerOptions().limitOptions();

    assertThat(
        limitOptions.get(ruleForSchemaInclusion),
        is(new RegularExpressionInclusionRule(".*regexp.*")));
    assertThat(
        limitOptions.get(ruleForSynonymInclusion),
        is(new RegularExpressionInclusionRule(".*regexp.*")));
    assertThat(
        limitOptions.get(ruleForSynonymInclusion),
        is(new RegularExpressionInclusionRule(".*regexp.*")));

    assertThat(
        limitOptions.get(ruleForTableInclusion),
        is(new RegularExpressionInclusionRule(".*regexp.*")));
    assertThat(
        limitOptions.get(ruleForColumnInclusion),
        is(new RegularExpressionExclusionRule(".*regexp.*")));
    assertThat(limitOptions.tableTypes().lookupTableType("CHAIR"), isPresent());

    assertThat(
        limitOptions.get(ruleForRoutineInclusion),
        is(new RegularExpressionInclusionRule(".*regexp.*")));
    assertThat(
        limitOptions.get(ruleForRoutineParameterInclusion),
        is(new RegularExpressionExclusionRule(".*regexp.*")));
    assertThat(limitOptions.routineTypes(), hasItems(RoutineType.function));
  }

  @Test
  public void excludeColumnsBadValue() {
    runBadCommand(new String[] {"--exclude-columns", "["});
  }

  @Test
  public void excludeColumnsNoValue() {
    runBadCommand(new String[] {"--exclude-columns"});
  }

  @Test
  public void excludeParameterBadValue() {
    runBadCommand(new String[] {"--exclude-parameters", "["});
  }

  @Test
  public void excludeParameterNoValue() {
    runBadCommand(new String[] {"--exclude-parameters"});
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    newCommandLine(LimitCommand.class, new StateFactory(state)).parseArgs(args);
    final LimitOptions limitOptions = schemaCrawlerOptions.limitOptions();

    assertThat(limitOptions.get(ruleForSchemaInclusion), is(new IncludeAll()));
    assertThat(limitOptions.get(ruleForSynonymInclusion), is(new ExcludeAll()));
    assertThat(limitOptions.get(ruleForSequenceInclusion), is(new ExcludeAll()));

    assertThat(limitOptions.get(ruleForTableInclusion), is(new IncludeAll()));
    assertThat(limitOptions.get(ruleForColumnInclusion), is(new IncludeAll()));
    assertThat(limitOptions.tableTypes().lookupTableType("TABLE"), isPresent());
    assertThat(limitOptions.tableTypes().lookupTableType("BASE TABLE"), isPresent());
    assertThat(limitOptions.tableTypes().lookupTableType("VIEW"), isPresent());

    assertThat(limitOptions.get(ruleForRoutineInclusion), is(new ExcludeAll()));
    assertThat(limitOptions.get(ruleForRoutineParameterInclusion), is(new IncludeAll()));
    assertThat(limitOptions.routineTypes(), hasItems(RoutineType.function, RoutineType.procedure));
  }

  @Test
  public void noValidArgs() throws Throwable {
    final String[] args = {"--some-option"};

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executeCommandInTest(new LimitCommand(state), args);
    final FilterOptions filterOptions = schemaCrawlerOptions.filterOptions();

    assertThat(filterOptions.parentTableFilterDepth(), is(0));
    assertThat(filterOptions.childTableFilterDepth(), is(0));
  }

  @Test
  public void routinesBadValue() {
    runBadCommand(new String[] {"--routines", "["});
  }

  @Test
  public void routinesNoValue() {
    runBadCommand(new String[] {"--routines"});
  }

  @Test
  public void schemasBadValue() {
    runBadCommand(new String[] {"--schemas", "["});
  }

  @Test
  public void schemasNoValue() {
    runBadCommand(new String[] {"--schemas"});
  }

  @Test
  public void sequencesBadValue() {
    runBadCommand(new String[] {"--sequences", "["});
  }

  @Test
  public void sequencesNoValue() {
    runBadCommand(new String[] {"--sequences"});
  }

  @Test
  public void synonymsBadValue() {
    runBadCommand(new String[] {"--synonyms", "["});
  }

  @Test
  public void synonymsNoValue() {
    runBadCommand(new String[] {"--synonyms"});
  }

  @Test
  public void tablesBadValue() {
    runBadCommand(new String[] {"--tables", "["});
  }

  @Test
  public void tablesNoValue() {
    runBadCommand(new String[] {"--tables"});
  }
}
