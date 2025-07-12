/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
   * Not quite a {@code Callable<Void>} (no "return null;" required) and not quite a Runnable
   * because it allows throwing checked exceptions.
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
