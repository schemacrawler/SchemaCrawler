/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
package us.fatehi.utility;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public final class StopWatch {

  @FunctionalInterface
  public interface Function {
    void call() throws Exception;
  }

  private static final class TaskInfo {

    private final Duration duration;
    private final String taskName;

    TaskInfo(final String taskName, final Duration duration) {
      requireNonNull(taskName, "Task name not provided");
      requireNonNull(duration, "Duration not provided");
      this.taskName = taskName;
      this.duration = duration;
    }

    public Duration getDuration() {
      return duration;
    }

    @Override
    public String toString() {
      final LocalTime durationLocal = LocalTime.ofNanoOfDay(duration.toNanos());
      return String.format("%s - <%s>", durationLocal.format(df), taskName);
    }
  }

  private static final DateTimeFormatter df =
      new DateTimeFormatterBuilder()
          .appendValue(HOUR_OF_DAY, 2)
          .appendLiteral(':')
          .appendValue(MINUTE_OF_HOUR, 2)
          .appendLiteral(':')
          .appendValue(SECOND_OF_MINUTE, 2)
          .appendFraction(NANO_OF_SECOND, 3, 3, true)
          .toFormatter();

  private final String id;
  private final List<TaskInfo> tasks = new LinkedList<>();
  private Duration totalDuration;

  public StopWatch(final String id) {
    this.id = id;
    totalDuration = Duration.ofNanos(0);
  }

  public String getId() {
    return id;
  }

  /**
   * Allows for a deferred conversion to a string. Useful in logging.
   *
   * @return String supplier.
   */
  public Supplier<String> report() {
    return () -> {
      final StringBuilder buffer = new StringBuilder(1024);

      final LocalTime totalDurationLocal = LocalTime.ofNanoOfDay(totalDuration.toNanos());
      buffer.append(
          String.format(
              "Total time taken for <%s> - %s hours%n", id, totalDurationLocal.format(df)));

      for (final TaskInfo task : tasks) {
        buffer.append(
            String.format(
                "-%5.1f%% - %s%n", calculatePercentage(task.getDuration(), totalDuration), task));
      }

      return buffer.toString();
    };
  }

  public void time(final String taskName, final Function task) throws Exception {

    requireNotBlank(taskName, "Task name not provided");
    requireNonNull(task, "Task not provided");

    Exception executionError = null;

    final Instant start = Instant.now();

    try {
      task.call();
    } catch (final Exception t) {
      executionError = t;
    }

    final Instant stop = Instant.now();
    final Duration runTime = Duration.between(start, stop);

    totalDuration = totalDuration.plus(runTime);

    final TaskInfo lastTaskInfo = new TaskInfo(taskName, runTime);
    tasks.add(lastTaskInfo);

    if (executionError != null) {
      throw executionError;
    }
  }

  private double calculatePercentage(final Duration duration, final Duration totalDuration) {
    final long totalMillis = totalDuration.toMillis();
    if (totalMillis == 0) {
      return 0;
    } else {
      return duration.toMillis() * 100D / totalMillis;
    }
  }
}
