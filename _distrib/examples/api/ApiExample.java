import java.util.Properties;

import javax.sql.DataSource;

import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

public final class ApiExample
{

  public static void main(final String[] args)
    throws Exception
  {
    // Create a database connection
    final DataSource dataSource = makeDataSource();

    // Get the schema definition
    final Schema schema = SchemaCrawler.getSchema(dataSource,
                                                  SchemaInfoLevel.BASIC,
                                                  new SchemaCrawlerOptions());

    final Table[] tables = schema.getTables();
    for (int i = 0; i < tables.length; i++)
    {
      final Table table = tables[i];
      System.out.println(table);
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
