package com.example;

import static us.fatehi.utility.Utility.isBlank;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.logging.Level;

import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSources;
import schemacrawler.tools.databaseconnector.SingleUseUserCredentials;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.LoggingConfig;

public final class ExecutableExample {

  public static void main(final String[] args) throws Exception {

    // Set log level
    new LoggingConfig(Level.OFF);

    // Create the options
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("PUBLIC.BOOKS"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder()
            // Set what details are required in the schema - this affects the
            // time taken to crawl the schema
            .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
    final SchemaCrawlerOptions options =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final Path outputFile = getOutputFile(args);
    final OutputOptions outputOptions =
        OutputOptionsBuilder.newOutputOptions(TextOutputFormat.html, outputFile);
    final String command = "schema";

    try (final Connection connection = getConnection()) {
      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
      executable.setSchemaCrawlerOptions(options);
      executable.setOutputOptions(outputOptions);
      executable.setConnection(connection);
      executable.execute();
    }

    System.out.println("Created output file, " + outputFile);
  }

  private static Connection getConnection() {
    final String connectionUrl = "jdbc:hsqldb:hsql://localhost:9001/schemacrawler";
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSources.newDatabaseConnectionSource(
            connectionUrl, new SingleUseUserCredentials("sa", ""));
    return dataSource.get();
  }

  private static Path getOutputFile(final String[] args) {
    final String outputfile;
    if (args != null && args.length > 0 && !isBlank(args[0])) {
      outputfile = args[0];
    } else {
      outputfile = "./schemacrawler_output.html";
    }
    final Path outputFile = Paths.get(outputfile).toAbsolutePath().normalize();
    return outputFile;
  }
}
