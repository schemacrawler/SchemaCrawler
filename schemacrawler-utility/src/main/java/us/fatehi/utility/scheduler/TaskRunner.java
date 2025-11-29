/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.scheduler;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public interface TaskRunner extends AutoCloseable {

  int MIN_THREADS = 1;
  int MAX_THREADS = 10;

  /** Add a task definition to the list of tasks to run. */
  void add(TaskDefinition taskDefinition) throws Exception;

  /** Alias for stop for use in a try-catch block. */
  @Override
  default void close() throws Exception {
    stop();
  }

  /**
   * Id of this task runner.
   *
   * @return Id of task runner.
   */
  String getId();

  /**
   * Returns true if the task runner is stopped and is not accepting any more tasks.
   *
   * @return True if stopped.
   */
  boolean isStopped();

  /**
   * Allows for a deferred conversion to a string. Useful in logging.
   *
   * @return String supplier.
   * @throws Exception
   */
  Supplier<String> report();

  /**
   * Stop the task runner after waiting for all the threads to finish executing. No more tasks will
   * be accepted.
   *
   * @throws ExecutionException On an exception.
   */
  void stop();

  /**
   * Submit the current list of tasks for execution, and block until execution is complete. Then
   * clears the task list.
   *
   * @throws Exception On an exception in any one of the tasks.
   */
  void submit() throws Exception;
}
