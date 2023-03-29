package us.fatehi.utility.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import us.fatehi.utility.UtilityLogger;

public class UtilityLoggerLogFatalStackTraceTest {

  @Test
  public void logFatalStackTrace_null() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.SEVERE)).thenReturn(true);

    final UtilityLogger UtilityLogger = new UtilityLogger(logger);

    UtilityLogger.logFatalStackTrace(null);

    verify(logger, never()).isLoggable(any(Level.class));
  }

  @Test
  public void logFatalStackTrace_simple() {
    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.SEVERE)).thenReturn(true);

    final UtilityLogger UtilityLogger = new UtilityLogger(logger);

    UtilityLogger.logFatalStackTrace(new NullPointerException("Bad bad exception"));

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture(), exceptionCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.SEVERE));
    assertThat(messageCaptor.getValue(), matchesPattern("Bad bad exception"));
    assertThat(exceptionCaptor.getValue().getClass().getName(),
        is("java.lang.NullPointerException"));
  }

  @Test
  public void logFatalStackTrace_simple_noInfo() {
    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.SEVERE)).thenReturn(false);

    final UtilityLogger UtilityLogger = new UtilityLogger(logger);

    UtilityLogger.logFatalStackTrace(new NullPointerException("Bad bad exception"));

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);

    verify(logger).isLoggable(levelCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.SEVERE));
    verify(logger, never()).log(any(Level.class), any(String.class), any(Exception.class));
  }
}
