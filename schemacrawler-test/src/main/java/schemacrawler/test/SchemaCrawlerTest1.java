package schemacrawler.test;


import java.util.Properties;

import javax.sql.DataSource;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;
import schemacrawler.utility.datasource.PropertiesDataSource;

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
    options.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    Catalog catalog = SchemaCrawlerUtility.getDatabase(dataSource
                                                         .getConnection(),
                                                       options).getCatalogs()[0];

    Schema[] schemas = catalog.getSchemas();
    for (Schema schema: schemas)
    {
      Table[] tables = schema.getTables();
      for (int i = 0; i < tables.length; i++)
      {
        Table table = tables[i];
        System.out.println(table);
        Column[] columns = table.getColumns();
        for (int j = 0; j < columns.length; j++)
        {
          Column column = columns[j];
          System.out.println("-- " + column);
        }
      }
    }

  }

  private static DataSource makeDataSource()
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
