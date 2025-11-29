/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.scheduler;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

final class MainThreadTaskRunner extends AbstractTaskRunner {

  MainThreadTaskRunner(final String id) {
    super(id);
  }

  @Override
  public boolean isStopped() {
    return false;
  }

  @Override
  public void stop() {
    // No-op
  }

  @Override
  Collection<TimedTaskResult> runTimed(final Collection<TaskDefinition> taskDefinitions)
      throws Exception {
    final Collection<TimedTaskResult> runTaskResults = new CopyOnWriteArrayList<>();
    for (final TaskDefinition taskDefinition : taskDefinitions) {
      final TimedTaskResult taskResult = new TimedTask(taskDefinition, clock).call();
      runTaskResults.add(taskResult);
    }
    return runTaskResults;
  }
}
