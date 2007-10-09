package schemacrawler.test;


import java.util.Arrays;
import java.util.Properties;

import javax.sql.DataSource;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

public class SchemaCrawlerTest1
{

  public static void main(final String[] args)
    throws Exception
  {
    final DataSource dataSource = makeDataSource();

    final Properties properties = new Properties();
    properties.setProperty("schemacrawler.table_types", "TABLE");
    properties.setProperty("schemacrawler.show_stored_procedures", "false");
    properties.setProperty("schemacrawler.table.pattern.include", ".*");
    properties.setProperty("schemacrawler.table.pattern.exclude", "");

    final SchemaCrawlerOptions options = new SchemaCrawlerOptions(properties);

    final Schema schema = SchemaCrawler.getSchema(dataSource,
                                                  SchemaInfoLevel.basic,
                                                  options);
    final Table[] tableArray = schema.getTables();

    System.out.println(Arrays.asList(tableArray));
    System.out.println(Arrays.asList(schema.getProcedures()));

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
