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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

final class MultiThreadedTaskRunner extends AbstractTaskRunner {

  private static final Logger LOGGER = Logger.getLogger(MultiThreadedTaskRunner.class.getName());

  private final ExecutorService executorService;

  MultiThreadedTaskRunner(final String id, final int maxThreadsSuggested) {
    super(id);

    final int maxThreads;
    if (maxThreadsSuggested < TaskRunner.MIN_THREADS) {
      maxThreads = TaskRunner.MIN_THREADS;
    } else if (maxThreadsSuggested > TaskRunner.MAX_THREADS) {
      maxThreads = TaskRunner.MAX_THREADS;
    } else {
      maxThreads = maxThreadsSuggested;
    }
    executorService = Executors.newFixedThreadPool(maxThreads);
    LOGGER.log(
        Level.INFO,
        new StringFormat(
            "Started thread pool <%s> for <%s> with <%d> threads",
            executorService, id, maxThreads));
  }

  @Override
  public boolean isStopped() {
    return executorService.isShutdown();
  }

  @Override
  public void stop() {
    try {
      executorService.shutdown();
      if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
        executorService.shutdownNow();
      }
    } catch (final InterruptedException ex) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  @Override
  Collection<TimedTaskResult> runTimed(final Collection<TaskDefinition> taskDefinitions)
      throws Exception {

    requireNonNull(taskDefinitions, "Tasks not provided");
    if (taskDefinitions.isEmpty()) {
      return Collections.emptyList();
    }
    if (isStopped()) {
      throw new IllegalStateException("Task runner is stopped");
    }

    try {
      final Collection<TimedTask> timedTasks = new CopyOnWriteArrayList<>();
      for (final TaskDefinition taskDefinition : taskDefinitions) {
        final TimedTask timedTask = new TimedTask(taskDefinition);
        timedTasks.add(timedTask);
      }

      final Collection<TimedTaskResult> runTaskResults = new CopyOnWriteArrayList<>();

      final List<Future<TimedTaskResult>> futureResults =
          executorService.invokeAll(timedTasks, 1, TimeUnit.HOURS);
      for (final Future<TimedTaskResult> futureResult : futureResults) {
        final TimedTaskResult timedTaskResult = futureResult.get();
        runTaskResults.add(timedTaskResult);
      }

      return runTaskResults;
    } catch (final ExecutionException e) {
      final Throwable cause = e.getCause();
      if (cause instanceof Exception) {
        throw (Exception) cause;
      } else {
        throw new RunnerException(cause);
      }
    }
  }
}
