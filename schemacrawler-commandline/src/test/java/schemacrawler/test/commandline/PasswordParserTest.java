package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.commandline.UserCredentialsParser;
import schemacrawler.tools.databaseconnector.UserCredentials;

public class PasswordParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final UserCredentialsParser optionsParser = new UserCredentialsParser();
    final UserCredentials options = optionsParser.parse(args);

    assertThat(options.getUser(), is(nullValue()));
    assertThat(options.getPassword(), is(nullValue()));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final UserCredentialsParser optionsParser = new UserCredentialsParser();
    final UserCredentials options = optionsParser.parse(args);

    assertThat(options.getUser(), is(nullValue()));
    assertThat(options.getPassword(), is(nullValue()));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(args));
  }

}
