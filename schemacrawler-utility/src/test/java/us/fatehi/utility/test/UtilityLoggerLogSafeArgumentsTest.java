/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

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

public class UtilityLoggerLogSafeArgumentsTest {

  @Test
  public void logSafeArguments_empty() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(new String[0]);

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.INFO));
    assertThat(messageCaptor.getValue().replaceAll("\r", ""), matchesPattern("Command line: \n"));
  }

  @Test
  public void logSafeArguments_null() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(null);

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger, never()).log(levelCaptor.capture(), messageCaptor.capture());
  }

  @Test
  public void logSafeArguments_nullArgument() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(new String[] {null, "an-argument"});

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.INFO));
    assertThat(
        messageCaptor.getValue().replaceAll("\r", ""),
        matchesPattern("Command line: \nan-argument"));
  }

  @Test
  public void logSafeArguments_password1() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(new String[] {"arg1", "--password=pwd"});

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.INFO));
    assertThat(
        messageCaptor.getValue().replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password2a() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(new String[] {"arg1", "--password", "hello"});

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.INFO));
    assertThat(
        messageCaptor.getValue().replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password2b() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(new String[] {"arg1", "--password"});

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.INFO));
    assertThat(
        messageCaptor.getValue().replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password3a() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(new String[] {"arg1", "--password:env", "hello"});

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.INFO));
    assertThat(
        messageCaptor.getValue().replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password3b() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(new String[] {"arg1", "--password:env"});

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.INFO));
    assertThat(
        messageCaptor.getValue().replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password4() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(new String[] {"arg1", "--password="});

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.INFO));
    assertThat(
        messageCaptor.getValue().replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_password5() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(new String[] {"arg1", "--password:env="});

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.INFO));
    assertThat(
        messageCaptor.getValue().replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\n<password provided>"));
  }

  @Test
  public void logSafeArguments_simple() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(true);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(new String[] {"arg1", "arg2"});

    final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
    final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    verify(logger).log(levelCaptor.capture(), messageCaptor.capture());

    assertThat(levelCaptor.getValue(), is(Level.INFO));
    assertThat(
        messageCaptor.getValue().replaceAll("\r", ""),
        matchesPattern("Command line: \narg1\narg2"));
  }

  @Test
  public void logSafeArguments_simple_noInfo() {

    final Logger logger = mock(Logger.class);
    when(logger.isLoggable(Level.INFO)).thenReturn(false);

    final UtilityLogger commandLineLogger = new UtilityLogger(logger);

    commandLineLogger.logSafeArguments(new String[] {"arg1", "arg2"});

    verify(logger, never()).log(any(Level.class), any(String.class));
  }
}
