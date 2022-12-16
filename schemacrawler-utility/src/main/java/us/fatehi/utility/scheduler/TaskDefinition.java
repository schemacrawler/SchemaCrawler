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

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;
import static us.fatehi.utility.Utility.toSnakeCase;

import java.util.logging.Level;
import java.util.logging.Logger;

import us.fatehi.utility.string.StringFormat;

public final class TaskDefinition {

  /**
   * Not quite a Callable<Void> (no "return null;" required) and not quite a Runnable because it
   * allows throwing checked exceptions.
   */
  @FunctionalInterface
  public interface TaskRunnable {
    void run() throws Exception;
  }

  private static final Logger LOGGER = Logger.getLogger(TaskDefinition.class.getName());

  private final String taskName;
  private final TaskRunnable task;

  public TaskDefinition(final String taskName) {
    this.taskName = toSnakeCase(requireNotBlank(taskName, "Task name not provided"));
    this.task = () -> LOGGER.log(Level.INFO, new StringFormat("Not running task <%s>", taskName));
  }

  public TaskDefinition(final String taskName, final TaskRunnable task) {
    requireNotBlank(taskName, "Task name not provided");

    this.taskName = toSnakeCase(taskName);
    this.task = requireNonNull(task, "Task not provided");
  }

  public TaskRunnable getTask() {
    return task;
  }

  public String getTaskName() {
    return taskName;
  }

  @Override
  public String toString() {
    return taskName;
  }
}
