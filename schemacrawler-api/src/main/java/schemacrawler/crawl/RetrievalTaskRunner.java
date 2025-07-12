/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoRetrieval;
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

  public RetrievalTaskRunner(
      final String runId, final SchemaInfoLevel infoLevel, final int maxThreads) {

    requireNotBlank(runId, "No SchemaCrawler run id provided");
    this.infoLevel = requireNonNull(infoLevel, "No info-level provided");

    taskRunner = TaskRunners.getTaskRunner(runId, maxThreads);
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

  /**
   * Allows for a deferred conversion to a string. Useful in logging.
   *
   * @return String supplier.
   */
  public void stopAndLogTime() {
    try {
      taskRunner.stop();
    } finally {
      LOGGER.log(Level.INFO, taskRunner.report());
    }
  }

  public void submit() throws Exception {
    taskRunner.submit();
  }

  private void add(
      final String retrievalName,
      final boolean shouldRun,
      final TaskDefinition.TaskRunnable function)
      throws Exception {
    if (shouldRun) {
      taskRunner.add(new TaskDefinition(retrievalName, function));
    } else {
      taskRunner.add(new TaskDefinition(retrievalName));
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
