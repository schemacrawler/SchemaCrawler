import static us.fatehi.commandlineparser.CommandLineUtility.applyApplicationLogLevel;
import static us.fatehi.commandlineparser.CommandLineUtility.logSystemProperties;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.sql.DataSource;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.ExcludeAll;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.utility.SchemaCrawlerUtility;

public final class ApiExample
{

  public static void main(final String[] args)
    throws Exception
  {
    // Turn application logging on by applying the correct log level
    applyApplicationLogLevel(Level.OFF);
    // Log system properties and classpath
    logSystemProperties();

    // Create the options
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    // Set what details are required in the schema - this affects the
    // time taken to crawl the schema
    options.setSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
    options.setRoutineInclusionRule(new ExcludeAll());
    options
      .setSchemaInclusionRule(new RegularExpressionInclusionRule("PUBLIC.BOOKS"));

    // Get the schema definition
    final Catalog catalog = SchemaCrawlerUtility.getCatalog(getConnection(),
                                                            options);

    for (final Schema schema: catalog.getSchemas())
    {
      System.out.println(schema);
      for (final Table table: catalog.getTables(schema))
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

        for (final Column column: table.getColumns())
        {
          System.out.println("     o--> " + column + " ("
                             + column.getColumnDataType() + ")");
        }
      }
    }

  }

  private static Connection getConnection()
    throws SchemaCrawlerException, SQLException
  {
    final DataSource dataSource = new DatabaseConnectionOptions("jdbc:hsqldb:hsql://localhost:9001/schemacrawler");
    return dataSource.getConnection("sa", "");
  }

}
