/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
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

    final Instant start = Instant.now(clock);

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
}
