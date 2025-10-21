/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.CommandlineTestUtility.createLoadedSchemaCrawlerShellState;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.addPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.commandPluginCommands;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.commandline.SchemaCrawlerShellCommands;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public class ExecuteCommandTest {

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void executeBadCommand(
      final DatabaseConnectionSource dataSource, final TestContext testContext) throws Exception {

    class ExceptionHandler implements IExecutionExceptionHandler {

      private Exception exception;

      public Exception getException() {
        return exception;
      }

      @Override
      public int handleExecutionException(
          final Exception ex, final CommandLine commandLine, final ParseResult parseResult)
          throws Exception {
        exception = ex;
        return 1;
      }
    }
    final ExceptionHandler exceptionHandler = new ExceptionHandler();

    final CommandLine commandLine = createShellCommandLine(dataSource);
    commandLine.setExecutionExceptionHandler(exceptionHandler);

    final Path testOutputFile = IOUtility.createTempFilePath("test", ".txt");
    final String[] args =
        new String[] {
          "execute",
          "-c",
          "test",
          "--unknown-parameter",
          "some-value",
          "-o",
          testOutputFile.toString()
        };

    final int exitCode = commandLine.execute(args);
    assertThat(exitCode, is(1));
    assertThat(exceptionHandler.getException().getMessage(), is("Unknown command <test>"));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void executeSchemaCommand(
      final DatabaseConnectionSource dataSource, final TestContext testContext) throws Exception {

    final CommandLine commandLine = createShellCommandLine(dataSource);

    final Path testOutputFile = IOUtility.createTempFilePath("test", ".txt");
    final String[] args =
        new String[] {"execute", "-c", "schema", "--no-info", "-o", testOutputFile.toString()};

    final int exitCode = commandLine.execute(args);
    assertThat(exitCode, is(0));
    assertThat(
        outputOf(testOutputFile),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".txt")));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void executeTestCommand(
      final DatabaseConnectionSource dataSource, final TestContext testContext) throws Exception {

    int exitCode;
    final CommandLine commandLine = createShellCommandLine(dataSource);

    final Path testOutputFile1 = IOUtility.createTempFilePath("test", ".1.txt");
    exitCode =
        commandLine.execute("execute", "-c", "test-command", "-o", testOutputFile1.toString());
    assertThat(exitCode, is(0));
    assertThat(
        outputOf(testOutputFile1),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".1.txt")));

    final Path testOutputFile2 = IOUtility.createTempFilePath("test", ".2.txt");
    exitCode =
        commandLine.execute(
            "execute",
            "-c",
            "test-command",
            "-o",
            testOutputFile2.toString(),
            "--test-command-parameter",
            "known-value",
            "--unknown-parameter",
            "some-value");
    assertThat(exitCode, is(0));
    assertThat(
        outputOf(testOutputFile2),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".2.txt")));

    final Path testOutputFile = IOUtility.createTempFilePath("test", ".3.txt");
    exitCode =
        commandLine.execute(
            "execute", "-c", "schema", "--no-info", "-o", testOutputFile.toString());
    assertThat(exitCode, is(0));
    assertThat(
        outputOf(testOutputFile),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".3.txt")));

    final Path testOutputFile4 = IOUtility.createTempFilePath("test", ".4.txt");
    exitCode =
        commandLine.execute("execute", "-c", "test-command", "-o", testOutputFile4.toString());
    assertThat(exitCode, is(0));
    assertThat(
        outputOf(testOutputFile4),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".4.txt")));
  }

  private CommandLine createShellCommandLine(final DatabaseConnectionSource dataSource) {
    final ShellState state = createLoadedSchemaCrawlerShellState(dataSource);
    final SchemaCrawlerShellCommands commands = new SchemaCrawlerShellCommands();
    final CommandLine commandLine = newCommandLine(commands, new StateFactory(state));
    final CommandLine executeCommandLine =
        commandLine.getSubcommands().getOrDefault("execute", null);
    if (executeCommandLine != null) {
      addPluginCommands(executeCommandLine, commandPluginCommands);
      commandLine.addSubcommand(executeCommandLine);
    }
    return commandLine;
  }
}
