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

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

final class TimedTaskResult implements Serializable {

  @Serial private static final long serialVersionUID = -6572177882937039431L;

  private static final DateTimeFormatter df =
      new DateTimeFormatterBuilder()
          .appendValue(HOUR_OF_DAY, 2)
          .appendLiteral(':')
          .appendValue(MINUTE_OF_HOUR, 2)
          .appendLiteral(':')
          .appendValue(SECOND_OF_MINUTE, 2)
          .appendFraction(NANO_OF_SECOND, 3, 3, true)
          .toFormatter();

  private final Duration duration;
  private final String taskName;
  private final Exception exception;

  TimedTaskResult(final String taskName, final Duration duration, final Exception exception) {
    requireNonNull(taskName, "Task name not provided");
    requireNonNull(duration, "Duration not provided");
    this.taskName = taskName;
    this.duration = duration;
    this.exception = exception;
  }

  public Duration getDuration() {
    return duration;
  }

  public Exception getException() {
    return exception;
  }

  public boolean hasException() {
    return exception != null;
  }

  @Override
  public String toString() {
    final LocalTime durationLocal = LocalTime.ofNanoOfDay(duration.toNanos());
    return "%s - <%s>".formatted(durationLocal.format(df), taskName);
  }
}
