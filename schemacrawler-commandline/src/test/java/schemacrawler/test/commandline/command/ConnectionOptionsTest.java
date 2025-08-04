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
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.tools.commandline.command.ConnectCommand;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.databaseconnector.DatabaseConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseServerHostConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseUrlConnectionOptions;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseServerType;

public class ConnectionOptionsTest {

  @Test
  public void allArgs() {
    final String[] args = {
      "--url",
      "jdbc:test-db://somehost:1234/adatabase",
      "--server",
      "test-db",
      "--host",
      "somehost",
      "--port",
      "1234",
      "--database",
      "adatabase",
      "additional",
      "--extra"
    };

    final ConnectCommand optionsParser = new ConnectCommand(new ShellState());

    assertThrows(
        CommandLine.MutuallyExclusiveArgsException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void badlyFormed_urlx() throws NoSuchFieldException, IllegalAccessException {
    final String[] args = {"--server", "test-db", "--urlx", "key1", "additional", "--extra"};

    final Config config = new Config();
    config.put("url", "jdbc:test-db://some-url");

    final ConnectCommand optionsParser = new ConnectCommand(new ShellState());

    assertThrows(
        CommandLine.ParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void blankConnectCommand() {
    final String[] args = {"--url", " "};

    final ConnectCommand optionsParser = new ConnectCommand(new ShellState());
    new CommandLine(optionsParser).parseArgs(args);

    final DatabaseConnectionOptions databaseConnectorOptions =
        optionsParser.getDatabaseConnectionOptions();
    final DatabaseServerType databaseServerType = databaseConnectorOptions.getDatabaseServerType();

    assertThat(
        ((DatabaseUrlConnectionOptions) databaseConnectorOptions).getConnectionUrl(), is(" "));
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is(nullValue()));
  }

  @Test
  public void hostPort() {
    final String[] args = {
      "--server",
      "test-db",
      "--host",
      "somehost",
      "--port",
      "1234",
      "--database",
      "adatabase",
      "additional",
      "--extra"
    };

    final Config config = new Config();
    config.put("url", "jdbc:test-db://${host}:${port}/${database}");

    final ConnectCommand optionsParser = new ConnectCommand(new ShellState());
    newCommandLine(optionsParser, null).parseArgs(args);

    final DatabaseConnectionOptions databaseConnectorOptions =
        optionsParser.getDatabaseConnectionOptions();
    final DatabaseServerType databaseServerType = databaseConnectorOptions.getDatabaseServerType();

    assertThat(
        ((DatabaseServerHostConnectionOptions) databaseConnectorOptions).getHost(), is("somehost"));
    assertThat(
        ((DatabaseServerHostConnectionOptions) databaseConnectorOptions).getPort(), is(1234));
    assertThat(
        ((DatabaseServerHostConnectionOptions) databaseConnectorOptions).getDatabase(),
        is("adatabase"));
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("test-db"));
  }

  @Test
  public void no_urlx() throws NoSuchFieldException, IllegalAccessException {
    final String[] args = {"--server", "test-db", "--urlx"};

    final Config config = new Config();
    config.put("url", "jdbc:test-db://some-url");

    final ConnectCommand optionsParser = new ConnectCommand(new ShellState());

    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }

  @Test
  public void noArgs() {
    final String[] args = {};

    final ConnectCommand optionsParser = new ConnectCommand(new ShellState());
    new CommandLine(optionsParser).parseArgs(args);
    assertThrows(
        CommandLine.ParameterException.class, () -> optionsParser.getDatabaseConnectionOptions());
  }

  @Test
  public void noUrlValueConnectCommand() {
    final String[] args = {"--url"};

    final ConnectCommand optionsParser = new ConnectCommand(new ShellState());
    assertThrows(
        CommandLine.ParameterException.class, () -> new CommandLine(optionsParser).parseArgs(args));
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final ConnectCommand optionsParser = new ConnectCommand(new ShellState());
    final CommandLine commandLine = newCommandLine(optionsParser, null);
    commandLine.parseArgs(args);
    assertThrows(
        CommandLine.ParameterException.class, () -> optionsParser.getDatabaseConnectionOptions());
  }

  @Test
  public void url() {
    final String[] args = {"--url", "jdbc:database_url", "additional", "--extra"};

    final ConnectCommand optionsParser = new ConnectCommand(new ShellState());
    newCommandLine(optionsParser, null).parseArgs(args);

    final DatabaseConnectionOptions databaseConnectorOptions =
        optionsParser.getDatabaseConnectionOptions();
    final DatabaseServerType databaseServerType = databaseConnectorOptions.getDatabaseServerType();

    assertThat(
        ((DatabaseUrlConnectionOptions) databaseConnectorOptions).getConnectionUrl(),
        is("jdbc:database_url"));
    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is(nullValue()));
  }

  @Test
  public void urlx() throws NoSuchFieldException, IllegalAccessException {
    final String[] args = {
      "--server", "test-db", "--urlx", "key1=value1;key2=value2", "additional", "--extra"
    };

    final Config config = new Config();
    config.put("url", "jdbc:test-db://some-url");

    final ConnectCommand optionsParser = new ConnectCommand(new ShellState());
    newCommandLine(optionsParser, null).parseArgs(args);

    final DatabaseConnectionOptions databaseConnectorOptions =
        optionsParser.getDatabaseConnectionOptions();
    final DatabaseServerType databaseServerType = databaseConnectorOptions.getDatabaseServerType();

    assertThat(databaseServerType.getDatabaseSystemIdentifier(), is("test-db"));

    // TODO: test urlx parameters
  }

  @Test
  public void urlxWithUrl() {
    final String[] args = {
      "--url", "jdbc:database_url", "--urlx", "key1=value1;key2=value2", "additional", "--extra"
    };

    final ConnectCommand optionsParser = new ConnectCommand(new ShellState());

    assertThrows(
        CommandLine.MissingParameterException.class,
        () -> {
          final CommandLine commandLine = newCommandLine(optionsParser, null);
          commandLine.parseArgs(args);
        });
  }
}
