package schemacrawler.integration.test;


import java.sql.Connection;
import java.util.Collection;

import javax.sql.DataSource;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.utility.SchemaCrawlerUtility;

public class TestSqlServerCatalog
{

  public static void main(final String[] args)
    throws Exception
  {
    final DataSource dataSource = new DatabaseConnectionOptions("jdbc:jtds:sqlserver://scsqlserver.cdf972bn8znp.us-east-1.rds.amazonaws.com:1433/SCHEMACRAWLER");
    final Connection connection = dataSource.getConnection("schemacrawler",
                                                           "schemacrawler");

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionInclusionRule("SCHEMACRAWLER.dbo"));
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

    final Catalog catalog = SchemaCrawlerUtility
      .getCatalog(connection, schemaCrawlerOptions);

    final Collection<Routine> routines = catalog.getRoutines();
    System.out.println(routines);
  }

}
