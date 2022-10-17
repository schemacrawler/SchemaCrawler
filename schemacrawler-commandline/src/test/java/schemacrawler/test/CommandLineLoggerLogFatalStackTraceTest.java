package schemacrawler.test;

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

import schemacrawler.tools.commandline.utility.CommandLineLogger;

public class CommandLineLoggerLogFatalStackTraceTest {

  @Test
  public void logFatalStackTrace_null() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.SEVERE)).thenReturn(true);

    final CommandLineLogger commandLineLogger = new CommandLineLogger(logger);

    commandLineLogger.logFatalStackTrace(null);

    verify(logger, never()).isLoggable(any(Level.class));
  }

  @Test
  public void logFatalStackTrace_simple() {
    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.SEVERE)).thenReturn(true);

    final CommandLineLogger commandLineLogger = new CommandLineLogger(logger);

    commandLineLogger.logFatalStackTrace(new NullPointerException("Bad bad exception"));

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture(), exceptionCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.SEVERE));
    assertThat(messageCaptor.getValue(), matchesPattern("Bad bad exception"));
    assertThat(
        exceptionCaptor.getValue().getClass().getName(), is("java.lang.NullPointerException"));
  }

  @Test
  public void logFatalStackTrace_simple_noInfo() {
    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.SEVERE)).thenReturn(false);

    final CommandLineLogger commandLineLogger = new CommandLineLogger(logger);

    commandLineLogger.logFatalStackTrace(new NullPointerException("Bad bad exception"));

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);

    verify(logger).isLoggable(levelCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.SEVERE));
    verify(logger, never()).log(any(Level.class), any(String.class), any(Exception.class));
  }
}
