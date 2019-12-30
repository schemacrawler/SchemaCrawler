package schemacrawler.test.commandline.command;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.CommandlineTestUtility.runCommandInTest;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.commandline.command.SortCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class SortCommandTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    runCommandInTest(new SortCommand(state), args);

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder
      .builder()
      .fromConfig(state.getAdditionalConfiguration());

    assertThat("No options are set",
               builder.toOptions(),
               is(equalTo(SchemaTextOptionsBuilder
                            .builder()
                            .toOptions())));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    runCommandInTest(new SortCommand(state), args);

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder
      .builder()
      .fromConfig(state.getAdditionalConfiguration());

    assertThat("No options are set",
               builder.toOptions(),
               is(equalTo(SchemaTextOptionsBuilder
                            .builder()
                            .toOptions())));
  }

  @Test
  public void sortTablesFalse()
  {
    final String[] args = {
      "--sort-tables=false"
    };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    runCommandInTest(new SortCommand(state), args);

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder
      .builder()
      .fromConfig(state.getAdditionalConfiguration());

    assertThat("No options are set",
               builder.toOptions(),
               is(equalTo(SchemaTextOptionsBuilder
                            .builder()
                            .toOptions())));
  }

  @Test
  public void sortTablesTrue()
  {
    final String[] args = {
      "--sort-tables=true"
    };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    runCommandInTest(new SortCommand(state), args);

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder
      .builder()
      .fromConfig(state.getAdditionalConfiguration());
    assertThat(builder
                 .toOptions()
                 .isAlphabeticalSortForTables(), is(true));
    assertThat(builder
                 .toOptions()
                 .isAlphabeticalSortForTableColumns(), is(false));
    assertThat(builder
                 .toOptions()
                 .isAlphabeticalSortForRoutines(), is(false));
    assertThat(builder
                 .toOptions()
                 .isAlphabeticalSortForRoutineParameters(), is(false));

  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--sort-tables",
      "--sort-columns",
      "--sort-routines",
      "--sort-parameters",
      "additional",
      "--extra"
    };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    runCommandInTest(new SortCommand(state), args);

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder
      .builder()
      .fromConfig(state.getAdditionalConfiguration());

    assertThat(builder
                 .toOptions()
                 .isAlphabeticalSortForTables(), is(true));
    assertThat(builder
                 .toOptions()
                 .isAlphabeticalSortForTableColumns(), is(true));
    assertThat(builder
                 .toOptions()
                 .isAlphabeticalSortForRoutines(), is(true));
    assertThat(builder
                 .toOptions()
                 .isAlphabeticalSortForRoutineParameters(), is(true));

  }

}
