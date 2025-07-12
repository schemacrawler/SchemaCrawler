/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.scheduler;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractTaskRunner implements TaskRunner {

  private static final Logger LOGGER = Logger.getLogger(AbstractTaskRunner.class.getName());

  private final String id;
  protected final Clock clock;
  private final Queue<TaskDefinition> taskDefinitions;
  private final Queue<TimedTaskResult> taskResults;

  AbstractTaskRunner(final String id) {
    this(id, Clock.systemUTC());
  }

  AbstractTaskRunner(final String id, final Clock clock) {
    this.id = requireNotBlank(id, "No id provided");
    this.clock = requireNonNull(clock, "Clock not provided");

    taskDefinitions = new LinkedBlockingDeque<>();
    taskResults = new LinkedBlockingDeque<>();
  }

  @Override
  public final void add(final TaskDefinition taskDefinition) throws Exception {

    if (taskDefinition == null) {
      return;
    }
    if (isStopped()) {
      throw new IllegalStateException("Task runner is stopped");
    }

    taskDefinitions.add(taskDefinition);
  }

  @Override
  public final String getId() {
    return id;
  }

  @Override
  public abstract boolean isStopped();

  /**
   * Allows for a deferred conversion to a string. Useful in logging.
   *
   * @return String supplier.
   */
  @Override
  public final Supplier<String> report() {

    return () -> {
      final BiFunction<Duration, Duration, Double> calculatePercentage =
          (final Duration duration, final Duration totalDuration) -> {
            final long totalMillis = totalDuration.toMillis();
            if (totalMillis == 0) {
              return 0d;
            }
            return duration.toMillis() * 100D / totalMillis;
          };

      final DateTimeFormatter df =
          new DateTimeFormatterBuilder()
              .appendValue(HOUR_OF_DAY, 2)
              .appendLiteral(':')
              .appendValue(MINUTE_OF_HOUR, 2)
              .appendLiteral(':')
              .appendValue(SECOND_OF_MINUTE, 2)
              .appendFraction(NANO_OF_SECOND, 3, 3, true)
              .toFormatter();

      Duration totalDuration = Duration.ofNanos(0);
      for (final TimedTaskResult task : taskResults) {
        totalDuration = totalDuration.plus(task.getDuration());
      }

      final StringBuilder buffer = new StringBuilder(1024);

      final LocalTime totalDurationLocal = LocalTime.ofNanoOfDay(totalDuration.toNanos());
      buffer.append(
          String.format(
              "Total time taken for <%s> - %s hours%n", id, totalDurationLocal.format(df)));

      for (final TimedTaskResult task : taskResults) {
        buffer.append(
            String.format(
                "-%5.1f%% - %s%n",
                calculatePercentage.apply(task.getDuration(), totalDuration), task));
      }

      taskResults.clear();

      return buffer.toString();
    };
  }

  @Override
  public abstract void stop();

  @Override
  public final void submit() throws Exception {

    // Cannot submit tasks to a stopped runner
    if (isStopped()) {
      throw new IllegalStateException("Task runner is stopped");
    }

    // Run tasks
    final Collection<TimedTaskResult> runTaskResults;
    requireNonNull(taskDefinitions, "Tasks not provided");
    if (taskDefinitions.isEmpty()) {
      runTaskResults = Collections.emptyList();
    } else {
      runTaskResults = runTimed(taskDefinitions);
    }

    taskResults.addAll(runTaskResults);
    taskDefinitions.clear();

    // Stop, report and throw on an exception
    boolean hasException = false;
    Exception exception = null;
    for (final TimedTaskResult runTaskResult : runTaskResults) {
      if (runTaskResult.hasException()) {
        hasException = true;
        final Exception runTaskException = runTaskResult.getException();
        if (exception == null) {
          exception = runTaskException;
        } else {
          exception.addSuppressed(runTaskException);
        }
      }
    }
    if (hasException) {
      stop();
      LOGGER.log(Level.CONFIG, report());
      throw exception;
    }
  }

  abstract Collection<TimedTaskResult> runTimed(final Collection<TaskDefinition> taskDefinitions)
      throws Exception;
}
