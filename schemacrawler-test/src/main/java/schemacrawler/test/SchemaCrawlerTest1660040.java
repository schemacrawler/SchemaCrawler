package schemacrawler.test;


import java.util.Properties;

import javax.sql.DataSource;

import schemacrawler.crawl.Config;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.utility.datasource.PropertiesDataSource;
import schemacrawler.utility.datasource.PropertiesDataSourceException;

public class SchemaCrawlerTest1660040
{

  public static void main(final String[] args)
    throws Exception
  {
    final DataSource dataSource = makeDataSource();

    final Config config = Config
      .load("schemacrawler.config.1660040.properties", "");

    // Get the schema definition
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions(config);
    final Schema schema = SchemaCrawler.getSchema(dataSource, options);

    final Table[] tables = schema.getTables();
    for (final Table table: tables)
    {
      System.out.println(table);
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
