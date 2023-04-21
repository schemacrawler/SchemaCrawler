/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package us.fatehi.utility.scheduler;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

final class TimedTaskResult implements Serializable {

  private static final long serialVersionUID = -6572177882937039431L;

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
    return String.format("%s - <%s>", durationLocal.format(df), taskName);
  }
}
