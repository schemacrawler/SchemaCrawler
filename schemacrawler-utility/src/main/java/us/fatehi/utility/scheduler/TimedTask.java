/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.scheduler;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import us.fatehi.utility.string.StringFormat;

/**
 * Wrapper around a task definition that runs the task and times it. Throws an exception if the task
 * does not succeed.
 */
class TimedTask implements Callable<TimedTaskResult> {

  private static final Logger LOGGER = Logger.getLogger(TimedTask.class.getName());

  private final TaskDefinition taskDefinition;
  private final Clock clock;
  private Instant start;

  TimedTask(final TaskDefinition task, final Clock clock) {
    taskDefinition = requireNonNull(task, "Task not provided");
    this.clock = requireNonNull(clock, "Clock not provided");
  }

  @Override
  public TimedTaskResult call() {

    LOGGER.log(
        Level.FINE,
        new StringFormat(
            "Running <%s> on thread <%s>",
            taskDefinition.getTaskName(), Thread.currentThread().getName()));

    start = Instant.now(clock);

    Exception ex = null;
    try {
      taskDefinition.getTask().run();
    } catch (final Exception e) {
      ex = e;
    }

    final Instant stop = Instant.now(clock);
    final Duration runTime = Duration.between(start, stop);
    final TimedTaskResult timedTaskResult =
        new TimedTaskResult(taskDefinition.getTaskName(), runTime, ex);

    if (ex != null) {
      LOGGER.log(
          Level.WARNING,
          String.format(
              "Exception running <%s> on thread <%s>: %s",
              timedTaskResult, Thread.currentThread().getName(), ex.getMessage()),
          ex);
    }
    return timedTaskResult;
  }

  public String getTaskName() {
    return taskDefinition.getTaskName();
  }

  public ZonedDateTime getStart() {
    return start.atZone(ZoneId.systemDefault());
  }
}
