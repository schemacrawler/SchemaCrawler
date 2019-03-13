package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.tools.commandline.ApplicationOptions;
import schemacrawler.tools.commandline.ApplicationOptionsParser;

public class ApplicationOptionsParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    final ApplicationOptions options = optionsParser.parse(args);

    assertThat(options.getApplicationLogLevel(), is(Level.OFF));
    assertThat(options.isShowHelp(), is(false));
    assertThat(options.isShowVersionOnly(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    final ApplicationOptions options = optionsParser.parse(args);

    assertThat(options.getApplicationLogLevel(), is(Level.OFF));
    assertThat(options.isShowHelp(), is(false));
    assertThat(options.isShowVersionOnly(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(args));
  }

  @Test
  public void help()
  {
    final String[] args = { "--help" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    final ApplicationOptions options = optionsParser.parse(args);

    assertThat(options.getApplicationLogLevel(), is(Level.OFF));
    assertThat(options.isShowHelp(), is(true));
    assertThat(options.isShowVersionOnly(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void moreHelp()
  {
    final String[] args = { "--help", "-h" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    assertThrows(CommandLine.OverwrittenOptionException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void loglevelNoValue()
  {
    final String[] args = { "--log-level" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    assertThrows(CommandLine.MissingParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void loglevelBadValue()
  {
    final String[] args = { "--log-level", "BAD" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    assertThrows(CommandLine.ParameterException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void loglevel()
  {
    final String[] args = { "--log-level", "FINE" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    final ApplicationOptions options = optionsParser.parse(args);

    assertThat(options.getApplicationLogLevel(), is(Level.FINE));
    assertThat(options.isShowHelp(), is(false));
    assertThat(options.isShowVersionOnly(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void loglevelMixedCase()
  {
    final String[] args = { "--log-level", "FinE" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    final ApplicationOptions options = optionsParser.parse(args);

    assertThat(options.getApplicationLogLevel(), is(Level.FINE));
    assertThat(options.isShowHelp(), is(false));
    assertThat(options.isShowVersionOnly(), is(false));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--log-level", "ALL", "-h", "--version", "additional", "-extra" };

    final ApplicationOptionsParser optionsParser = new ApplicationOptionsParser();
    final ApplicationOptions options = optionsParser.parse(args);

    assertThat(options.getApplicationLogLevel(), is(Level.ALL));
    assertThat(options.isShowHelp(), is(true));
    assertThat(options.isShowVersionOnly(), is(true));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(new String[] {
      "additional", "-extra" }));
  }

}
