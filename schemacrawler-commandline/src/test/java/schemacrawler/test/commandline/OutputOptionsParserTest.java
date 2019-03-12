package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.OutputOptionsParser;
import schemacrawler.tools.options.OutputOptions;

public class OutputOptionsParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(new Config());
    final OutputOptions options = outputOptionsParser.parse(args);

    assertThat(options.getOutputFile().isPresent(), is(false));
    assertThat(options.getOutputFormatValue(), is("text"));

    final String[] remainder = outputOptionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(new Config());
    final OutputOptions options = outputOptionsParser.parse(args);

    assertThat(options.getOutputFile().isPresent(), is(false));
    assertThat(options.getOutputFormatValue(), is("text"));

    final String[] remainder = outputOptionsParser.getRemainder();
    assertThat(remainder, is(args));
  }

  @Test
  public void outputfileNoValue()
  {
    final String[] args = { "--output-file" };

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(new Config());
    assertThrows(CommandLine.MissingParameterException.class,
                 () -> outputOptionsParser.parse(args));
  }

  @Test
  public void outputformatNoValue()
  {
    final String[] args = { "--output-format" };

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(new Config());
    assertThrows(CommandLine.MissingParameterException.class,
                 () -> outputOptionsParser.parse(args));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "-outputfile",
      "file.txt",
      "-outputformat",
      "tables.js",
      "additional",
      "-extra" };

    final OutputOptionsParser outputOptionsParser = new OutputOptionsParser(new Config());
    final OutputOptions options = outputOptionsParser.parse(args);

    assertThat(options.getOutputFile()
                 .orElseThrow(() -> new IllegalArgumentException("No file found"))
                 .getFileName(), is(Paths.get("file.txt")));
    assertThat(options.getOutputFormatValue(), is("tables.js"));

    final String[] remainder = outputOptionsParser.getRemainder();
    assertThat(remainder, is(new String[] {
      "additional", "-extra" }));
  }

}
