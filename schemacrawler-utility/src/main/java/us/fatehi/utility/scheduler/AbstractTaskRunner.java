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
package us.fatehi.utility.scheduler;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

abstract class AbstractTaskRunner implements TaskRunner {

  private final String id;
  private final List<TaskDefinition> taskDefinitions;
  private final List<TaskInfo> tasks;

  public AbstractTaskRunner(final String id) {
    this.id = requireNotBlank(id, "No id provided");

    taskDefinitions = new CopyOnWriteArrayList<>();
    tasks = new CopyOnWriteArrayList<>();
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
            } else {
              return duration.toMillis() * 100D / totalMillis;
            }
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
      for (final TaskInfo task : tasks) {
        totalDuration = totalDuration.plus(task.getDuration());
      }

      final StringBuilder buffer = new StringBuilder(1024);

      final LocalTime totalDurationLocal = LocalTime.ofNanoOfDay(totalDuration.toNanos());
      buffer.append(
          String.format(
              "Total time taken for <%s> - %s hours%n", id, totalDurationLocal.format(df)));

      for (final TaskInfo task : tasks) {
        buffer.append(
            String.format(
                "-%5.1f%% - %s%n",
                calculatePercentage.apply(task.getDuration(), totalDuration), task));
      }

      tasks.clear();

      return buffer.toString();
    };
  }

  @Override
  public abstract void stop() throws ExecutionException;

  @Override
  public final void submit() throws Exception {
    run(taskDefinitions);
    clear();
  }

  void clear() {
    taskDefinitions.clear();
  }

  List<TaskInfo> getTasks() {
    return tasks;
  }

  abstract void run(final List<TaskDefinition> taskDefinitions) throws Exception;
}
