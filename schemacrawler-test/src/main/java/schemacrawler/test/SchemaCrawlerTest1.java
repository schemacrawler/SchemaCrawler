package schemacrawler.test;


import java.util.Properties;

import javax.sql.DataSource;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.PropertiesDataSource;
import schemacrawler.utility.SchemaCrawlerUtility;

public class SchemaCrawlerTest1
{

  public static void main(final String[] args)
    throws Exception
  {
    final DataSource dataSource = makeDataSource();

    final Config config = new Config();
    config.put("schemacrawler.table_types", "TABLE");
    config.put("schemacrawler.show_stored_procedures", "false");
    config.put("schemacrawler.table.pattern.include", ".*");
    config.put("schemacrawler.table.pattern.exclude", "");

    final SchemaCrawlerOptions options = new SchemaCrawlerOptions(config);
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
    final Properties connectionProperties = new Properties();
    connectionProperties.setProperty("driver", "org.hsqldb.jdbcDriver");
    connectionProperties
      .setProperty("url", "jdbc:hsqldb:hsql://localhost:9001/schemacrawler");
    connectionProperties.setProperty("user", "sa");
    connectionProperties.setProperty("password", "");

    return new PropertiesDataSource(connectionProperties);
  }

}
