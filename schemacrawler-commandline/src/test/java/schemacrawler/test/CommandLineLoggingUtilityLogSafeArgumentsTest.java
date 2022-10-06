package schemacrawler.test;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import nl.altindag.log.LogCaptor;
import schemacrawler.tools.commandline.utility.CommandLineLoggingUtility;

@Disabled
public class CommandLineLoggingUtilityLogSafeArgumentsTest {

  private LogCaptor logCaptor;

  @AfterEach
  public void _clearLogs() {
    logCaptor.clearLogs();
    logCaptor.close();
    logCaptor = null;
  }

  @BeforeEach
  public void _setupLogCaptor() {
    logCaptor = LogCaptor.forClass(CommandLineLoggingUtility.class);
    logCaptor.setLogLevelToInfo();
  }

  @Test
  public void logSafeArguments_empty() {

    CommandLineLoggingUtility.logSafeArguments(new String[0], null);

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""), matchesPattern("Command line: \n"));
  }

  @Test
  public void logSafeArguments_null() {

    CommandLineLoggingUtility.logSafeArguments(null, null);

    assertThat(logCaptor.getLogs(), hasSize(1));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
  }

  @Test
  public void logSafeArguments_password1() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password=pwd"}, null);

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password2a() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password", "hello"}, null);

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password2b() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password"}, null);

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password3a() {

    CommandLineLoggingUtility.logSafeArguments(
        new String[] {"arg1", "--password:env", "hello"}, null);

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password3b() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password:env"}, null);

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password4() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password="}, null);

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password5() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "--password:env="}, null);

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(logCaptor.getInfoLogs().get(0), startsWith("Environment:"));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_simple() {

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "arg2"}, null);

    assertThat(logCaptor.getLogs(), hasSize(2));
    assertThat(
        logCaptor.getInfoLogs().get(1).replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\narg2"));
  }

  @Test
  public void logSafeArguments_simple_noInfo() {

    logCaptor.disableLogs();

    CommandLineLoggingUtility.logSafeArguments(new String[] {"arg1", "arg2"}, null);

    assertThat(logCaptor.getLogs(), hasSize(0));
  }
}
