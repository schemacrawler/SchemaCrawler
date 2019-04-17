package schemacrawler.test.commandline.parser;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.commandline.parser.GrepOptionsParser;

public class GrepOptionsParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final GrepOptionsParser optionsParser = new GrepOptionsParser(builder);

    optionsParser.parse(args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.isGrepColumns(), is(false));
    assertThat(schemaCrawlerOptions.isGrepRoutineColumns(), is(false));
    assertThat(schemaCrawlerOptions.isGrepDefinitions(), is(false));
    assertThat(schemaCrawlerOptions.isGrepInvertMatch(), is(false));
    assertThat(schemaCrawlerOptions.isGrepOnlyMatching(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final GrepOptionsParser optionsParser = new GrepOptionsParser(builder);

    optionsParser.parse(args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.isGrepColumns(), is(false));
    assertThat(schemaCrawlerOptions.isGrepRoutineColumns(), is(false));
    assertThat(schemaCrawlerOptions.isGrepDefinitions(), is(false));
    assertThat(schemaCrawlerOptions.isGrepInvertMatch(), is(false));
    assertThat(schemaCrawlerOptions.isGrepOnlyMatching(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(args));
  }

  @Test
  public void grepColumnsBadValue()
  {
    final String[] args = { "--grep-columns", "[[" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final GrepOptionsParser optionsParser = new GrepOptionsParser(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void grepColumnsNoValue()
  {
    final String[] args = { "--grep-columns" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final GrepOptionsParser optionsParser = new GrepOptionsParser(builder);

    assertThrows(CommandLine.ParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--grep-columns",
      "new.*pattern[1-3]",
      "--grep-in-out",
      "new.*pattern[4-6]",
      "--grep-def",
      "new.*pattern[7-9]",
      "--invert-match=true",
      "--only-matching=true",
      "additional",
      "-extra" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final GrepOptionsParser optionsParser = new GrepOptionsParser(builder);

    optionsParser.parse(args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.isGrepColumns(), is(true));
    assertThat(schemaCrawlerOptions.getGrepColumnInclusionRule().get(),
               is(new RegularExpressionInclusionRule(Pattern.compile(
                 "new.*pattern[1-3]"))));

    assertThat(schemaCrawlerOptions.isGrepRoutineColumns(), is(true));
    assertThat(schemaCrawlerOptions.getGrepRoutineColumnInclusionRule().get(),
               is(new RegularExpressionInclusionRule(Pattern.compile(
                 "new.*pattern[4-6]"))));

    assertThat(schemaCrawlerOptions.isGrepDefinitions(), is(true));
    assertThat(schemaCrawlerOptions.getGrepDefinitionInclusionRule().get(),
               is(new RegularExpressionInclusionRule(Pattern.compile(
                 "new.*pattern[7-9]"))));

    assertThat(schemaCrawlerOptions.isGrepInvertMatch(), is(true));
    assertThat(schemaCrawlerOptions.isGrepOnlyMatching(), is(true));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(new String[] {
      "additional", "-extra" }));
  }

}
