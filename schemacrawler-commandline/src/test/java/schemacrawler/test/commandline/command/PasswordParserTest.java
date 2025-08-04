/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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

public class PasswordParserTest {

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

    assertThat(options.hasPassword(), is(false));
    assertThat(options.getPassword(), is(nullValue()));
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
  public void password() {
    final String[] args = {"--password", "pwd123"};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.hasUser(), is(false));
    assertThat(options.hasPassword(), is(true));
    assertThat(options.getUser(), is(nullValue()));
    assertThat(options.getPassword(), is("pwd123"));
  }

  @Test
  public void passwordEmptyEnv() {
    final String[] args = {"--password:env", "NO_ENV"};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.hasUser(), is(false));
    assertThat(options.hasPassword(), is(false));
    assertThat(options.getUser(), is(nullValue()));
    assertThat(options.getPassword(), is(nullValue()));
  }

  @Test
  public void passwordEmptyFile() throws Exception {
    final Path path = Files.createTempFile("password-file", ".txt");
    final File file = path.toFile();
    file.deleteOnExit();

    final String[] args = {"--password:file", file.getAbsolutePath()};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.hasUser(), is(false));
    assertThat(options.hasPassword(), is(false));
    assertThat(options.getUser(), is(nullValue()));
    assertThat(options.getPassword(), is(nullValue()));
  }

  @Test
  public void passwordFile() throws Exception {
    final Path path = Files.createTempFile("password-file", ".txt");
    final File file = path.toFile();
    Files.write(path, "pwd123".getBytes(StandardCharsets.UTF_8));
    file.deleteOnExit();

    final String[] args = {"--password:file", file.getAbsolutePath()};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    final UserCredentials options = optionsParser.getUserCredentials();

    assertThat(options.hasUser(), is(false));
    assertThat(options.hasPassword(), is(true));
    assertThat(options.getUser(), is(nullValue()));
    assertThat(options.getPassword(), is("pwd123"));

    // Clear password should have no effect
    options.clearPassword();

    assertThat(options.hasPassword(), is(true));
    assertThat(options.getPassword(), is("pwd123"));
  }

  @Test
  public void passwordFilePlusPassword() throws Exception {
    final Path path = Files.createTempFile("password-file", ".txt");
    final File file = path.toFile();
    Files.write(path, "pwd123".getBytes(StandardCharsets.UTF_8));
    file.deleteOnExit();

    final String[] args = {"--password:file", file.getAbsolutePath(), "--password", "pwd123"};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();

    assertThrows(
        CommandLine.MutuallyExclusiveArgsException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void passwordNoFile() throws Exception {
    final String[] args = {"--password:file", "./no-file.txt"};

    final UserCredentialsOptions optionsParser = new UserCredentialsOptions();
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    assertThrows(CommandLine.ParameterException.class, () -> optionsParser.getUserCredentials());
  }
}
