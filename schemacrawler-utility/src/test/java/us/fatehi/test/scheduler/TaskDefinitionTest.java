package us.fatehi.test.scheduler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.scheduler.TaskDefinition;

public class TaskDefinitionTest {

  @Test
  public void taskDefinitionConstructor() {

    final TaskDefinition taskDefinition = new TaskDefinition("Task definition");
    assertThat(taskDefinition, is(not(nullValue())));
    assertThat(taskDefinition.getTaskName(), is("_task_definition"));
    assertThat(taskDefinition.toString(), is("_task_definition"));
  }

  @Test
  public void taskDefinitionConstructorWithTask() {

    final TaskDefinition.TaskRunnable taskRunnable =
        () -> {
          // Task logic
        };

    assertDoesNotThrow(() -> taskRunnable.run());

    final TaskDefinition taskDefinition = new TaskDefinition("Task definition", taskRunnable);
    assertThat(taskDefinition, is(not(nullValue())));
    assertThat(taskDefinition.getTaskName(), is("_task_definition"));
    assertThat(taskDefinition.getTask(), is(taskRunnable));
    assertThat(taskDefinition.toString(), is("_task_definition"));
  }
}
