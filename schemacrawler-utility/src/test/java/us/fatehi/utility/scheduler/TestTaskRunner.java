/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.scheduler;

import java.time.Clock;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public final class TestTaskRunner extends AbstractTaskRunner {

  boolean isStopped;
  int size;

  public TestTaskRunner(String id, Clock clock) {
    super(id, clock);
  }

  @Override
  public boolean isStopped() {
    return isStopped;
  }

  @Override
  public void stop() {
    isStopped = true;
  }

  public int size() {
    return size;
  }

  @Override
  Collection<TimedTaskResult> runTimed(final Collection<TaskDefinition> taskDefinitions)
      throws Exception {
    size = taskDefinitions.size();

    final Collection<TimedTaskResult> runTaskResults = new CopyOnWriteArrayList<>();
    for (final TaskDefinition taskDefinition : taskDefinitions) {
      final TimedTaskResult taskResult = new TimedTask(taskDefinition, clock).call();
      runTaskResults.add(taskResult);
    }
    return runTaskResults;
  }
}
