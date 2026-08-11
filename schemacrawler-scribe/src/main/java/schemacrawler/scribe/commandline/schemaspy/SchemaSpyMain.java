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
import java.util.stream.Collectors;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import schemacrawler.schemacrawler.Version;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

@Command(
    name = "schemaspy",
    mixinStandardHelpOptions = false,
    versionProvider = SchemaSpyMain.SchemaSpyVersionProvider.class,
    sortOptions = false,
    description = "SchemaSpy compatibility wrapper for SchemaCrawler Scribe (OKF output).")
public final class SchemaSpyMain implements Runnable {

  private record DatabaseTypeResolution(String server, boolean isUrlFallback) {}

  public static final class SchemaSpyVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() {
      return new String[] {
        "SchemaSpy 7.x adapter for generating OKF bundles.", "", String.valueOf(Version.about())
      };
    }
  }

  public static int execute(final String... args) {
    requireNonNull(args, "No command-line arguments provided");
    try {
      final SchemaSpyMain command = new SchemaSpyMain();
      final CommandLine commandLine = new CommandLine(command);
      commandLine.setExecutionExceptionHandler(
          (exception, cmd, parseResult) -> {
            cmd.getErr().println(exception.getMessage());
            return 1;
          });
      return commandLine.execute(resolveEffectiveArgs(args));
    } catch (final RuntimeException e) {
      System.err.println(e.getMessage());
      return 1;
    }
  }

  public static void main(final String[] args) {
    System.exit(execute(args));
  }

  static String[] resolveEffectiveArgs(final String... args) {
    return SchemaSpyPropertiesResolver.resolveEffectiveArgs(args);
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
      spec.commandLine().getOut().println(getAllSupportedDatabaseTypes());
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

  private DatabaseTypeResolution resolveDatabaseType(final String typeIdentifier) {
    requireNonNull(typeIdentifier, "No database type provided");

    // First, try to resolve as a SchemaSpy database type
    final var schemaSpyType = SchemaSpyDatabaseType.fromType(typeIdentifier);
    if (schemaSpyType.isPresent()) {
      final SchemaSpyDatabaseType type = schemaSpyType.get();
      if (type.isUrlFallback()) {
        return new DatabaseTypeResolution(null, true);
      }
      return new DatabaseTypeResolution(type.getSchemaCrawlerServer().orElseThrow(), false);
    }

    // Second, try to resolve as a SchemaCrawler server identifier
    final DatabaseConnectorRegistry registry = DatabaseConnectorRegistry.getRegistry();
    if (registry.hasDatabaseSystemIdentifier(typeIdentifier)) {
      return new DatabaseTypeResolution(typeIdentifier, false);
    }

    // Not found in either registry
    throw new ExecutionRuntimeException(
        "Unknown database type <%s>. Supported database types:\n%s"
            .formatted(typeIdentifier, getAllSupportedDatabaseTypes()));
  }

  private String getAllSupportedDatabaseTypes() {
    final List<String> allTypes = new ArrayList<>();

    // Add SchemaSpy types
    allTypes.add("SchemaSpy database types:");
    allTypes.add("  " + SchemaSpyDatabaseType.supportedDatabaseTypes());

    // Add SchemaCrawler servers
    final DatabaseConnectorRegistry registry = DatabaseConnectorRegistry.getRegistry();
    final var serverTypes =
        registry.getDatabaseServerTypes().stream()
            .map(st -> st.getDatabaseSystemIdentifier())
            .collect(Collectors.toList());
    if (!serverTypes.isEmpty()) {
      allTypes.add("\nSchemaCrawler database servers:");
      allTypes.add("  " + String.join(", ", serverTypes));
    }

    return String.join("\n", allTypes);
  }

  private int runSchemaCrawler() {
    return SchemaCrawlerCommandLine.execute(toSchemaCrawlerArgs(false).toArray(String[]::new));
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
    }
    if (Level.FINEST.equals(logLevelForExecution)) {
      return "FINEST";
    } else if (Level.INFO.equals(logLevelForExecution)) {
    }
    return "INFO";
  }

  private List<String> toSchemaCrawlerArgs(final boolean maskPassword) {
    final DatabaseTypeResolution resolution = resolveDatabaseType(databaseType);
    final List<String> args = new ArrayList<>();
    args.add("--log-level");
    args.add(toCommandLineLogLevel());
    if (configFile != null && !configFile.isBlank()) {
      args.add("--config-file");
      args.add(configFile);
    }

    if (resolution.isUrlFallback()) {
      args.add("--url");
    } else {
      args.add("--server");
      args.add(resolution.server());
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

    // Phase 1: Connection properties
    if (connectionProperties != null && !connectionProperties.isBlank()) {
      args.add("--jdbc-properties");
      args.add(connectionProperties);
    }

    // Phase 2: Catalog filtering
    if (catalog != null && !catalog.isBlank()) {
      args.add("--catalogs");
      args.add(catalog);
    }

    // Phase 3: Schema filtering with priority
    final String schemaFilter = resolveSchemaFilter();
    if (schemaFilter != null) {
      args.add("--schemas");
      args.add(schemaFilter);
    }

    // Phase 4: Table filtering with include/exclude logic
    final String tableFilter = resolveTableFilter();
    if (tableFilter != null) {
      args.add("--tables");
      args.add(tableFilter);
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

    // Phase 5: Row count behavior - load by default unless -norows
    if (!noRows) {
      args.add("--load-row-counts");
      args.add("true");
    }

    return args;
  }

  private void validateOptions() {
    if (databaseType == null || databaseType.isBlank()) {
      throw new ParameterException(spec.commandLine(), "Missing required option: -t");
    }
    resolveDatabaseType(databaseType);
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

  private String resolveSchemaFilter() {
    // Priority order: -schemaSpec > -schemas > -s
    if (schemaRegex != null && !schemaRegex.isBlank()) {
      return schemaRegex;
    }
    if (schemas != null && !schemas.isBlank()) {
      // Convert comma-separated schema list to regex: "A,B,C" -> ".*\\.(A|B|C)"
      return convertSchemasListToRegex(schemas);
    }
    if (schema != null && !schema.isBlank()) {
      return schema;
    }
    return null;
  }

  private String convertSchemasListToRegex(final String schemaList) {
    final String[] schemaArray = schemaList.split(",");
    final List<String> escapedSchemas = new ArrayList<>();
    for (final String s : schemaArray) {
      final String trimmed = s.trim();
      if (!trimmed.isEmpty()) {
        escapedSchemas.add("\\Q" + trimmed + "\\E");
      }
    }
    if (escapedSchemas.isEmpty()) {
      return null;
    }
    // Create pattern: ".*\\.(SCHEMA1|SCHEMA2|...)"
    return ".*\\.(" + String.join("|", escapedSchemas) + ")";
  }

  private String resolveTableFilter() {
    final boolean hasInclude = includeTableRegex != null && !includeTableRegex.isBlank();
    final boolean hasExclude = excludeTableRegex != null && !excludeTableRegex.isBlank();

    if (!hasInclude && !hasExclude) {
      return null;
    }

    if (hasInclude && !hasExclude) {
      // Only include pattern
      return includeTableRegex;
    }

    if (!hasInclude && hasExclude) {
      // Only exclude pattern - match all NOT matching exclude
      return "(?!" + excludeTableRegex + ").*";
    }

    // Both include and exclude: include AND NOT exclude
    // Pattern: matches include but NOT exclude
    return "(?=.*" + includeTableRegex + ")(?!" + excludeTableRegex + ").*";
  }
}
