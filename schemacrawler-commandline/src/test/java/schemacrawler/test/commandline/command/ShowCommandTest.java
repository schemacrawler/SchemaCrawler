package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.CommandlineTestUtility.runCommandInTest;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.commandline.command.ShowCommand;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class ShowCommandTest {

  @Test
  public void allArgs() {
    final String[] args = {
      "--no-info",
      "--no-remarks",
      "--weak-associations",
      "--portable-names",
      "additional",
      "--extra"
    };

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    runCommandInTest(new ShowCommand(state), args);

    final SchemaTextOptionsBuilder builder =
        SchemaTextOptionsBuilder.builder().fromConfig(state.getAdditionalConfiguration());

    final SchemaTextOptions options = builder.toOptions();

    assertThat(options.isNoInfo(), is(true));
    assertThat(options.isHideRemarks(), is(true));
    assertThat(options.isShowWeakAssociations(), is(true));
    // --
    assertThat(options.isHideTableConstraintNames(), is(true));
    assertThat(options.isHideTableConstraintNames(), is(true));
    assertThat(options.isHideForeignKeyNames(), is(true));
    assertThat(options.isHideIndexNames(), is(true));
    assertThat(options.isHidePrimaryKeyNames(), is(true));
    assertThat(options.isHideTriggerNames(), is(true));
    assertThat(options.isHideRoutineSpecificNames(), is(true));
    assertThat(options.isShowUnqualifiedNames(), is(true));
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    runCommandInTest(new ShowCommand(state), args);

    final SchemaTextOptionsBuilder builder =
        SchemaTextOptionsBuilder.builder().fromConfig(state.getAdditionalConfiguration());

    assertThat(
        "No options are set",
        builder.toOptions(),
        is(equalTo(SchemaTextOptionsBuilder.builder().toOptions())));
  }

  @Test
  public void noInfoFalse() {
    final String[] args = {"--no-info=false"};

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    runCommandInTest(new ShowCommand(state), args);

    final SchemaTextOptionsBuilder builder =
        SchemaTextOptionsBuilder.builder().fromConfig(state.getAdditionalConfiguration());

    assertThat(
        "No options are set",
        builder.toOptions(),
        is(equalTo(SchemaTextOptionsBuilder.builder().toOptions())));
  }

  @Test
  public void noInfoTrue() {
    final String[] args = {"--no-info=true"};

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    runCommandInTest(new ShowCommand(state), args);

    final SchemaTextOptionsBuilder builder =
        SchemaTextOptionsBuilder.builder().fromConfig(state.getAdditionalConfiguration());

    assertThat(builder.toOptions().isNoInfo(), is(true));
    assertThat(builder.toOptions().isHideRemarks(), is(false));
    assertThat(builder.toOptions().isShowWeakAssociations(), is(false));
    // --
    assertThat(builder.toOptions().isHideTableConstraintNames(), is(false));
    assertThat(builder.toOptions().isHideTableConstraintNames(), is(false));
    assertThat(builder.toOptions().isHideForeignKeyNames(), is(false));
    assertThat(builder.toOptions().isHideIndexNames(), is(false));
    assertThat(builder.toOptions().isHidePrimaryKeyNames(), is(false));
    assertThat(builder.toOptions().isHideTriggerNames(), is(false));
    assertThat(builder.toOptions().isHideRoutineSpecificNames(), is(false));
    assertThat(builder.toOptions().isShowUnqualifiedNames(), is(false));
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    runCommandInTest(new ShowCommand(state), args);

    final SchemaTextOptionsBuilder builder =
        SchemaTextOptionsBuilder.builder().fromConfig(state.getAdditionalConfiguration());

    assertThat(
        "No options are set",
        builder.toOptions(),
        is(equalTo(SchemaTextOptionsBuilder.builder().toOptions())));
  }
}
