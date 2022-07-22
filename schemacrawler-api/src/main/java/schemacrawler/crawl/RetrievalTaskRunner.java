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
package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoRetrieval;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import us.fatehi.utility.scheduler.TaskDefinition;
import us.fatehi.utility.scheduler.TaskRunner;
import us.fatehi.utility.scheduler.TaskRunners;

/**
 * Builds a list of task definitions that can be run in parallel. These are then submitted to be run
 * (in parallel) in a blocked way until all are complete.
 */
public final class RetrievalTaskRunner {

  private static final Logger LOGGER = Logger.getLogger(RetrievalTaskRunner.class.getName());

  private final TaskRunner taskRunner;
  private final SchemaInfoLevel infoLevel;
  private final List<TaskDefinition> taskDefinitions;

  public RetrievalTaskRunner(final SchemaInfoLevel infoLevel, final int maxThreads) {
    this.infoLevel = requireNonNull(infoLevel, "No info-level provided");

    taskRunner = TaskRunners.getTaskRunner(infoLevel.getTag(), maxThreads);
    taskDefinitions = new CopyOnWriteArrayList<>();
  }

  public RetrievalTaskRunner add(
      final SchemaInfoRetrieval retrieval,
      final TaskDefinition.TaskRunnable function,
      final SchemaInfoRetrieval... additionalRetrievals)
      throws Exception {
    final boolean shouldRun = shouldRun(retrieval) && shouldRun(additionalRetrievals);
    add(retrieval.name(), shouldRun, function);
    return this;
  }

  public RetrievalTaskRunner add(
      final String retrievalName,
      final TaskDefinition.TaskRunnable function,
      final SchemaInfoRetrieval... additionalRetrievals)
      throws Exception {
    final boolean shouldRun = shouldRun(additionalRetrievals);
    add(retrievalName, shouldRun, function);
    return this;
  }

  public boolean isStopped() {
    return taskRunner.isStopped();
  }

  /**
   * Allows for a deferred conversion to a string. Useful in logging.
   *
   * @return String supplier.
   */
  public void stopAndLogTime() throws ExecutionException {
    ExecutionException exception = null;
    try {
      taskRunner.stop();
    } catch (final ExecutionException e) {
      exception = e;
    }
    LOGGER.log(Level.INFO, taskRunner.report());
    if (exception != null) {
      throw exception;
    }
  }

  public void submit() throws Exception {
    try {
      taskRunner.run(taskDefinitions.toArray(new TaskDefinition[taskDefinitions.size()]));
    } catch (final CompletionException e) {
      final Throwable cause = e.getCause();
      if (cause != null) {
        throw new ExecutionRuntimeException(cause);
      } else {
        throw e;
      }
    } finally {
      taskDefinitions.clear();
    }
  }

  private void add(
      final String retrievalName,
      final boolean shouldRun,
      final TaskDefinition.TaskRunnable function)
      throws Exception {

    if (taskRunner.isStopped()) {
      throw new IllegalStateException("Task runner is stopped");
    }

    if (shouldRun) {
      taskDefinitions.add(new TaskDefinition(retrievalName, function));
    } else {
      taskDefinitions.add(new TaskDefinition(retrievalName));
    }
  }

  private boolean shouldRun(final SchemaInfoRetrieval... additionalRetrievals) {
    boolean shouldRun = true;
    if (additionalRetrievals != null && additionalRetrievals.length > 0) {
      for (final SchemaInfoRetrieval additionalRetrieval : additionalRetrievals) {
        shouldRun = shouldRun && infoLevel.is(additionalRetrieval);
      }
    }
    return shouldRun;
  }
}
