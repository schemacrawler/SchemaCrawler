import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnectionSource;
import schemacrawler.tools.databaseconnector.SingleUseUserCredentials;
import schemacrawler.utility.SchemaCrawlerUtility;

public final class ApiExample
{

  private static Connection getConnection()
  {
    final String connectionUrl =
      "jdbc:hsqldb:hsql://localhost:9001/schemacrawler";
    final DatabaseConnectionSource dataSource =
      new DatabaseConnectionSource(connectionUrl);
    dataSource.setUserCredentials(new SingleUseUserCredentials("sa", ""));
    return dataSource.get();
  }

  public static void main(final String[] args)
    throws Exception
  {

    // Create the options
    final SchemaCrawlerOptionsBuilder optionsBuilder =
      SchemaCrawlerOptionsBuilder.builder()
        // Set what details are required in the schema - this affects the
        // time taken to crawl the schema
        .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard())
        .includeSchemas(new RegularExpressionInclusionRule("PUBLIC.BOOKS"))
        .includeTables(tableFullName -> !tableFullName.contains("ΒΙΒΛΊΑ"));
    final SchemaCrawlerOptions options = optionsBuilder.toOptions();

    // Get the schema definition
    final Catalog catalog =
      SchemaCrawlerUtility.getCatalog(getConnection(), options);

    for (final Schema schema : catalog.getSchemas())
    {
      System.out.println(schema);
      for (final Table table : catalog.getTables(schema))
      {
        System.out.print("o--> " + table);
        if (table instanceof View)
        {
          System.out.println(" (VIEW)");
        }
        else
        {
          System.out.println();
        }

        for (final Column column : table.getColumns())
        {
          System.out.println(
            "     o--> " + column + " (" + column.getColumnDataType() + ")");
        }
      }
    }

  }

}
