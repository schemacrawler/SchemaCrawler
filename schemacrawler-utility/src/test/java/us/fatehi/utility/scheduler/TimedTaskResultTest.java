package us.fatehi.utility.scheduler;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import org.junit.jupiter.api.Test;

public class TimedTaskResultTest {

  private static final DateTimeFormatter df =
      new DateTimeFormatterBuilder()
          .appendValue(HOUR_OF_DAY, 2)
          .appendLiteral(':')
          .appendValue(MINUTE_OF_HOUR, 2)
          .appendLiteral(':')
          .appendValue(SECOND_OF_MINUTE, 2)
          .appendFraction(NANO_OF_SECOND, 3, 3, true)
          .toFormatter();

  @Test
  public void testConstructorAndGetters() {
    final Duration duration = duration();
    final String taskName = "task";
    final TimedTaskResult result = new TimedTaskResult(taskName, duration, null);

    assertThat(result.getDuration(), is(duration));

    final LocalTime durationLocal = LocalTime.ofNanoOfDay(duration.toNanos());
    final String expected = String.format("%s - <%s>", durationLocal.format(df), taskName);

    assertThat(result.toString(), is(expected));
  }

  @Test
  public void testException() {
    final Duration duration = duration();
    final String taskName = "task";

    final TimedTaskResult result1 = new TimedTaskResult(taskName, duration, null);

    assertThat(result1.hasException(), is(false));
    assertThat(result1.getException(), is(nullValue()));

    RuntimeException exception = new RuntimeException();
    final TimedTaskResult result2 = new TimedTaskResult(taskName, duration, exception);

    assertThat(result2.hasException(), is(true));
    assertThat(result2.getException(), is(exception));
  }

  private Duration duration() {
    final LocalTime startTime = LocalTime.of(10, 0, 0);
    final LocalTime endTime = LocalTime.of(10, 0, 10);
    final Duration duration = Duration.between(startTime, endTime);
    return duration;
  }
}
