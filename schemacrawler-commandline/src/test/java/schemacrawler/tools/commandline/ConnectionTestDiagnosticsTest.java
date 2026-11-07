/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.commandline.state.ShellState;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class ConnectionTestDiagnosticsTest {

  @Test
  public void explicitUrlIsPrintedOnlyAfterSuccessfulConnection() throws Exception {
    final ShellState state = mock(ShellState.class);
    final DatabaseConnectionSource source = mock(DatabaseConnectionSource.class);
    final Connection connection = mock(Connection.class);
    final DatabaseMetaData metadata = mock(DatabaseMetaData.class);
    final String url = "jdbc:postgresql://host/books?password=URL_SECRET";
    when(state.isConnected()).thenReturn(true);
    when(state.getConnectionSource()).thenReturn(source);
    when(source.get()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(metadata);
    when(metadata.getURL()).thenReturn(url);
    final PrintStream originalOut = System.out;
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try (final PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
      System.setOut(out);
      ConnectionTest.printConnectionUrl(state, false);
      assertThat(buffer.size(), is(0));
      ConnectionTest.printConnectionUrl(state, true);
      final String output = buffer.toString(StandardCharsets.UTF_8);
      assertThat(output.contains("JDBC URL: " + url), is(true));
      assertThat(output.contains("may contain credentials"), is(true));
    } finally {
      System.setOut(originalOut);
    }
  }
}
