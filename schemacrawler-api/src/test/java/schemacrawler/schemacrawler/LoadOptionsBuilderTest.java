/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schemacrawler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.scheduler.TaskRunner;

public class LoadOptionsBuilderTest {

  @Test
  public void maxThreads() {
    final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder.builder();

    // Default
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MAX_THREADS));

    loadOptionsBuilder.withMaxThreads(Integer.MIN_VALUE);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MIN_THREADS));

    loadOptionsBuilder.withMaxThreads(-2);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MIN_THREADS));

    loadOptionsBuilder.withMaxThreads(0);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MIN_THREADS));

    loadOptionsBuilder.withMaxThreads(1);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MIN_THREADS));

    loadOptionsBuilder.withMaxThreads(2);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(2));

    loadOptionsBuilder.withMaxThreads(11);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MAX_THREADS));

    loadOptionsBuilder.withMaxThreads(Integer.MAX_VALUE);
    assertThat(loadOptionsBuilder.toOptions().getMaxThreads(), is(TaskRunner.MAX_THREADS));
  }
}
