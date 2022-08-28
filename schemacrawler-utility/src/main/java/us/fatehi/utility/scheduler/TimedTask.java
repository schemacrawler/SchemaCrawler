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

import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

/**
 * Wrapper around a task definition that runs the task and times it. It puts timing information on a
 * shared thread-safe list for reporting. Throws an exception if the task does not succeed.
 */
class TimedTask implements Runnable {

  private final TaskDefinition task;
  private final Consumer<TaskInfo> addTaskResults;

  TimedTask(final Consumer<TaskInfo> taskResults, final TaskDefinition task) {
    this.addTaskResults = requireNonNull(taskResults, "Tasks results list not provided");
    this.task = requireNonNull(task, "Task not provided");
  }

  @Override
  public void run() {

    final Instant start = Instant.now();

    RuntimeException ex = null;
    try {
      task.run();
    } catch (final RuntimeException e) {
      ex = e;
    }

    final Instant stop = Instant.now();
    final Duration runTime = Duration.between(start, stop);

    final TaskInfo taskInfo = new TaskInfo(task.getTaskName(), runTime);
    addTaskResults.accept(taskInfo);

    if (ex != null) {
      throw ex;
    }
  }
}
