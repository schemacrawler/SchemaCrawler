/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.scribe.commandline.schemaspy.ConnectArgsMapper.ConnectArgs;
import schemacrawler.tools.commandline.command.ConnectCommand;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.databaseconnector.DatabaseServerHostConnectionOptions;
import schemacrawler.tools.databaseconnector.DatabaseUrlConnectionOptions;

public class ConnectArgsMapperTest {

  private static ConnectArgsMapper mapper(
      final String databaseType,
      final String database,
      final String host,
      final Integer port,
      final boolean sso,
      final String user,
      final String password,
      final String connectionProperties) {
    return new ConnectArgsMapper(
        new ConnectArgs(
            databaseType, database, host, port, sso, user, password, connectionProperties));
  }

  @Test
  public void knownSchemaSpyTypeMapsToServer() {
    final ConnectCommand command = new ConnectCommand(new ShellState());
    final List<String> args =
        mapper("pgsql", "books", "localhost", 5432, false, "u", "p", null).toArgs(false);
    final CommandLine commandLine = new CommandLine(command);
    commandLine.setUnmatchedArgumentsAllowed(true);
    assertDoesNotThrow(() -> commandLine.parseArgs(args.toArray(String[]::new)));

    final DatabaseServerHostConnectionOptions connectionOptions =
        (DatabaseServerHostConnectionOptions) command.getDatabaseConnectionOptions();
    assertThat(args, hasItem("--server"));
    assertThat(args, hasItem("postgresql"));
    assertThat(args, hasItem("--host"));
    assertThat(args, hasItem("localhost"));
    assertThat(args, hasItem("--database"));
    assertThat(args, hasItem("books"));
    assertThat(connectionOptions.host(), is("localhost"));
    assertThat(connectionOptions.port(), is(5432));
    assertThat(connectionOptions.database(), is("books"));
  }

  @Test
  public void unmappedTypeFallsBackToUrl() {
    final ConnectCommand command = new ConnectCommand(new ShellState());
    final List<String> args =
        mapper("hsqldb", "jdbc:hsqldb:mem:testdb", "localhost", null, false, "u", "p", null)
            .toArgs(false);
    final CommandLine commandLine = new CommandLine(command);
    commandLine.setUnmatchedArgumentsAllowed(true);
    assertDoesNotThrow(() -> commandLine.parseArgs(args.toArray(String[]::new)));

    final DatabaseUrlConnectionOptions connectionOptions =
        (DatabaseUrlConnectionOptions) command.getDatabaseConnectionOptions();
    assertThat(args, hasItem("--url"));
    assertThat(args, not(hasItem("--server")));
    assertThat(connectionOptions.connectionUrl(), is("jdbc:hsqldb:mem:testdb"));
  }

  @Test
  public void connectionPropertiesExpandToRepeatedUrlx() {
    final List<String> args =
        mapper("pgsql", "books", "localhost", null, false, "u", "p", "k1=v1;k2=v2").toArgs(false);
    assertThat(
        args,
        contains(
            "--server",
            "postgresql",
            "--host",
            "localhost",
            "--database",
            "books",
            "--user",
            "u",
            "--password",
            "p",
            "--urlx",
            "k1=v1",
            "--urlx",
            "k2=v2"));
  }

  @Test
  public void unknownTypeThrows() {
    final ConnectArgsMapper connectArgsMapper =
        mapper("not-a-real-db", "db", "h", null, false, "u", "p", null);
    assertThrows(ExecutionRuntimeException.class, () -> connectArgsMapper.toArgs(false));
  }

  @Test
  public void argsParseWithConnectCommand() {
    final List<String> args =
        mapper("sqlite", ":memory:", "localhost", null, false, "u", "p", "ssl=true").toArgs(false);
    final ConnectCommand command = new ConnectCommand(new ShellState());
    final CommandLine commandLine = new CommandLine(command);
    commandLine.setUnmatchedArgumentsAllowed(true);

    assertDoesNotThrow(() -> commandLine.parseArgs(args.toArray(String[]::new)));
    final DatabaseServerHostConnectionOptions connectionOptions =
        (DatabaseServerHostConnectionOptions) command.getDatabaseConnectionOptions();
    assertThat(connectionOptions.urlx().get("ssl"), is("true"));
  }
}
