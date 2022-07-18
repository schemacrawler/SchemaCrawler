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

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import us.fatehi.utility.string.StringFormat;

public class TimedTask implements Runnable {

  @FunctionalInterface
  public interface Function {
    void call() throws Exception;
  }

  private static final Logger LOGGER = Logger.getLogger(TimedTask.class.getName());

  private final String taskName;
  private final TimedTask.Function task;
  private final List<TaskInfo> tasks;

  public TimedTask(
      final List<TaskInfo> tasks, final String taskName, final TimedTask.Function task) {
    this.tasks = requireNonNull(tasks, "Tasks list not provided");
    this.taskName = requireNotBlank(taskName, "Job name not provided");
    this.task = requireNonNull(task, "Job not provided");
  }

  @Override
  public void run() {

    LOGGER.log(
        Level.INFO,
        new StringFormat(
            "Running <%s> on thread <%s>", taskName, Thread.currentThread().getName()));

    final Instant start = Instant.now();

    Exception ex = null;
    try {
      task.call();
    } catch (final Exception e) {
      ex = e;
    }

    final Instant stop = Instant.now();
    final Duration runTime = Duration.between(start, stop);

    final TaskInfo taskInfo = new TaskInfo(taskName, runTime);
    tasks.add(taskInfo);

    if (ex != null) {
      throw new RuntimeException(
          String.format(
              "Exception running <%s> on thread <%s>: %s",
              taskName, Thread.currentThread().getName(), ex.getMessage()),
          ex);
    }
  }
}
