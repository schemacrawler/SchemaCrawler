/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static us.fatehi.utility.Utility.isBlank;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import schemacrawler.schemacrawler.Version;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;

@Command(
    name = "schemaspy",
    mixinStandardHelpOptions = false,
    versionProvider = SchemaSpyCommand.VersionProvider.class,
    sortOptions = false,
    description = "SchemaSpy compatibility wrapper for SchemaCrawler Scribe (OKF output).")
public final class SchemaSpyCommand implements Runnable {

  public static final class VersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() {
      return new String[] {
        "SchemaSpy 7.x adapter for generating OKF bundles.", "", String.valueOf(Version.about())
      };
    }
  }

  @Option(
      names = {"-dbhelp", "-dbHelp"},
      description = "Print supported SchemaSpy database types for -t and exit")
  private boolean dbhelp;

  @Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "Show this help message and exit.")
  private boolean helpRequested;

  @Option(
      names = {"-V", "--version"},
      versionHelp = true,
      description =
          "Display SchemaSpy adapter info and SchemaCrawler version and system information")
  private boolean versionRequested;

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
      spec.commandLine().getOut().println(buildArgsBuilder().getAllSupportedDatabaseTypes());
      return;
    }

    validateOptions();
    final int exitCode = runSchemaCrawler();
    if (exitCode != 0) {
      throw new ExecutionRuntimeException("SchemaCrawler Scribe execution failed");
    }
  }

  String toEquivalentCommand() {
    return String.join(" ", buildArgsBuilder().toArgs(true));
  }

  Level toJulLevel() {
    if (debug) {
      return Level.FINE;
    }
    if (isBlank(logLevel)) {
      return Level.INFO;
    }
    final String normalizedLogLevel = logLevel.trim().toUpperCase(Locale.ROOT);
    return switch (normalizedLogLevel) {
      case "TRACE", "FINEST" -> Level.FINEST;
      case "DEBUG", "FINE" -> Level.FINE;
      case "WARN", "WARNING" -> Level.WARNING;
      case "ERROR", "SEVERE" -> Level.SEVERE;
      default -> Level.INFO;
    };
  }

  private ArgsBuilder buildArgsBuilder() {
    return new ArgsBuilder(
        new LogArgsMapper.LogArgs(logLevel, debug),
        new ConnectArgsMapper.ConnectArgs(
            databaseType, database, host, port, sso, user, password, connectionProperties),
        new LimitArgsMapper.LimitArgs(
            catalog, schema, schemas, schemaRegex, includeTableRegex, excludeTableRegex),
        new LoadArgsMapper.LoadArgs(noRows),
        new ExecuteArgsMapper.ExecuteArgs(outputPath, locale));
  }

  private int runSchemaCrawler() {
    final List<String> args = buildArgsBuilder().toArgs(false);
    return SchemaCrawlerCommandLine.execute(args.toArray(String[]::new));
  }

  private void validateOptions() {
    if (isBlank(databaseType)) {
      throw new ParameterException(spec.commandLine(), "Missing required option: -t");
    }
    buildArgsBuilder().validateDatabaseType(databaseType);
    if (isBlank(database)) {
      throw new ParameterException(spec.commandLine(), "Missing required option: -db");
    }
    if (!sso && isBlank(user)) {
      throw new ParameterException(
          spec.commandLine(), "Missing required option: -u (unless -sso is used)");
    }
    if (promptForPassword && !sso && isBlank(password)) {
      throw new ParameterException(
          spec.commandLine(), "Password prompt mode (-pfp) is not implemented in stage 1");
    }
    if (!isBlank(metaPath)) {
      throw new ParameterException(spec.commandLine(), "Unsupported option in stage 1: -meta");
    }
  }
}
