/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import schemacrawler.tools.commandline.command.UserCredentialsOptions;
import us.fatehi.utility.datasource.UserCredentials;

public class UserParserTest {

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.hasUser(), is(false));
    assertThat(options.hasPassword(), is(false));
    assertThat(options.getUser(), is(nullValue()));
    assertThat(options.getPassword(), is(nullValue()));

    // Clear password should have no effect
    options.clearPassword();

    assertThat(options.hasUser(), is(false));
    assertThat(options.getUser(), is(nullValue()));
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.hasUser(), is(false));
    assertThat(options.hasPassword(), is(false));
    assertThat(options.hasUser(), is(false));
    assertThat(options.hasPassword(), is(false));
  }

  @Test
  public void user() {
    final String[] args = {"--user", "usr123"};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.hasPassword(), is(false));
    assertThat(options.hasUser(), is(true));
    assertThat(options.getPassword(), is(nullValue()));
    assertThat(options.getUser(), is("usr123"));
  }

  @Test
  public void userEmptyEnv() {
    final String[] args = {"--user:env", "NO_ENV"};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.hasPassword(), is(false));
    assertThat(options.hasUser(), is(false));
    assertThat(options.getPassword(), is(nullValue()));
    assertThat(options.getUser(), is(nullValue()));
  }

  @Test
  public void userEmptyFile() throws Exception {
    final Path path = Files.createTempFile("user-file", ".txt");
    final File file = path.toFile();
    file.deleteOnExit();

    final String[] args = {"--user:file", file.getAbsolutePath()};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.hasPassword(), is(false));
    assertThat(options.hasUser(), is(false));
    assertThat(options.getPassword(), is(nullValue()));
    assertThat(options.getUser(), is(nullValue()));
  }

  @Test
  public void userFile() throws Exception {
    final Path path = Files.createTempFile("user-file", ".txt");
    final File file = path.toFile();
    Files.write(path, "usr123".getBytes(StandardCharsets.UTF_8));
    file.deleteOnExit();

    final String[] args = {"--user:file", file.getAbsolutePath()};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.hasPassword(), is(false));
    assertThat(options.hasUser(), is(true));
    assertThat(options.getPassword(), is(nullValue()));
    assertThat(options.getUser(), is("usr123"));

    // Clear password should have no effect
    options.clearPassword();

    assertThat(options.hasUser(), is(true));
    assertThat(options.getUser(), is("usr123"));
  }

  @Test
  public void userFilePlusUser() throws Exception {
    final Path path = Files.createTempFile("user-file", ".txt");
    final File file = path.toFile();
    Files.write(path, "usr123".getBytes(StandardCharsets.UTF_8));
    file.deleteOnExit();

    final String[] args = {"--user:file", file.getAbsolutePath(), "--user", "usr123"};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();

    assertThrows(
        CommandLine.MutuallyExclusiveArgsException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void userNoFile() throws Exception {
    final String[] args = {"--user:file", "./no-file.txt"};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    assertThrows(CommandLine.ParameterException.class, () -> optionsParser.getUserCredentials());
  }
}
