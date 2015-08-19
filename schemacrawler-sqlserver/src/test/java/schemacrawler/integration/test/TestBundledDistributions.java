package schemacrawler.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class TestBundledDistributions
{

  @Test
  public void testInformationSchema_sqlserver()
    throws Exception
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    final DatabaseConnector databaseSystemIdentifier = registry
      .lookupDatabaseSystemIdentifier("sqlserver");
    assertEquals(8,
                 databaseSystemIdentifier.getDatabaseSystemConnector()
                   .getDatabaseSpecificOverrideOptionsBuilder().toOptions()
                   .getInformationSchemaViews().size());
  }

  @Test
  public void testPlugin_sqlserver()
    throws Exception
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    assertTrue(registry.hasDatabaseSystemIdentifier("sqlserver"));
  }

}
