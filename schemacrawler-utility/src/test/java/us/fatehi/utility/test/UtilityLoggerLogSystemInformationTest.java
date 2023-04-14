package us.fatehi.utility.test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import us.fatehi.utility.UtilityLogger;

public class UtilityLoggerLogSystemInformationTest {

  @Test
  public void logSystemClasspath_simple() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.CONFIG)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSystemClasspath();

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger, atLeastOnce()).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getAllValues(), hasItem(Level.CONFIG));
    assertThat(messageCaptor.getAllValues().get(0), startsWith("Classpath:"));
    assertThat(
        messageCaptor.getAllValues().get(1).replaceAll("\\R", "\n"),
        matchesPattern("LD_LIBRARY_PATH: \n"));
  }

  @Test
  public void logSystemClasspath_simple_noConfig() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.CONFIG)).thenReturn(false);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSystemClasspath();

    verify(logger, never()).log(any(Level.class), any(String.class));
  }

  @Test
  public void logSystemProperties_simple() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.CONFIG)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSystemProperties();

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.CONFIG));
    assertThat(messageCaptor.getValue(), startsWith("System properties:"));
  }

  @Test
  public void logSystemProperties_simple_noConfig() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.CONFIG)).thenReturn(false);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSystemProperties();

    verify(logger, never()).log(any(Level.class), any(String.class));
  }
}
