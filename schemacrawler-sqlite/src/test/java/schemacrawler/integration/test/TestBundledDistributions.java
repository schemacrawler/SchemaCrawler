package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class TestBundledDistributions
{

  @Test
  public void testInformationSchema_sqlite()
    throws Exception
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    final DatabaseConnector databaseSystemIdentifier = registry
      .lookupDatabaseSystemIdentifier("sqlite");
    final Config config = databaseSystemIdentifier.getDatabaseSystemConnector()
      .getConfig();
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions(config);
    assertEquals(2, options.getInformationSchemaViews().size());
  }

  @Test
  public void testPlugin_sqlite()
    throws Exception
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    assertTrue(registry.hasDatabaseSystemIdentifier("sqlite"));
  }

}
