package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.commandline.SortOptionsParser;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class SortOptionsParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder.builder();
    final SortOptionsParser optionsParser = new SortOptionsParser(builder);
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
    final SortOptionsParser optionsParser = new SortOptionsParser(builder);
    optionsParser.parse(args);

    assertThat("No options are set",
               builder.toOptions(),
               is(equalTo(SchemaTextOptionsBuilder.builder().toOptions())));

    final String[] remainder = optionsParser.getRemainder();
    assertThat("Remainder is not empty", remainder.length, is(1));
  }

  @Test
  public void sortTablesFalse()
  {
    final String[] args = {
      "--sort-tables=false" };

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder.builder();
    final SortOptionsParser optionsParser = new SortOptionsParser(builder);
    optionsParser.parse(args);

    assertThat("No options are set",
               builder.toOptions(),
               is(equalTo(SchemaTextOptionsBuilder.builder().toOptions())));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void sortTablesTrue()
  {
    final String[] args = {
      "--sort-tables=true" };

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder.builder();
    final SortOptionsParser optionsParser = new SortOptionsParser(builder);
    optionsParser.parse(args);

    assertThat(builder.toOptions().isAlphabeticalSortForTables(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(),
               is(false));
    assertThat(builder.toOptions().isAlphabeticalSortForRoutines(), is(false));
    assertThat(builder.toOptions().isAlphabeticalSortForRoutineColumns(),
               is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--sort-tables",
      "--sort-columns",
      "--sort-routines",
      "--sort-in-out",
      "additional",
      "--extra" };

    final SchemaTextOptionsBuilder builder = SchemaTextOptionsBuilder.builder();
    final SortOptionsParser optionsParser = new SortOptionsParser(builder);
    optionsParser.parse(args);

    assertThat(builder.toOptions().isAlphabeticalSortForTables(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(),
               is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForRoutines(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForRoutineColumns(),
               is(true));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(new String[] {
      "additional", "--extra" }));
  }

}
