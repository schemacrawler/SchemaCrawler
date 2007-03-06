import java.util.Properties;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import schemacrawler.crawl.InclusionRule;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

public final class ApiExample
{

  public static void main(final String[] args)
    throws Exception
  {
    // Create a database connection
    final DataSource dataSource = makeDataSource();

    // Create the options
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    /*
    options.setShowStoredProcedures(false);
    options
      .setTableInclusionRule(new InclusionRule("C.*",
                                               InclusionRule.EXCLUDE_NONE));
    options
      .setColumnInclusionRule(new InclusionRule(InclusionRule.INCLUDE_ALL_PATTERN,
                                               Pattern.compile(".*ID")));
    options.setTableTypes("TABLE");
    options.setAlphabeticalSortForTableColumns(true);
    */

    // Get the schema definition
    final Schema schema = SchemaCrawler.getSchema(dataSource,
                                                  SchemaInfoLevel.BASIC,
                                                  options);

    final Table[] tables = schema.getTables();
    for (int i = 0; i < tables.length; i++)
    {
      final Table table = tables[i];
      System.out.print(table);
      if (table instanceof View)
      {
        System.out.println(" (view)");
      }
      else
      {
        System.out.println();
      }

      final Column[] columns = table.getColumns();
      for (int j = 0; j < columns.length; j++)
      {
        final Column column = columns[j];
        System.out.println("-- " + column);
      }
    }

  }

  private static DataSource makeDataSource()
    throws PropertiesDataSourceException
  {
    final String datasourceName = "schemacrawler";

    final Properties connectionProperties = new Properties();
    connectionProperties.setProperty(datasourceName + ".driver",
                                     "org.hsqldb.jdbcDriver");
    connectionProperties
      .setProperty(datasourceName + ".url",
                   "jdbc:hsqldb:hsql://localhost:9001/schemacrawler");
    connectionProperties.setProperty(datasourceName + ".user", "sa");
    connectionProperties.setProperty(datasourceName + ".password", "");

    return new PropertiesDataSource(connectionProperties, datasourceName);
  }

}
