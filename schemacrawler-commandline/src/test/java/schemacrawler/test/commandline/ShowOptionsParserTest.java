package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.commandline.ShowOptionsParser;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class ShowOptionsParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder.builder();
    final ShowOptionsParser optionsParser = new ShowOptionsParser(builder);
    optionsParser.parse(args);

    assertThat("No options are set",
               builder.toOptions(),
               is(equalTo(SchemaTextOptionsBuilder.builder().toOptions())));

    final String[] remainder = optionsParser.getRemainder();
    assertThat("Remainder is not empty", remainder.length, is(0));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder.builder();
    final ShowOptionsParser optionsParser = new ShowOptionsParser(builder);
    optionsParser.parse(args);

    assertThat("No options are set",
               builder.toOptions(),
               is(equalTo(SchemaTextOptionsBuilder.builder().toOptions())));

    final String[] remainder = optionsParser.getRemainder();
    assertThat("Remainder is not empty", remainder.length, is(1));
  }

  @Test
  public void noInfoFalse()
  {
    final String[] args = {
      "--no-info=false" };

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder.builder();
    final ShowOptionsParser optionsParser = new ShowOptionsParser(builder);
    optionsParser.parse(args);

    assertThat("No options are set",
               builder.toOptions(),
               is(equalTo(SchemaTextOptionsBuilder.builder().toOptions())));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void noInfoTrue()
  {
    final String[] args = {
      "--no-info=true" };

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder.builder();
    final ShowOptionsParser optionsParser = new ShowOptionsParser(builder);
    optionsParser.parse(args);

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
    // --

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--no-info",
      "--no-remarks",
      "--weak-associations",
      "--portable-names",
      "additional",
      "--extra" };

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder.builder();
    final ShowOptionsParser optionsParser = new ShowOptionsParser(builder);
    optionsParser.parse(args);

    assertThat(builder.toOptions().isNoInfo(), is(true));
    assertThat(builder.toOptions().isHideRemarks(), is(true));
    assertThat(builder.toOptions().isShowWeakAssociations(), is(true));
    // --
    assertThat(builder.toOptions().isHideTableConstraintNames(), is(true));
    assertThat(builder.toOptions().isHideTableConstraintNames(), is(true));
    assertThat(builder.toOptions().isHideForeignKeyNames(), is(true));
    assertThat(builder.toOptions().isHideIndexNames(), is(true));
    assertThat(builder.toOptions().isHidePrimaryKeyNames(), is(true));
    assertThat(builder.toOptions().isHideTriggerNames(), is(true));
    assertThat(builder.toOptions().isHideRoutineSpecificNames(), is(true));
    assertThat(builder.toOptions().isShowUnqualifiedNames(), is(true));
    // --

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(new String[] {
      "additional", "--extra" }));
  }

}
