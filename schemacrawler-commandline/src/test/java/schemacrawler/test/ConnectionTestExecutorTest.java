/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static us.fatehi.test.utility.extensions.FileHasContent.contentsOf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.commandline.ConnectionTest;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.extensions.CaptureSystemStreams;
import us.fatehi.test.utility.extensions.CapturedSystemStreams;

@WithTestDatabase
@CaptureSystemStreams
@DisableLogging
@DisplayName("Connection-test command-line executor")
public class ConnectionTestExecutorTest {

  @Test
  @DisplayName("Returns success and reports the connection URL")
  public void connectionTest(
      final DatabaseConnectionInfo connectionInfo, final CapturedSystemStreams streams) {
    final int exitCode =
        new ConnectionTest()
            .execute("--url", connectionInfo.connectionUrl(), "--user", "sa", "--password", "");

    assertThat(exitCode, is(0));
    assertThat(contentsOf(streams.out()), containsString(connectionInfo.connectionUrl()));
  }
}
