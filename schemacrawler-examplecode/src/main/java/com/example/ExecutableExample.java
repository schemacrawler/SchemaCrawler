/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.example;

import static us.fatehi.utility.Utility.isBlank;

import java.nio.file.Path;
import java.util.logging.Level;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

public final class ExecutableExample {

  public static void main(final String[] args) throws Exception {

    // Set log level
    new LoggingConfig(Level.OFF);

    // Create the options
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().includeTables(new RegularExpressionInclusionRule(".*_PK"));
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

    try (final DatabaseConnectionSource dataSource = getDatabaseConnectionSource()) {
      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
      executable.setSchemaCrawlerOptions(options);
      executable.setOutputOptions(outputOptions);
      executable.setDataSource(dataSource);
      executable.execute();
    }

    System.out.println("Created output file, " + outputFile);
  }

  private static DatabaseConnectionSource getDatabaseConnectionSource() {
    final String connectionUrl = "jdbc:sqlite::resource:test.db";
    return DatabaseConnectionSources.newDatabaseConnectionSource(
        connectionUrl, new MultiUseUserCredentials("", ""));
  }

  private static Path getOutputFile(final String[] args) {
    final String outputfile;
    if (args != null && args.length > 0 && !isBlank(args[0])) {
      outputfile = args[0];
    } else {
      outputfile = "./schemacrawler_output.html";
    }
    return Path.of(outputfile).toAbsolutePath().normalize();
  }
}
