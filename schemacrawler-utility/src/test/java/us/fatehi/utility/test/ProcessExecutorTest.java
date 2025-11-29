/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import us.fatehi.utility.ProcessExecutor;

public class ProcessExecutorTest {

  @Test
  public void testCallWithCommand(@TempDir final Path tempDir) throws Exception {

    final String command = "qx-not-a-c0mmand";

    final ProcessExecutor processExecutor = new ProcessExecutor();
    processExecutor.setCommandLine(Arrays.asList(command));

    processExecutor.call();

    assertThat(processExecutor.getCommand(), contains(command));
    assertThat(processExecutor.getExitCode(), is(Integer.MIN_VALUE));
    assertThat(processExecutor.getProcessError().toString().endsWith("stderr"), is(true));
    assertThat(processExecutor.getProcessOutput().toString().endsWith("stdout"), is(true));
  }

  @Test
  public void testCallWithEmptyCommand(@TempDir final Path tempDir) throws Exception {

    final ProcessExecutor processExecutor = new ProcessExecutor();
    processExecutor.setCommandLine(Collections.emptyList());

    processExecutor.call();

    assertThat(processExecutor.getCommand(), is(empty()));
    assertThat(processExecutor.getExitCode(), is(0));
    assertThat(processExecutor.getProcessError(), is(nullValue()));
    assertThat(processExecutor.getProcessOutput(), is(nullValue()));
  }

  @Test
  public void testCallWithNullCommand() {
    final ProcessExecutor processExecutor = new ProcessExecutor();

    processExecutor.call();

    assertThat(processExecutor.getCommand(), is(nullValue()));
    assertThat(processExecutor.getExitCode(), is(Integer.MIN_VALUE));
    assertThat(processExecutor.getProcessError(), is(nullValue()));
    assertThat(processExecutor.getProcessOutput(), is(nullValue()));
  }
}
