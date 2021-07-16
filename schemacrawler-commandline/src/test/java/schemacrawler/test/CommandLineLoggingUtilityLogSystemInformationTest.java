package schemacrawler.test;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.altindag.log.LogCaptor;
import schemacrawler.tools.commandline.utility.CommandLineLoggingUtility;

public class CommandLineLoggingUtilityLogSystemInformationTest {

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

    logCaptor.setLogLevelToInfo();
  }

  @Test
  public void logSystemClasspath_simple() {

    CommandLineLoggingUtility.logSystemClasspath();

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Classpath:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""), matchesPattern("LD_LIBRARY_PATH: \n"));
  }

  @Test
  public void logSystemClasspath_simple_noConfig() {

    logCaptor.disableLogs();

    CommandLineLoggingUtility.logSystemClasspath();

    assertThat(logCaptor.getLogs(), hasSize(0));
  }

  @Test
  public void logSystemProperties_simple() {

    CommandLineLoggingUtility.logSystemProperties();

    assertThat(logCaptor.getLogs(), hasSize(1));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("System properties:"));
  }

  @Test
  public void logSystemProperties_simple_noConfig() {

    logCaptor.disableLogs();

    CommandLineLoggingUtility.logSystemProperties();

    assertThat(logCaptor.getLogs(), hasSize(0));
  }
}
