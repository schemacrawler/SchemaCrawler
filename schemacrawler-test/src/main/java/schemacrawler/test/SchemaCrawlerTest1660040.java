package schemacrawler.test;


import java.util.Properties;

import javax.sql.DataSource;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.main.Config;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

public class SchemaCrawlerTest1660040
{

  public static void main(String[] args)
    throws Exception
  {
    DataSource dataSource = makeDataSource();

    Config config = Config.load("schemacrawler.config.1660040.properties", "");
    
    // Get the schema definition
    SchemaCrawlerOptions options = new SchemaCrawlerOptions(config);
    final Schema schema = SchemaCrawler.getSchema(dataSource,
                                                  SchemaInfoLevel.MAXIMUM,
                                                  options);

    final Table[] tables = schema.getTables();
    for (int i = 0; i < tables.length; i++)
    {
      final Table table = tables[i];
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
