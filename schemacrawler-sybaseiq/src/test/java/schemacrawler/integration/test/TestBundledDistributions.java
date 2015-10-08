package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class TestBundledDistributions
{

  @Test
  public void testInformationSchema_sybaseiq()
    throws Exception
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    final DatabaseConnector dbConnector = registry
      .lookupDatabaseSystemIdentifier("sybaseiq");
    assertEquals(1, dbConnector.getDatabaseSpecificOverrideOptionsBuilder()
      .toOptions().getInformationSchemaViews().size());
  }

  @Test
  public void testPlugin_sybaseiq()
    throws Exception
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    assertTrue(registry.hasDatabaseSystemIdentifier("sybaseiq"));
  }

}
