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
import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import us.fatehi.utility.string.StringFormat;

public final class StopWatch {

  private static final Logger LOGGER = Logger.getLogger(StopWatch.class.getName());

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

  private final List<TaskInfo> tasks;
  private final ExecutorService executorService;

  public StopWatch(final String id) {
    this.id = id;
    tasks = new LinkedList<>();
    executorService = Executors.newFixedThreadPool(5);
  }

  public String getId() {
    return id;
  }

  public void noOp(final String taskName) {
    LOGGER.log(Level.INFO, new StringFormat("Not running <%s>", taskName));

    final TaskInfo taskInfo = new TaskInfo(taskName, Duration.ZERO);
    tasks.add(taskInfo);
  }

  /**
   * Allows for a deferred conversion to a string. Useful in logging.
   *
   * @return String supplier.
   * @throws Exception
   */
  public Supplier<String> report() {

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

      return buffer.toString();
    };
  }

  public void run(final TaskDefinition... taskDefinitions) throws Exception {

    requireNonNull(taskDefinitions, "Tasks not provided");

    final CompletableFuture<Void> completableFuture =
        CompletableFuture.allOf(
            Arrays.stream(taskDefinitions)
                .map(
                    task -> CompletableFuture.runAsync(new TimedTask(tasks, task), executorService))
                .collect(Collectors.toList())
                .toArray(new CompletableFuture[taskDefinitions.length]));

    completableFuture.join();
  }

  public void stop() throws ExecutionException {
    executorService.shutdown();

    try {
      if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
        executorService.shutdownNow();
      }
    } catch (final InterruptedException ex) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
