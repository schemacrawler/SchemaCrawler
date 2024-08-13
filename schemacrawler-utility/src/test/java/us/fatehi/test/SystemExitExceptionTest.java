package us.fatehi.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.SystemExitException;

public class SystemExitExceptionTest {

  @Test
  public void testSystemExitException() {

    final SystemExitException systemExitException = new SystemExitException(1, "message");

    assertThat(systemExitException.getExitCode(), is(1));
    assertThat(systemExitException.getMessage(), is("message"));
    assertThat(systemExitException.toString(), is("Exit code 1: message"));
  }

  @Test
  public void testSystemExitExceptionWriter() {
    final SystemExitException systemExitException = new SystemExitException(1, "message");
    final StringWriter out = new StringWriter();
    systemExitException.printStackTrace(new PrintWriter(out));
    assertThat(out.toString().startsWith("Exit code 1: message"), is(true));
  }

  @Test
  public void testSystemExitExceptionStream() {
    final SystemExitException systemExitException = new SystemExitException(1, "message");
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    systemExitException.printStackTrace(new PrintStream(out));
    assertThat(out.toString().startsWith("Exit code 1: message"), is(true));
  }

  @Test
  public void testSystemExitExceptionNoPrint() {
    final SystemExitException systemExitException = new SystemExitException(1, "message");

    assertDoesNotThrow(
        () -> {
          systemExitException.printStackTrace();
        });
    assertDoesNotThrow(
        () -> {
          systemExitException.printStackTrace((PrintStream) null);
        });
    assertDoesNotThrow(
        () -> {
          systemExitException.printStackTrace((PrintWriter) null);
        });
  }
}
