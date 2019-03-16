package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.commandline.DatabaseServerTypeParser;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLineException;
import schemacrawler.tools.databaseconnector.DatabaseConnector;

public class DatabaseServerTypeParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final DatabaseServerTypeParser optionsParser = new DatabaseServerTypeParser();

    assertThrows(SchemaCrawlerCommandLineException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final DatabaseServerTypeParser optionsParser = new DatabaseServerTypeParser();

    assertThrows(SchemaCrawlerCommandLineException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void allArgsWithValue()
  {
    final String[] args = { "--server", "hsqldb", "--url", "jdbc:hsqldb:url" };

    final DatabaseServerTypeParser optionsParser = new DatabaseServerTypeParser();
    assertThrows(SchemaCrawlerCommandLineException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void allArgsNoValue()
  {
    final String[] args = { "--server", "--url" };

    final DatabaseServerTypeParser optionsParser = new DatabaseServerTypeParser();
    assertThrows(SchemaCrawlerCommandLineException.class,
                 () -> optionsParser.parse(args));
  }

  @Test
  public void withUrl()
    throws SchemaCrawlerException
  {
    final String[] args = {
      "-url", "jdbc:hsqldb:url", "additional", "-extra" };

    final DatabaseServerTypeParser optionsParser = new DatabaseServerTypeParser();
    final DatabaseConnector options = optionsParser.parse(args);

    assertThat(optionsParser.isBundled(), is(false));
    assertThat(options.toString(),
               is("Database connector for unknown database system type"));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(new String[] {
      "additional", "-extra" }));
  }

  @Test
  public void withServer()
    throws SchemaCrawlerException
  {
    final String[] args = {
      "-server", "hsqldb", "additional", "-extra" };

    final DatabaseServerTypeParser optionsParser = new DatabaseServerTypeParser();

    assertThrows(SchemaCrawlerCommandLineException.class,
                 () -> optionsParser.parse(args));
  }

}
