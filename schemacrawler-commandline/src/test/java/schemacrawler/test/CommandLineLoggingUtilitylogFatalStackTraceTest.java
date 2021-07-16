package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.altindag.log.LogCaptor;
import schemacrawler.tools.commandline.utility.CommandLineLoggingUtility;

public class CommandLineLoggingUtilitylogFatalStackTraceTest {

  private static LogCaptor logCaptor;

  @BeforeAll
  public static void setupLogCaptor() {
    logCaptor = LogCaptor.forClass(CommandLineLoggingUtility.class);
  }

  @AfterAll
  public static void tearDown() {
    logCaptor.close();
  }

  @AfterEach
  public void clearLogs() {
    logCaptor.clearLogs();

    logCaptor.setLogLevelToTrace();
  }

  @Test
  public void logFatalStackTrace_null() {

    CommandLineLoggingUtility.logFatalStackTrace(null);

    assertThat(logCaptor.getLogs(), hasSize(0));
  }

  @Test
  public void logFatalStackTrace_simple() {

    CommandLineLoggingUtility.logFatalStackTrace(new NullPointerException("Bad bad exception"));

    assertThat(logCaptor.getLogs(), hasSize(1));
    assertThat(
        logCaptor.getLogs().get(0).replaceAll("\r", ""), matchesPattern("Bad bad exception"));
  }

  @Test
  public void logFatalStackTrace_simple_noInfo() {

    logCaptor.disableLogs();

    CommandLineLoggingUtility.logFatalStackTrace(new NullPointerException("Bad bad exception"));

    assertThat(logCaptor.getLogs(), hasSize(0));
  }
}
