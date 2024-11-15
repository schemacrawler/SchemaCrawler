package us.fatehi.utility.scheduler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

public class TaskRunnerTest {

  @Test
  public void testTaskRunner() throws Exception {

    try (final TaskRunner taskRunner =
        new TestTaskRunner("test_id", Clock.fixed(Instant.now(), ZoneId.of("UTC"))); ) {

      taskRunner.add(new TaskDefinition("task 1"));
      taskRunner.submit();
      final Supplier<String> report = taskRunner.report();

      assertThat(
          report.get().replaceAll("\\R", ""),
          is(
              "Total time taken for <test_id> - 00:00:00.000 hours"
                  + "-  0.0% - 00:00:00.000 - <task_1>"));
    }
  }
}
