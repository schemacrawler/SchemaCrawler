/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;
import static schemacrawler.tools.utility.SchemaCrawlerUtility.getCatalog;
import static us.fatehi.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.typesafe.config.ConfigFactory;

import picocli.CommandLine;
import picocli.CommandLine.PicocliException;
import schemacrawler.Main;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.TestUtility;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public final class CommandlineTestUtility {

  private static final SchemaCrawlerOptions schemaCrawlerOptions =
      DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

  public static Path commandlineExecution(
      final DatabaseConnectionInfo connectionInfo,
      final String command,
      final Map<String, String> argsMap,
      final boolean loadHsqlDbInformationSchemaViews,
      final OutputFormat outputFormat)
      throws Exception {
    final Path propertiesFile;
    if (loadHsqlDbInformationSchemaViews) {
      propertiesFile = DatabaseTestUtility.tempHsqldbConfig();
    } else {
      propertiesFile = TestUtility.savePropertiesToTempFile(new Properties());
    }

    return commandlineExecution(
        connectionInfo, command, argsMap, propertiesFile, outputFormat.getFormat());
  }

  public static Path commandlineExecution(
      final DatabaseConnectionInfo connectionInfo,
      final String command,
      final Map<String, String> argsMap,
      final Map<String, Object> config,
      final OutputFormat outputFormat)
      throws Exception {
    return commandlineExecution(
        connectionInfo, command, argsMap, writeConfigToTempFile(config), outputFormat.getFormat());
  }

  public static Path commandlineExecution(
      final DatabaseConnectionInfo connectionInfo,
      final String command,
      final Map<String, String> argsMap,
      final OutputFormat outputFormat)
      throws Exception {
    return commandlineExecution(
        connectionInfo, command, argsMap, (Path) null, outputFormat.getFormat());
  }

  public static Path commandlineExecution(
      final DatabaseConnectionInfo connectionInfo,
      final String command,
      final Map<String, String> extraArgsMap,
      final Path propertiesFile,
      final String outputFormatValue,
      final Path out)
      throws Exception {

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--url", connectionInfo.connectionUrl());
    argsMap.put("--user", "sa");
    argsMap.put("--password", "");

    System.clearProperty("config.file");
    if (propertiesFile != null) {
      System.setProperty("config.file", propertiesFile.toString());
    }
    ConfigFactory.invalidateCaches();

    argsMap.put("-c", command);
    argsMap.put("--output-format", outputFormatValue);
    argsMap.put("--output-file", out.toString());

    // Override and add to command-line arguments
    if (extraArgsMap != null) {
      argsMap.putAll(extraArgsMap);
    }

    Main.main(flattenCommandlineArgs(argsMap));

    return out;
  }

  public static Path commandlineExecution(
      final DatabaseConnectionInfo connectionInfo,
      final String command,
      final Map<String, String> argsMap,
      final String outputFormatValue)
      throws Exception {
    return commandlineExecution(connectionInfo, command, argsMap, (Path) null, outputFormatValue);
  }

  public static ShellState createConnectedSchemaCrawlerShellState(
      final DatabaseConnectionSource dataSource) {

    final ShellState state = new ShellState();
    state.setSchemaCrawlerOptions(schemaCrawlerOptions);
    state.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);
    state.setDataSource(dataSource); // is-connected
    return state;
  }

  public static ShellState createLoadedSchemaCrawlerShellState(
      final DatabaseConnectionSource dataSource) {

    final Catalog catalog =
        getCatalog(dataSource, schemaRetrievalOptionsDefault, schemaCrawlerOptions, new Config());

    final ShellState state = createConnectedSchemaCrawlerShellState(dataSource);
    state.setCatalog(catalog); // is-loaded
    return state;
  }

  public static void executeCommandInTest(final Object object, final String[] args)
      throws Throwable {

    class SaveExceptionHandler
        implements CommandLine.IParameterExceptionHandler, CommandLine.IExecutionExceptionHandler {

      private Throwable lastException;

      @Override
      public int handleExecutionException(
          final Exception ex,
          final CommandLine commandLine,
          final CommandLine.ParseResult parseResult)
          throws Exception {
        lastException = ex;
        if (ex instanceof PicocliException picocliException) {
          if (picocliException.getCause() != null) {
            lastException = picocliException.getCause();
          }
        }
        return 0;
      }

      @Override
      public int handleParseException(final CommandLine.ParameterException ex, final String[] args)
          throws Exception {
        lastException = ex;
        return 0;
      }

      public void throwOnException() throws Throwable {
        if (lastException != null) {
          throw lastException;
        }
      }
    }

    final SaveExceptionHandler saveExceptionHandler = new SaveExceptionHandler();
    final CommandLine commandLine = newCommandLine(object, null);
    commandLine.setParameterExceptionHandler(saveExceptionHandler);
    commandLine.setExecutionExceptionHandler(saveExceptionHandler);
    commandLine.execute(args);
    saveExceptionHandler.throwOnException();
  }

  public static Path writeConfigToTempFile(final Map<String, ?> config) throws IOException {
    final Properties configProperties = new Properties();
    if (config != null) {
      configProperties.putAll(config);
    }
    return TestUtility.savePropertiesToTempFile(configProperties);
  }

  private static Path commandlineExecution(
      final DatabaseConnectionInfo connectionInfo,
      final String command,
      final Map<String, String> argsMap,
      final Path propertiesFile,
      final String outputFormatValue)
      throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      commandlineExecution(
          connectionInfo, command, argsMap, propertiesFile, outputFormatValue, out.getFilePath());
    }
    return testout.getFilePath();
  }

  private CommandlineTestUtility() {
    // Prevent instantiation
  }
}
