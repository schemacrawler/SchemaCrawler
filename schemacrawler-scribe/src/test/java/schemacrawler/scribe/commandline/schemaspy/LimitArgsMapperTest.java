/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForRoutineInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSequenceInclusion;
import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSynonymInclusion;

import java.util.List;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.scribe.commandline.schemaspy.LimitArgsMapper.LimitArgs;
import schemacrawler.tools.commandline.command.LimitCommand;
import schemacrawler.tools.commandline.state.ShellState;

public class LimitArgsMapperTest {

  private static LimitArgsMapper mapper(
      final String catalog,
      final String schema,
      final String schemas,
      final String schemaRegex,
      final String includeTableRegex,
      final String excludeTableRegex) {
    return new LimitArgsMapper(
        new LimitArgs(catalog, schema, schemas, schemaRegex, includeTableRegex, excludeTableRegex));
  }

  @Test
  public void catalogOnlySelectsAllSchemasInCatalog() {
    final List<String> args = mapper("mycat", null, null, null, null, null).toArgs();
    assertThat(args, hasItem("--schemas"));
    assertThat(args, hasItem("\\Qmycat\\E\\..*"));
    assertThat(args, not(hasItem("--catalogs")));
    assertThat(args, hasItem("--routines"));
    assertThat(args, hasItem("--sequences"));
    assertThat(args, hasItem("--synonyms"));
  }

  @Test
  public void catalogAndSchemaAreFoldedTogether() {
    final List<String> args = mapper("mycat", "public", null, null, null, null).toArgs();
    assertThat(args, hasItem("\\Qmycat\\E\\.\\Qpublic\\E"));
  }

  @Test
  public void schemasListConvertsToRegex() {
    final List<String> args = mapper(null, null, "schema1,schema2", null, null, null).toArgs();
    final String pattern = args.get(args.indexOf("--schemas") + 1);
    assertThat(pattern, containsString("\\Qschema1\\E"));
    assertThat(pattern, containsString("\\Qschema2\\E"));
  }

  @Test
  public void schemaSpecTakesPriorityOverList() {
    final List<String> args =
        mapper(null, "single", "list1,list2", ".*REGEX.*", null, null).toArgs();
    final String pattern = args.get(args.indexOf("--schemas") + 1);
    assertThat(pattern, containsString("REGEX"));
    assertThat(pattern, not(containsString("single")));
    assertThat(pattern, not(containsString("list1")));
  }

  @Test
  public void includeAndExcludeTablesAreCombined() {
    final List<String> args = mapper(null, null, null, null, ".*PUBLIC.*", ".*TEMP.*").toArgs();
    final String pattern = args.get(args.indexOf("--tables") + 1);
    assertThat(pattern, containsString("(?="));
    assertThat(pattern, containsString("(?!"));
  }

  @Test
  public void argsParseWithLimitCommand() {
    final List<String> args = mapper("mycat", "public", null, null, null, null).toArgs();
    final LimitCommand command = new LimitCommand(new ShellState());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    final LimitCommand statefulCommand = new LimitCommand(state);
    final CommandLine statefulCommandLine = new CommandLine(statefulCommand);
    statefulCommandLine.setUnmatchedArgumentsAllowed(true);

    assertDoesNotThrow(() -> statefulCommandLine.execute(args.toArray(String[]::new)));
    assertThat(
        state.getSchemaCrawlerOptions().limitOptions().get(ruleForSchemaInclusion).toString(),
        containsString("mycat"));
    assertThat(
        state.getSchemaCrawlerOptions().limitOptions().get(ruleForSchemaInclusion).toString(),
        containsString("public"));
    assertThat(
        state.getSchemaCrawlerOptions().limitOptions().get(ruleForRoutineInclusion),
        is(new IncludeAll()));
    assertThat(
        state.getSchemaCrawlerOptions().limitOptions().get(ruleForSequenceInclusion),
        is(new IncludeAll()));
    assertThat(
        state.getSchemaCrawlerOptions().limitOptions().get(ruleForSynonymInclusion),
        is(new IncludeAll()));

    final CommandLine commandLine = new CommandLine(command);
    commandLine.setUnmatchedArgumentsAllowed(true);

    assertDoesNotThrow(() -> commandLine.parseArgs(args.toArray(String[]::new)));
  }
}
