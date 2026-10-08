/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static schemacrawler.test.utility.CommandlineTestUtility.createLoadedSchemaCrawlerShellState;
import static schemacrawler.test.utility.CommandlineTestUtility.executeCommandInTest;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static us.fatehi.test.utility.extensions.FileHasContent.contentsOf;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.commandline.command.FilterCommand;
import schemacrawler.tools.commandline.command.GrepCommand;
import schemacrawler.tools.commandline.shell.SweepCommand;
import schemacrawler.tools.commandline.shell.SystemCommand;
import schemacrawler.tools.commandline.state.ShellState;
import us.fatehi.test.utility.extensions.CaptureSystemStreams;
import us.fatehi.test.utility.extensions.CapturedSystemStreams;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@CaptureSystemStreams
public class LoadedShellCommandsTest {

  @Test
  public void projectionOptionChangesKeepLoadedBaseline() throws Throwable {
    final Catalog baseline = mock(Catalog.class);
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());
    state.setCatalog(baseline);
    state.markCatalogLoaded();

    executeCommandInTest(new GrepCommand(state), new String[] {"--grep-tables", ".*\\.BOOKS"});
    executeCommandInTest(new FilterCommand(state), new String[] {"--parents", "1"});

    assertThat(state.isLoaded(), is(true));
    assertThat(state.isCatalogStale(), is(false));
    assertThat(state.getCatalog() == baseline, is(true));
  }

  @Test
  public void loadAndLimitChangesRequireReload() {
    final Catalog baseline = mock(Catalog.class);
    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());
    state.setCatalog(baseline);
    state.markCatalogLoaded();

    state.withLimitOptions(
        LimitOptionsBuilder.builder()
            .includeTables(new RegularExpressionInclusionRule(".*\\.BOOKS"))
            .toOptions());

    assertThat(state.isLoaded(), is(false));
    assertThat(state.isCatalogStale(), is(true));
    assertThat(state.getCatalog() == baseline, is(true));

    state.markCatalogLoaded();
    state.withLoadOptions(
        LoadOptionsBuilder.builder().withInfoLevel(InfoLevel.detailed).toOptions());

    assertThat(state.isLoaded(), is(false));
    assertThat(state.isCatalogStale(), is(true));
    assertThat(state.getCatalog() == baseline, is(true));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  @WithTestDatabase
  public void isLoaded(
      final DatabaseConnectionSource connectionSource, final CapturedSystemStreams streams) {
    final ShellState state = createLoadedSchemaCrawlerShellState(connectionSource);

    final String[] args = {"--is-loaded"};

    final SystemCommand optionsParser = new SystemCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), containsString("Database metadata is loaded"));
  }

  @Test
  @WithTestDatabase
  public void isNotConnected(
      final DatabaseConnectionSource connectionSource, final CapturedSystemStreams streams) {
    final ShellState state = new ShellState();
    state.setConnectionSource(connectionSource); // is-connected

    final String[] args = {"--is-loaded"};

    final SystemCommand optionsParser = new SystemCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(contentsOf(streams.out()), containsString("Database metadata is not loaded"));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  @WithTestDatabase
  public void sweepCatalog(final DatabaseConnectionSource connectionSource) {
    final ShellState state = createLoadedSchemaCrawlerShellState(connectionSource);

    final String[] args = {};

    assertThat(state.getCatalog(), is(not(nullValue())));

    final SweepCommand optionsParser = new SweepCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getCatalog(), is(nullValue()));
  }

  @Test
  @WithTestDatabase
  public void sweepCatalogWithNoState() {
    final ShellState state = new ShellState();

    final String[] args = {};

    assertThat(state.getCatalog(), is(nullValue()));

    final SweepCommand optionsParser = new SweepCommand(state);
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.execute(args);

    assertThat(state.getCatalog(), is(nullValue()));
  }
}
