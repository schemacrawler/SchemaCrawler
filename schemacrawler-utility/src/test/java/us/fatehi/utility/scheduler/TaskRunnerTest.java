/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.scheduler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

public class TaskRunnerTest {

  @Test
  public void addBadTask() throws Exception {

    try (final TestTaskRunner taskRunner =
        new TestTaskRunner("test_id", Clock.fixed(Instant.now(), ZoneId.of("UTC"))); ) {

      assertThat(taskRunner.isStopped(), is(false));

      taskRunner.add(
          new TaskDefinition(
              "task 1",
              () -> {
                throw new RuntimeException("Failed task");
              }));

      final RuntimeException runtimeException =
          assertThrows(RuntimeException.class, () -> taskRunner.submit());
      assertThat(runtimeException.getMessage(), is("Failed task"));

      assertThat(taskRunner.isStopped(), is(true));

      final IllegalStateException stoppedException =
          assertThrows(IllegalStateException.class, () -> taskRunner.submit());
      assertThat(stoppedException.getMessage(), is("Task runner is stopped"));
    }
  }

  @Test
  public void addTask() throws Exception {

    try (final TestTaskRunner taskRunner =
        new TestTaskRunner("test_id", Clock.fixed(Instant.now(), ZoneId.of("UTC"))); ) {

      taskRunner.submit();
      assertThat(taskRunner.size(), is(0));

      taskRunner.add(new TaskDefinition("task 1"));
      taskRunner.add(null);

      taskRunner.submit();
      assertThat(taskRunner.size(), is(1));

      taskRunner.stop();
      assertThrows(IllegalStateException.class, () -> taskRunner.add(new TaskDefinition("task 2")));
    }
  }

  @Test
  public void report() throws Exception {

    try (final TaskRunner taskRunner =
        new TestTaskRunner("test_id", Clock.fixed(Instant.now(), ZoneId.of("UTC"))); ) {

      assertThat(taskRunner.getId(), is("test_id"));

      taskRunner.add(new TaskDefinition("task 1"));
      taskRunner.submit();
      final Supplier<String> report = taskRunner.report();

      assertThat(
          report.get().replaceAll("\\R", ""),
          is(
              "Total time taken for <test_id> - 00:00:00.000 hours"
                  + "-  0.0% - 00:00:00.000 - <task_1>"));
    }
  }
}
