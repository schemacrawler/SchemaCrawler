package com.example;

import java.sql.Connection;
import java.util.logging.Level;

import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.SingleUseUserCredentials;

public final class ApiExample {

  public static void main(final String[] args) throws Exception {

    // Set log level
    new LoggingConfig(Level.OFF);

    // Create the options
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("PUBLIC.BOOKS"))
            .includeTables(tableFullName -> !tableFullName.contains("ΒΙΒΛΊΑ"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder()
            // Set what details are required in the schema - this affects the
            // time taken to crawl the schema
            .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
    final SchemaCrawlerOptions options =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    // Get the schema definition
    final Catalog catalog;
    try (final Connection connection = getConnection()) {
      catalog = SchemaCrawlerUtility.getCatalog(connection, options);
    }

    for (final Schema schema : catalog.getSchemas()) {
      System.out.println(schema);
      for (final Table table : catalog.getTables(schema)) {
        System.out.print("o--> " + table);
        if (table instanceof View) {
          System.out.println(" (VIEW)");
        } else {
          System.out.println();
        }

        for (final Column column : table.getColumns()) {
          System.out.printf("     o--> %s (%s)%n", column, column.getType());
        }
      }
    }
  }

  private static Connection getConnection() {
    final String connectionUrl = "jdbc:hsqldb:hsql://localhost:9001/schemacrawler";
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSources.newDatabaseConnectionSource(
            connectionUrl, new SingleUseUserCredentials("sa", ""));
    return dataSource.get();
  }
}
