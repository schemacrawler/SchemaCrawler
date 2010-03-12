package com.example.sctest;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.SchemaCrawlerUtility;

public final class SCTestProgram
{

  public static void main(final String[] args) throws Exception
  {
    // Create a database connection
    final DatabaseConnectionOptions connectionOptions = new DatabaseConnectionOptions(
        "org.hsqldb.jdbcDriver",
        "jdbc:hsqldb:hsql://localhost:9001/schemacrawler");
    connectionOptions.setUser("sa");
    connectionOptions.setPassword("");

    // Create the options
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setShowStoredProcedures(false);

    // Get the schema definition
    final Database database = SchemaCrawlerUtility.getDatabase(connectionOptions.createConnection(), options);

    for (final Schema schema : database.getSchemas()) {
      System.out.println(schema);
      for (final Table table : schema.getTables()) {
        System.out.print("o--> " + table);
        if (table instanceof View) {
          System.out.println(" (VIEW)");
        }
        else {
          System.out.println();
        }

        for (final Column column : table.getColumns()) {
          System.out.println("     o--> " + column + " (" + column.getType() + ")");
        }
      }
    }

  }

}
