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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import us.fatehi.utility.string.StringFormat;

public final class MultiThreadedTaskRunner extends AbstractTaskRunner {

  private static final Logger LOGGER = Logger.getLogger(MultiThreadedTaskRunner.class.getName());

  public static final int MIN_THREADS = 1;
  public static final int MAX_THREADS = 10;

  private final ExecutorService executorService;

  MultiThreadedTaskRunner(final String id, final int maxThreadsSuggested) {
    super(id);

    final int maxThreads;
    if (maxThreadsSuggested < MIN_THREADS) {
      maxThreads = MIN_THREADS;
    } else if (maxThreadsSuggested > MAX_THREADS) {
      maxThreads = MAX_THREADS;
    } else {
      maxThreads = maxThreadsSuggested;
    }
    LOGGER.log(
        Level.INFO, new StringFormat("Configured to run loaders in <%d> threads", maxThreads));

    executorService = Executors.newFixedThreadPool(maxThreads);
  }

  @Override
  public boolean isStopped() {
    return executorService.isShutdown();
  }

  @Override
  public void stop() throws ExecutionException {
    clear();
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
  void run(final List<TaskDefinition> taskDefinitions) throws Exception {

    if (executorService.isShutdown()) {
      throw new IllegalStateException("Task runner is stopped");
    }

    requireNonNull(taskDefinitions, "Tasks not provided");

    final CompletableFuture<Void> completableFuture =
        CompletableFuture.allOf(
            taskDefinitions.stream()
                .map(
                    task ->
                        CompletableFuture.runAsync(
                            new TimedTask(getTasks(), task), executorService))
                .collect(Collectors.toList())
                .toArray(new CompletableFuture[taskDefinitions.size()]));

    completableFuture.join();
  }
}
