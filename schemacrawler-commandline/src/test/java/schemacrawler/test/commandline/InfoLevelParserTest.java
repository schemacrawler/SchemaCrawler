package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.commandline.InfoLevelParser;

public class InfoLevelParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final InfoLevelParser optionsParser = new InfoLevelParser(builder);

    assertThrows(CommandLine.ParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final InfoLevelParser optionsParser = new InfoLevelParser(builder);

    assertThrows(CommandLine.ParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void infoLevelBadValue()
  {
    final String[] args = { "--info-level", "someinfolvl" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final InfoLevelParser optionsParser = new InfoLevelParser(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void infoLevelNoValue()
  {
    final String[] args = { "--info-level" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final InfoLevelParser optionsParser = new InfoLevelParser(builder);

    assertThrows(CommandLine.ParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void infoLevelWithValue()
  {
    final String[] args = {
      "--info-level", "detailed", "additional", "-extra" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final InfoLevelParser optionsParser = new InfoLevelParser(builder);

    optionsParser.parse(args);

    final SchemaInfoLevel schemaInfoLevel = builder.toOptions()
      .getSchemaInfoLevel();
    final SchemaInfoLevelBuilder expectedSchemaInfoLevelBuilder = SchemaInfoLevelBuilder
      .builder().withInfoLevel(InfoLevel.detailed).withoutRoutines()
      .withTag("detailed");
    assertThat(schemaInfoLevel, is(expectedSchemaInfoLevelBuilder.toOptions()));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(new String[] {
      "additional", "-extra" }));
  }

}
