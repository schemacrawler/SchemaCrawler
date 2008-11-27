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

public class SchemaCrawlerMySQLTest1
{

  public static void main(final String[] args)
    throws Exception
  {
    final DataSource dataSource = makeDataSource();

    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setSchemaInfoLevel(SchemaInfoLevel.verbose());

    Catalog catalog = SchemaCrawlerUtility.getCatalog(dataSource
      .getConnection(), options);

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
                                     "com.mysql.jdbc.Driver");
    connectionProperties
      .setProperty(datasourceName + ".url",
                   "jdbc:mysql://localhost:3306/schemacrawler;useInformationSchema=true");
    connectionProperties.setProperty(datasourceName + ".user", "root");
    connectionProperties.setProperty(datasourceName + ".password", "");

    return new PropertiesDataSource(connectionProperties, datasourceName);
  }

}
