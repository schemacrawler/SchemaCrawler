package us.fatehi.utility.scheduler;

import java.time.Clock;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import static java.util.Objects.requireNonNull;

public final class TestTaskRunner extends AbstractTaskRunner {

  public TestTaskRunner(String id, Clock clock) {
    super(id, clock);
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

    requireNonNull(taskDefinitions, "Tasks not provided");
    if (taskDefinitions.isEmpty()) {
      return Collections.emptyList();
    }

    final Collection<TimedTaskResult> runTaskResults = new CopyOnWriteArrayList<>();
    for (final TaskDefinition taskDefinition : taskDefinitions) {
      final TimedTaskResult taskResult = new TimedTask(taskDefinition, clock).call();
      runTaskResults.add(taskResult);
    }
    return runTaskResults;
  }
}
