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

public class CommandLineLoggingUtilityLogSafeArgumentsTest {

  private static LogCaptor logCaptor;

  @BeforeAll
  public static void setupLogCaptor() {
    logCaptor = LogCaptor.forClass(CommandLineLoggingUtility.class);
    logCaptor.setLogLevelToInfo();
  }

  @AfterAll
  public static void tearDown() {
    logCaptor.close();
  }

  @AfterEach
  public void clearLogs() {
    logCaptor.clearLogs();
  }

  @Test
  public void logSafeArguments_empty() {

    CommandLineLoggingUtility.logSafeArguments(new String[0]);

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""), matchesPattern("Command line: \n"));
  }

  @Test
  public void logSafeArguments_null() {

    CommandLineLoggingUtility.logSafeArguments(null);

    assertThat(logCaptor.getLogs(), hasSize(1));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
  }

  @Test
  public void logSafeArguments_password1() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password=pwd"});

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password2a() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password", "hello"});

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password2b() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password"});

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password3a() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password:env", "hello"});

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password3b() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password:env"});

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password4() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password="});

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password5() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password:env="});

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_simple() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "arg2"});

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\narg2"));
  }
}
