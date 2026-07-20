/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;

@Command(
    name = "schemaspy",
    mixinStandardHelpOptions = true,
    sortOptions = false,
    description = "SchemaSpy compatibility wrapper for SchemaCrawler Scribe (OKF output).")
public final class SchemaSpyMain implements Runnable {

  public static int execute(final String... args) {
    requireNonNull(args, "No command-line arguments provided");
    final SchemaSpyMain command = new SchemaSpyMain();
    final CommandLine commandLine = new CommandLine(command);
    commandLine.setExecutionExceptionHandler(
        (exception, cmd, parseResult) -> {
          cmd.getErr().println(exception.getMessage());
          return 1;
        });
    return commandLine.execute(args);
  }

  public static void main(final String[] args) {
    System.exit(execute(args));
  }

  @Option(
      names = "-dbhelp",
      description = "Print supported SchemaSpy database types for -t and exit")
  private boolean dbhelp;

  @Option(names = "-configFile", paramLabel = "filePath", description = "Compatibility config file")
  private String configFile;

  @Option(names = "-o", paramLabel = "outputPath", description = "Output file or directory path")
  private String outputPath = "schemaspy-output.zip";

  @Option(names = "-t", paramLabel = "databaseType", description = "SchemaSpy database type")
  private String databaseType;

  @Option(names = "-db", paramLabel = "dbName", description = "Database name")
  private String database;

  @Option(names = "-host", paramLabel = "hostName", description = "Database host")
  private String host = "localhost";

  @Option(names = "-port", paramLabel = "portNumber", description = "Database port")
  private Integer port;

  @Option(names = "-u", paramLabel = "user", description = "Database user")
  private String user;

  @Option(
      names = {"-p", "-password"},
      paramLabel = "password",
      description = "Database password")
  private String password;

  @Option(names = "-pfp", description = "Prompt for password if not provided")
  private boolean promptForPassword;

  @Option(names = "-sso", description = "Single sign-on mode")
  private boolean sso;

  @Option(
      names = "-connprops",
      paramLabel = "filePathOrKeyValue",
      description = "Connection properties file or key/value list")
  private String connectionProperties;

  @Option(names = "-dp", paramLabel = "pathToDrivers", description = "Driver path(s)")
  private String driverPath;

  @Option(names = "-loadjars", description = "Load sibling jars for a single -dp entry")
  private boolean loadJars;

  @Option(names = "-cat", paramLabel = "catalog", description = "Catalog filter")
  private String catalog;

  @Option(names = "-s", paramLabel = "schema", description = "Schema filter")
  private String schema;

  @Option(names = "-schemas", paramLabel = "schemaList", description = "Comma-separated schemas")
  private String schemas;

  @Option(names = "-all", description = "Analyze all schemas")
  private boolean allSchemas;

  @Option(names = "-schemaSpec", paramLabel = "schemaRegex", description = "Schema regex filter")
  private String schemaRegex;

  @Option(names = "-dbthreads", paramLabel = "number", description = "Database read thread count")
  private Integer databaseThreads = 15;

  @Option(names = "-norows", description = "Skip row counts")
  private boolean noRows;

  @Option(names = "-noviews", description = "Skip views")
  private boolean noViews;

  @Option(names = "-i", paramLabel = "includeTableRegex", description = "Include table regex")
  private String includeTableRegex;

  @Option(names = "-I", paramLabel = "excludeTableRegex", description = "Exclude table regex")
  private String excludeTableRegex;

  @Option(names = "-meta", paramLabel = "pathToFolder", description = "SchemaMeta folder path")
  private String metaPath;

  @Option(names = "-nohtml", description = "Compatibility no-op for OKF output")
  private boolean noHtml;

  @Option(names = "-noviz", description = "Compatibility no-op for OKF output")
  private boolean noViz;

  @Option(names = "-loglevel", paramLabel = "level", description = "Compatibility log level")
  private String logLevel;

  @Option(names = "-debug", description = "Enable debug-level logging")
  private boolean debug;

  @Option(names = "--locale", paramLabel = "bcp47", description = "Locale tag")
  private String locale;

  @Spec private CommandSpec spec;

  @Override
  public void run() {
    if (dbhelp) {
      spec.commandLine().getOut().println("Supported database types:");
      spec.commandLine().getOut().println(SchemaSpyDatabaseType.supportedDatabaseTypes());
      return;
    }

    validateOptions();
    final int exitCode = runSchemaCrawler();
    if (exitCode != 0) {
      throw new ExecutionRuntimeException("SchemaCrawler Scribe execution failed");
    }
  }

  String toEquivalentCommand() {
    return String.join(" ", toSchemaCrawlerArgs(true));
  }

  Level toJulLevel() {
    if (debug) {
      return Level.FINE;
    }
    if (logLevel == null || logLevel.isBlank()) {
      return Level.INFO;
    }
    final String normalizedLogLevel = logLevel.trim().toUpperCase(Locale.ROOT);
    return switch (normalizedLogLevel) {
      case "TRACE", "FINEST" -> Level.FINEST;
      case "DEBUG", "FINE" -> Level.FINE;
      case "WARN", "WARNING" -> Level.WARNING;
      case "ERROR", "SEVERE" -> Level.SEVERE;
      case "INFO" -> Level.INFO;
      default -> Level.INFO;
    };
  }

  private SchemaSpyDatabaseType parseDatabaseType() {
    return SchemaSpyDatabaseType.fromType(databaseType)
        .orElseThrow(
            () ->
                new ExecutionRuntimeException(
                    "Unknown database type <%s>. Supported database types: %s"
                        .formatted(databaseType, SchemaSpyDatabaseType.supportedDatabaseTypes())));
  }

  private int runSchemaCrawler() {
    return schemacrawler.tools.commandline.SchemaCrawlerCommandLine.execute(
        toSchemaCrawlerArgs(false).toArray(String[]::new));
  }

  private String toCommandLineLogLevel() {
    final Level logLevelForExecution = toJulLevel();
    if (Level.SEVERE.equals(logLevelForExecution)) {
      return "SEVERE";
    }
    if (Level.WARNING.equals(logLevelForExecution)) {
      return "WARNING";
    }
    if (Level.FINE.equals(logLevelForExecution)) {
      return "FINE";
    } else if (Level.FINEST.equals(logLevelForExecution)) {
      return "FINEST";
    } else if (Level.INFO.equals(logLevelForExecution)) {
    }
    return "INFO";
  }

  private List<String> toSchemaCrawlerArgs(final boolean maskPassword) {
    final SchemaSpyDatabaseType type = parseDatabaseType();
    final List<String> args = new ArrayList<>();
    args.add("--log-level");
    args.add(toCommandLineLogLevel());
    if (configFile != null && !configFile.isBlank()) {
      args.add("--config-file");
      args.add(configFile);
    }

    if (type.isUrlFallback()) {
      args.add("--url");
    } else {
      args.add("--server");
      args.add(type.getSchemaCrawlerServer().orElseThrow());
      args.add("--host");
      args.add(host);
      if (port != null) {
        args.add("--port");
        args.add(String.valueOf(port));
      }
      args.add("--database");
    }
    args.add(database);

    if (!sso && user != null && !user.isBlank()) {
      args.add("--user");
      args.add(user);
    }
    if (!sso && password != null && !password.isBlank()) {
      args.add("--password");
      args.add(maskPassword ? "******" : password);
    }
    args.add("--command");
    args.add("scribe");
    args.add("--info-level");
    args.add("maximum");
    args.add("--output-format");
    args.add("okf");
    args.add("--output-file");
    args.add(outputPath);
    if (locale != null && !locale.isBlank()) {
      args.add("--language");
      args.add(locale);
    }

    return args;
  }

  private void validateOptions() {
    if (databaseType == null || databaseType.isBlank()) {
      throw new ParameterException(spec.commandLine(), "Missing required option: -t");
    }
    parseDatabaseType();
    if (database == null || database.isBlank()) {
      throw new ParameterException(spec.commandLine(), "Missing required option: -db");
    }
    if (!sso && (user == null || user.isBlank())) {
      throw new ParameterException(
          spec.commandLine(), "Missing required option: -u (unless -sso is used)");
    }
    if (promptForPassword && !sso && (password == null || password.isBlank())) {
      throw new ParameterException(
          spec.commandLine(), "Password prompt mode (-pfp) is not implemented in stage 1");
    }
    if (metaPath != null && !metaPath.isBlank()) {
      throw new ParameterException(spec.commandLine(), "Unsupported option in stage 1: -meta");
    }
  }
}
