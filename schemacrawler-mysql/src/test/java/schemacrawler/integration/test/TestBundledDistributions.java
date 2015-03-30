package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class TestBundledDistributions
{

  @Test
  public void testInformationSchema_mysql()
    throws Exception
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    final DatabaseConnector databaseSystemIdentifier = registry
      .lookupDatabaseSystemIdentifier("mysql");
    final Config config = databaseSystemIdentifier.getDatabaseSystemConnector()
      .getConfig();
    final SchemaCrawlerOptions options = new SchemaCrawlerOptionsBuilder()
      .setFromConfig(config).toOptions();
    assertEquals(4, options.getInformationSchemaViews().size());
  }

  @Test
  public void testPlugin_mysql()
    throws Exception
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    assertTrue(registry.hasDatabaseSystemIdentifier("mysql"));
  }

}
