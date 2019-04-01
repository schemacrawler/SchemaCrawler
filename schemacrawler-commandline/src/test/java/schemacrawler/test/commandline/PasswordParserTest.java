/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.test.commandline;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLineException;
import schemacrawler.tools.commandline.UserCredentialsParser;
import schemacrawler.tools.databaseconnector.UserCredentials;

public class PasswordParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final UserCredentialsParser optionsParser = new UserCredentialsParser();
    optionsParser.parse(args);
    final UserCredentials options = optionsParser.getUserCredentials();

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
    optionsParser.parse(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.getUser(), is(nullValue()));
    assertThat(options.getPassword(), is(nullValue()));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(args));
  }

  @Test
  public void password()
  {
    final String[] args = { "--password", "pwd123" };

    final UserCredentialsParser optionsParser = new UserCredentialsParser();
    optionsParser.parse(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.getUser(), is(nullValue()));
    assertThat(options.getPassword(), is("pwd123"));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void passwordEmptyFile()
    throws Exception
  {
    final Path path = Files.createTempFile("password-file", ".txt");
    final File file = path.toFile();
    file.deleteOnExit();

    final String[] args = { "--password:file", file.getAbsolutePath() };

    final UserCredentialsParser optionsParser = new UserCredentialsParser();
    optionsParser.parse(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.getUser(), is(nullValue()));
    assertThat(options.getPassword(), is(nullValue()));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void passwordFile()
    throws Exception
  {
    final Path path = Files.createTempFile("password-file", ".txt");
    final File file = path.toFile();
    Files.write(path, "pwd123".getBytes(StandardCharsets.UTF_8));
    file.deleteOnExit();

    final String[] args = { "--password:file", file.getAbsolutePath() };

    final UserCredentialsParser optionsParser = new UserCredentialsParser();
    optionsParser.parse(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.getUser(), is(nullValue()));
    assertThat(options.getPassword(), is("pwd123"));

    final String[] remainder = optionsParser.getRemainder();
    assertThat(remainder, is(emptyArray()));
  }

  @Test
  public void passwordNoFile()
    throws Exception
  {
    final String[] args = { "--password:file", "./no-file.txt" };

    final UserCredentialsParser optionsParser = new UserCredentialsParser();
    optionsParser.parse(args);
    assertThrows(SchemaCrawlerCommandLineException.class,
                 () -> optionsParser.getUserCredentials());
  }

  @Test
  public void passwordFilePlusPassword()
    throws Exception
  {
    final Path path = Files.createTempFile("password-file", ".txt");
    final File file = path.toFile();
    Files.write(path, "pwd123".getBytes(StandardCharsets.UTF_8));
    file.deleteOnExit();

    final String[] args = {
      "--password:file", file.getAbsolutePath(), "--password", "pwd123" };

    final UserCredentialsParser optionsParser = new UserCredentialsParser();
    optionsParser.parse(args);
    assertThrows(SchemaCrawlerCommandLineException.class,
                 () -> optionsParser.getUserCredentials());
  }

}
