package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.commandline.FilterOptionsParser;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLineException;

public class FilterOptionsParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final FilterOptionsParser optionsParser = new FilterOptionsParser(builder);

    optionsParser.parse(args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.getParentTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.getChildTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.isNoEmptyTables(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final FilterOptionsParser optionsParser = new FilterOptionsParser(builder);

    optionsParser.parse(args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.getParentTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.getChildTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.isNoEmptyTables(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(args));
  }

  @Test
  public void parentsBadValue()
  {
    final String[] args = { "--parents", "-1" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final FilterOptionsParser optionsParser = new FilterOptionsParser(builder);
    assertThrows(SchemaCrawlerCommandLineException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void childrenBadValue()
  {
    final String[] args = { "--children", "-1" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final FilterOptionsParser optionsParser = new FilterOptionsParser(builder);
    assertThrows(SchemaCrawlerCommandLineException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void parentsNoValue()
  {
    final String[] args = { "--parents" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final FilterOptionsParser optionsParser = new FilterOptionsParser(builder);

    assertThrows(CommandLine.ParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void childrenNoValue()
  {
    final String[] args = { "--children" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final FilterOptionsParser optionsParser = new FilterOptionsParser(builder);

    assertThrows(CommandLine.ParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--parents",
      "2",
      "--children",
      "2",
      "--no-empty-tables=true",
      "additional",
      "-extra" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final FilterOptionsParser optionsParser = new FilterOptionsParser(builder);

    optionsParser.parse(args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.getParentTableFilterDepth(), is(2));
    assertThat(schemaCrawlerOptions.getChildTableFilterDepth(), is(2));
    assertThat(schemaCrawlerOptions.isNoEmptyTables(), is(true));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(new String[] {
      "additional", "-extra" }));
  }

}
